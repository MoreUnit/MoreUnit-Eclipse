package org.moreunit.core.matching;

import static java.util.Collections.reverse;
import static java.util.Collections.sort;
import static java.util.regex.Matcher.quoteReplacement;
import static java.util.regex.Pattern.compile;
import static java.util.regex.Pattern.quote;
import static org.moreunit.core.util.Preconditions.checkArgument;
import static org.moreunit.core.util.Strings.countOccurrences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.moreunit.core.resources.Path;
import org.moreunit.core.util.Strings;

public class TestFolderPathPattern
{
    public static final String SRC_PROJECT_VARIABLE = "${srcProject}";

    private static final int MAX_GROUPS = 9;

    private static final Pattern SRC_PATH_VALIDATOR;
    static
    {
        // stars may be captured or not
        String optionalStar = "(?:\\(\\*\\)|\\*)?";
        String twoStars = "(?:\\(\\*{2}\\)|\\*{2})";

        String nonStars = "[^\\*\\(\\)]+";

        String simpleVariableSegment = "(?:\\(\\*\\)|\\*)";
        String variableSegmentPart = nonStars + "(" + optionalStar + nonStars + ")*";
        String segmentWithVariableStart = optionalStar + variableSegmentPart;
        String segmentWithVariableMiddle = nonStars + optionalStar + nonStars;
        String segmentWithVariableEnd = variableSegmentPart + optionalStar;

        SRC_PATH_VALIDATOR = compile("^/?" + quote(SRC_PROJECT_VARIABLE) + "(?:/(?:" + twoStars + "|" + nonStars + "|" + simpleVariableSegment + "|" + segmentWithVariableStart + "|" + segmentWithVariableMiddle + "|" + segmentWithVariableEnd + "))*" + "/?$");
    }

    private static final Pattern TEST_PATH_VALIDATOR;
    static
    {
        TEST_PATH_VALIDATOR = compile("^/?[^/\\*\\(\\)]*" + quote(SRC_PROJECT_VARIABLE) + "[^\\*\\(\\)]*$");
    }

    private final String srcPathTemplate;
    private final String testPathTemplate;
    private final Pattern testProjectPattern;

    public TestFolderPathPattern(String srcPathTemplate, String testPathTemplate)
    {
        checkArgument(isValid(srcPathTemplate, testPathTemplate));
        this.srcPathTemplate = removeSurroundingSlashes(srcPathTemplate);
        this.testPathTemplate = removeSurroundingSlashes(testPathTemplate);
        this.testProjectPattern = createTestProjectPattern(this.testPathTemplate);
    }

    private static Pattern createTestProjectPattern(String testPathTemplate)
    {
        String testProjTemplate = getProjectName(testPathTemplate);
        String ptn = testProjTemplate.replaceFirst(quote(SRC_PROJECT_VARIABLE), "\\\\E(.*)\\\\Q");
        return compile("\\Q" + ptn + "\\E");
    }

    private static String getProjectName(String path)
    {
        int separatorIdx = path.indexOf("/");
        if(separatorIdx == - 1)
        {
            return path;
        }
        return path.substring(0, separatorIdx);
    }

    private static String removeSurroundingSlashes(String path)
    {
        String p = path;
        if(p.startsWith("/"))
        {
            p = p.substring(1);
        }
        if(p.endsWith("/"))
        {
            p = p.substring(0, p.length() - 1);
        }
        return p;
    }

    public static boolean isValid(String srcPathTemplate, String testPathTemplate)
    {
        if(Strings.isBlank(srcPathTemplate) || Strings.isBlank(testPathTemplate))
        {
            return false;
        }
        if(! (SRC_PATH_VALIDATOR.matcher(srcPathTemplate).matches() && TEST_PATH_VALIDATOR.matcher(testPathTemplate).matches()))
        {
            return false;
        }

        int groupCount = countOccurrences(srcPathTemplate, "(");
        if(groupCount > MAX_GROUPS)
        {
            return false;
        }
        if(groupCount != countOccurrences(srcPathTemplate, ")"))
        {
            return false;
        }

        List<GroupRef> groupRefs = getGroupRefs(testPathTemplate);
        if(groupCount != groupRefs.size())
        {
            return false;
        }

        return areGroupRefsValid(groupRefs, groupCount);
    }

    private static List<GroupRef> getGroupRefs(String template)
    {
        List<GroupRef> refs = new ArrayList<GroupRef>();

        boolean backslashEscaped = false;
        int refStart = - 1;

        char[] chars = template.toCharArray();
        for (int i = 0; i < chars.length; i++)
        {
            char c = chars[i];

            if(refStart != - 1) // currently parsing a group reference
            {
                if(Character.isDigit(c))
                {
                    refs.add(new GroupRef(Integer.valueOf(String.valueOf(c)), refStart, i + 1));
                }
                else if(c == '\\')
                {
                    backslashEscaped = true;
                }
                refStart = - 1;
            }

            // group reference detected
            if(refStart == - 1 && ! backslashEscaped && c == '\\')
            {
                refStart = i;
            }

            backslashEscaped = false;
        }

        char lastChar = chars[chars.length - 1];
        // last char was part of a group ref, let's add the ref to the list
        if(refStart != - 1 && lastChar != '\\')
        {
            refs.add(new GroupRef(Integer.valueOf(String.valueOf(lastChar)), refStart, chars.length));
        }

        return refs;
    }

