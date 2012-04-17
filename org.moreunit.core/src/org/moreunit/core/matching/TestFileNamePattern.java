package org.moreunit.core.matching;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.sort;
import static java.util.regex.Pattern.quote;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import org.moreunit.core.matching.NameTokenizer.TokenizationResult;

public class TestFileNamePattern
{
    private static final String CONSECUTIVE_WILDCARDS = "(\\.\\*){2,}";
    private static final String GROUP_PATTERN = "\\([^\\)]*\\)";
    public static final String SRC_FILE_VARIABLE = "${srcFile}";

    private static final String VALIDATOR;
    static
    {
        String separatorAndOrStar = "(\\*?(${sep})?)?((${sep})?\\*?)";
        String authorizedChars = "[^\\(\\|\\)\\*]";
        String prefixOrSuffix = separatorAndOrStar + "(\\(" + authorizedChars + "+(\\|" + authorizedChars + "+)*\\)|" + authorizedChars + "*)" + separatorAndOrStar;

        VALIDATOR = "^" + prefixOrSuffix + quote(SRC_FILE_VARIABLE) + prefixOrSuffix + "$";
    }

    private static final Comparator<String> byDescendingLength = new Comparator<String>()
    {
        public int compare(String s1, String s2)
        {
            int result = Integer.valueOf(s2.length()).compareTo(s1.length());
            return result != 0 ? result : s1.compareToIgnoreCase(s2);
        }
    };

    private final String separator;
    private final NameTokenizer tokenizer;
    private final String prefix;
    private final String suffix;
    private final boolean wildCardBefore;
    private final boolean wildCardAfter;
    private final String patternString;
    private final List<Group> groups;
    private final Collection<Pattern> patterns;

    public TestFileNamePattern(String template, NameTokenizer tokenizer)
    {
        separator = tokenizer.getSeparator();
        if(! isValid(template, separator))
        {
            throw new IllegalArgumentException("Invalid template: " + template);
        }

        this.tokenizer = tokenizer;

        patternString = template.replace("*", ".*");

        int varStart = patternString.indexOf("$");
        int varEnd = patternString.indexOf("}");

        String pre = patternString.substring(0, varStart);

        // removes separator present after prefix, if any
        if(pre.endsWith(separator))
        {
            pre = pre.substring(0, pre.length() - separator.length());
        }

        // records presence of wildcard before file name and removes it
        wildCardBefore = pre.endsWith(".*");
        if(wildCardBefore)
        {
            prefix = pre.substring(0, pre.length() - 2);
        }
        else
        {
            prefix = pre;
        }

        String suf = varEnd + 1 == patternString.length() ? "" : patternString.substring(varEnd + 1);

        // removes separator present before suffix, if any
        if(suf.startsWith(separator))
        {
            suf = suf.substring(separator.length());
        }

        // records presence of wildcard after file name and removes it
        wildCardAfter = suf.startsWith(".*");
        if(wildCardAfter)
        {
            suffix = suf.substring(2);
        }
        else
        {
            suffix = suf;
        }

        // final step: makes valid regex from our custom template
        String finalPatternStr = patternString.replace(SRC_FILE_VARIABLE, ".*");
        groups = findGroups(finalPatternStr);
        patterns = createPatterns(finalPatternStr);
    }

    public static boolean isValid(String template, String separator)
    {
        return template.matches(VALIDATOR.replace("${sep}", separator));
    }

    private List<Group> findGroups(String patternStr)
    {
        List<Group> groups = new ArrayList<Group>();

        // Possible group combinations are: no group, prefix group, suffix
        // group, or both prefix and suffix group.
        // The validation forbids any other configuration

        int firstGroupStart = patternStr.indexOf("(");
        if(firstGroupStart != - 1)
        {
            groups.add(new Group(patternStr, firstGroupStart, patternStr.indexOf(")")));
        }

        int secondGroupStart = patternStr.lastIndexOf("(");
        if(secondGroupStart != - 1 && secondGroupStart != firstGroupStart)
        {
            groups.add(new Group(patternStr, secondGroupStart, patternStr.lastIndexOf(")")));
        }

        return groups;
    }

    private Collection<Pattern> createPatterns(String patternStr)
    {
        Collection<Pattern> result = new ArrayList<Pattern>();
        if(groups.size() < 2)
        {
            result.add(Pattern.compile(patternStr));
        }
        else
        {
            // makes one or the other group optional
            for (Group g : groups)
            {
                // removes separator present before group, if any
                String beforeGroup = patternStr.substring(0, g.start);
                beforeGroup = beforeGroup.replaceFirst(quote(separator) + "$", "");

                // removes separator present after group, if any
                String afterGroup = patternStr.substring(g.end + 1);
                afterGroup = afterGroup.replaceFirst("^" + quote(separator), "");

                result.add(Pattern.compile(beforeGroup + patternStr.substring(g.start, g.end + 1) + "?" + afterGroup));
            }
        }

        return result;
    }