    private static boolean areGroupRefsValid(List<GroupRef> groupRefs, int groupCount)
    {
        sort(groupRefs);

        for (int i = 0; i < groupCount; i++)
        {
            if(! groupRefs.get(i).num.equals(i + 1))
            {
                return false;
            }
        }
        return true;
    }

    public SourceFolderPath getTestPathFor(Path srcPath) throws DoesNotMatchConfigurationException
    {
        String cleanSrcPath = removeSurroundingSlashes(srcPath.toString());
        String projectName = getProjectName(cleanSrcPath);

        String srcPathTpl = getSrcPathTemplateForSrcProject(quoteReplacement(quote(projectName)));
        String codePathWithinSrcFolder = cleanSrcPath.replaceFirst(srcPathTpl, "");

        String tstPathTpl = getTestPathTemplateForSrcProject(projectName) + codePathWithinSrcFolder;
        srcPathTpl += quote(codePathWithinSrcFolder);
        tstPathTpl = resolveGroups(cleanSrcPath, srcPathTpl, tstPathTpl, srcPath);

        return new SourceFolderPath(tstPathTpl);
    }

    private String resolveGroups(String path, String tplWithGroups, String tplWithRefs, Path analizedPath) throws DoesNotMatchConfigurationException
    {
        String result = tplWithRefs;

        Matcher matcher = Pattern.compile(tplWithGroups).matcher(path);
        if(matcher.matches())
        {
            List<GroupRef> groupRefs = getGroupRefs(result);
            reverse(groupRefs);

            for (int i = 0; i < groupRefs.size(); i++)
            {
                GroupRef ref = groupRefs.get(i);

                final String groupContent;
                if(matcher.groupCount() >= ref.num)
                {
                    groupContent = matcher.group(ref.num);
                }
                else
                {
                    throw new DoesNotMatchConfigurationException(analizedPath);
                }

                result = result.substring(0, ref.startIdx) + groupContent + result.substring(ref.endIdx);
            }
        }
        return result;
    }

    public SourceFolderPath getSrcPathFor(Path testPath) throws DoesNotMatchConfigurationException
    {
        String tstProjectName = testPath.getProjectName();
        String srcProjectName = getSrcProjectName(tstProjectName, testPath);
        String cleanTestPath = removeSurroundingSlashes(testPath.toString());

        String tstPathTpl = getTestPathTemplateForSrcProject(srcProjectName);
        List<GroupRef> groupRefs = getGroupRefs(tstPathTpl);
        tstPathTpl = tstPathTpl.replaceAll("\\\\[1-9]", "(.*)");

        String srcPathTpl = getSrcPathTemplateForSrcProject(srcProjectName);
        srcPathTpl = replaceGroupsWithRefs(srcPathTpl, groupRefs);

        String codePathWithinSrcFolder = cleanTestPath.replaceFirst(tstPathTpl, "");
        if(codePathWithinSrcFolder.length() != 0 && ! codePathWithinSrcFolder.startsWith(tstProjectName))
        {
            srcPathTpl += codePathWithinSrcFolder;
            tstPathTpl += quote(codePathWithinSrcFolder);
        }

        srcPathTpl = resolveGroups(cleanTestPath, tstPathTpl, srcPathTpl, testPath);

        return new SourceFolderPath(srcPathTpl);
    }

    private String replaceGroupsWithRefs(String template, List<GroupRef> groupRefs)
    {
        Map<Integer, Integer> refIndices = new HashMap<Integer, Integer>();
        int idx = 1;
        for (GroupRef ref : groupRefs)
        {
            refIndices.put(ref.num - 1, idx);
            idx++;
        }

        String result = template;
        for (int i = 0; i < groupRefs.size(); i++)
        {
            result = result.replaceFirst("\\([^\\)]+\\)", "\\\\" + refIndices.get(i));
        }
        return result;
    }

    private String getSrcProjectName(String tstProjectName, Path tstPath) throws DoesNotMatchConfigurationException
    {
        Matcher m = testProjectPattern.matcher(tstProjectName);
        if(! m.matches())
        {
            throw new DoesNotMatchConfigurationException(tstPath);
        }
        return m.group(1);
    }

    private String getSrcPathTemplateForSrcProject(String projectName)
    {
        String tpl = srcPathTemplate.replaceFirst(quote(SRC_PROJECT_VARIABLE), projectName);

        // replaces * with [^/]* and ** with .*
        return tpl.replaceAll("\\*", "[^/]*").replaceAll("(?:" + quote("[^/]*") + "){2}", ".*");
    }

    private String getTestPathTemplateForSrcProject(String projectName)
    {
        return testPathTemplate.replaceFirst(quote(SRC_PROJECT_VARIABLE), projectName);
    }

    private static class GroupRef implements Comparable<GroupRef>
    {
        final Integer num;
        final int startIdx;
        final int endIdx;

        GroupRef(Integer num, int startIdx, int endIdx)
        {
            this.num = num;
            this.startIdx = startIdx;
            this.endIdx = endIdx;
        }

        @Override
        public int compareTo(GroupRef o)
        {
            return num.compareTo(o.num);
        }
    }
}