    public FileNameEvaluation evaluate(String fileBaseName)
    {
        if(matchesAnyPattern(fileBaseName))
        {
            return buildTestFileResult(fileBaseName);
        }
        return buildSrcFileResult(fileBaseName);
    }

    private boolean matchesAnyPattern(String str)
    {
        for (Pattern p : patterns)
        {
            if(p.matcher(str).matches())
            {
                return true;
            }
        }
        return false;
    }

    private FileNameEvaluation buildTestFileResult(String fileBaseName)
    {
        String maybeSeparator = String.format("(%s)?", quote(separator));
        String cleanName = fileBaseName.replaceFirst("^" + prefix + maybeSeparator, "").replaceFirst(maybeSeparator + suffix + "$", "");

        List<String> otherPatterns = buildOtherCorrespondingSrcFilePatterns(cleanName);

        return new FileNameEvaluation(true, asList(cleanName), otherPatterns);
    }

    private List<String> buildOtherCorrespondingSrcFilePatterns(String cleanName)
    {
        TokenizationResult result = tokenizer.tokenize(cleanName);

        List<String> patterns = new ArrayList<String>();
        if(wildCardBefore)
        {
            patterns.addAll(result.getCombinationsFromEnd());
        }
        if(wildCardAfter)
        {
            patterns.addAll(result.getCombinationsFromStart());
        }

        // if wcBefore && wcAfter, doesn't search for "middle" token
        // combinations as it would be very time consuming
        // to search for them, plus it would give lots of garbage results

        // makes pattern order predictable
        // long names should be searched preferentially to short ones
        sort(patterns, byDescendingLength);

        return patterns;
    }

    private FileNameEvaluation buildSrcFileResult(String fileBaseName)
    {
        String testFileNameWithGroups = patternString.replace(SRC_FILE_VARIABLE, fileBaseName);

        List<String> preferredPatterns = buildPreferredTestFilePatterns(testFileNameWithGroups);

        List<String> otherPatterns = buildOtherCorrespondingTestFilePatterns(testFileNameWithGroups);

        return new FileNameEvaluation(false, preferredPatterns, otherPatterns);
    }

    private List<String> buildPreferredTestFilePatterns(String testFileNameWithGroups)
    {
        List<String> namesToProcess = new ArrayList<String>();
        namesToProcess.add(testFileNameWithGroups);

        if(! groups.isEmpty())
        {
            for (Group group : groups)
            {
                List<String> processedNames = new ArrayList<String>();

                for (String name : namesToProcess)
                {
                    String beforeGroup = name.substring(0, name.indexOf("("));
                    String afterGroup = name.substring(name.indexOf(")") + 1);

                    for (String part : group.possibleParts)
                    {
                        if(part.length() != 0)
                        {
                            processedNames.add(beforeGroup + part + afterGroup);
                        }
                    }
                }

                namesToProcess = processedNames;
            }
        }

        return namesToProcess;
    }

    private List<String> buildOtherCorrespondingTestFilePatterns(String testFileNameWithGroups)
    {
        if(groups.size() != 2)
        {
            return emptyList();
        }

        // also authorizes either prefix without suffix or suffix without prefix
        List<String> otherFileNames = new ArrayList<String>();

        List<Group> groupsInFileName = findGroups(testFileNameWithGroups);
        for (String part : groupsInFileName.get(0).possibleParts)
        {
            otherFileNames.add(testFileNameWithGroups.replaceFirst(GROUP_PATTERN, part).replaceFirst(GROUP_PATTERN, "").replaceAll(CONSECUTIVE_WILDCARDS, ".*"));
        }

        for (String part : groupsInFileName.get(1).possibleParts)
        {
            otherFileNames.add(testFileNameWithGroups.replaceFirst(GROUP_PATTERN, "").replaceFirst(GROUP_PATTERN, part).replaceAll(CONSECUTIVE_WILDCARDS, ".*"));
        }

        return otherFileNames;
    }

    private static class Group
    {
        final int start;
        final int end;
        final String[] possibleParts;

        Group(String pattern, int start, int end)
        {
            this.start = start;
            this.end = end;

            String group = pattern.substring(start + 1, end);
            possibleParts = group.split("\\|");
        }
    }
}
