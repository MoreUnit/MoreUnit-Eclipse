package org.moreunit.core.matching;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Collections.sort;
import static java.util.Collections.unmodifiableList;
import static java.util.regex.Pattern.quote;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.moreunit.core.matching.NameTokenizer.TokenizationResult;
import org.moreunit.core.util.Strings;

/**
 * A pattern for naming test files from the name of the source file they test.
 * <p>
 * This class is in charge to {@link #evaluate(String) evaluate} a file name in
 * order to answer the following questions:
 * </p>
 * <ul>
 * <li>Does the given name correspond to a <strong>source file</strong> or a
 * <strong>test file</strong>, according to this pattern?</li>
 * <li>If it's the name of a <em>source</em> file: what <em>test</em> file
 * name(s) could correspond to it?
 * <li>If it's the name of a <em>test</em> file: what <em>source</em> file
 * name(s) could correspond to it?</li>
 * </ul>
 * <p>
 * To achieve its goal, this class must be instantiated with a template
 * describing how to name a test file from a source file, and a tokenizer
 * allowing to split a file name into words. Refer to the next sections for more
 * information.
 * </p>
 * <h2>Word separator - Tokenizer</h2>
 * <p>
 * In order to support any file naming scheme, this class must be instantiated
 * with a {@link NameTokenizer} - or at least with a word separator - that will
 * be used to split a file name into words. When an empty separator is given, a
 * {@link CamelCaseNameTokenizer} is created instead of a
 * {@link SeparatorNameTokenizer}.
 * </p>
 * <h2>Template</h2>
 * <p>
 * A valid template makes use of the following special elements:
 * </p>
 * <dl>
 * <dt><tt>${srcFile}</tt></dt>
 * <dd>represents the name of the file under test (must be present in pattern)</dd>
 * <dt><tt>*</tt> <em style="font-size: 0.8em">(star)</em></dt>
 * <dd>represents a variable part (optional)</dd>
 * <dt><tt>(|)</tt> <em style="font-size: 0.8em">(parentheses and pipe)</em></dt>
 * <dd>represents a choice between several possible parts (optional). Choices
 * must be separated by pipes, and the set of choices must be wrapped in
 * parentheses</dd>
 * </dl>
 * <p>
 * Here are some valid templates for an hypothetical source file named
 * {@code "Concept"}, using CamelCase naming:
 * </p>
 * <table border="1">
 * <tr>
 * <th>Template</th>
 * <th>Test files corresponding to a source file named {@code "Concept"}</th>
 * </tr>
 * <tr>
 * <td><tt>${srcFile}Test</tt></td>
 * <td><tt>ConceptTest</tt></td>
 * </tr>
 * <tr>
 * <td><tt>${srcFile}*Test</tt></td>
 * <td><tt>ConceptTest</tt>, <tt>ConceptFooTest</tt>, <tt>ConceptBarTest</tt>...
 * </td>
 * </tr>
 * <tr>
 * <td><tt>${srcFile}(Test|Spec)</tt></td>
 * <td><tt>ConceptTest</tt>, <tt>ConceptSpec</tt></td>
 * </tr>
 * <tr>
 * <td><tt>${srcFile}(Test|IntegrationTest)*</tt></td>
 * <td><tt>ConceptTest</tt>, <tt>ConceptIntegrationTest</tt>,
 * <tt>ConceptFooTest</tt>, <tt>ConceptFooIntegrationTest</tt>,
 * <tt>ConceptBarTest</tt>...</td>
 * </tr>
 * <tr>
 * <td><tt>(SpecFor|TestFor)*${srcFile}</tt></td>
 * <td><tt>SpecForConcept</tt>, <tt>TestForConcept</tt>,
 * <tt>SpecForFooConcept</tt>, <tt>TestForFooConcept</tt>,
 * <tt>SpecForBarConcept</tt>, <tt>SpecForFooConcept</tt>...</td>
 * </tr>
 * </table>
 * <h3>Note: template complexity</h3>
 * <p>
 * Though it is possible to specify a template as complex as &quot;
 * <tt>*(Pre1|Pre2)*${srcFile}*(Suf1|Suf2)*</tt>&quot;, it would have almost no
 * value: file searches would be time consuming, plus they would give lots of
 * garbage results.
 * </p>
 * <p>
 * This class won't even try to generate all possible source file names for a
 * test file named: {@code "AaaPre2BbbCccDddEeeSuf1Fff"}. Indeed, which one is
 * the likeliest source file name: {@code "BbbCccDddEee"}, {@code "BbbCccDdd"},
 * {@code "BbbCcc"}, {@code "Bbb"}, {@code "CccDddEee"}, {@code "DddEee"},
 * {@code "Eee"}, {@code "CccDdd"}, {@code "Ccc"}, {@code "Ddd"}?
 * </p>
 * <p>
 * For the previous example, it would only generate the following source file
 * names:
 * </p>
 * <dl>
 * <dt>Preferred name</dt>
 * <dd>{@code "BbbCccDddEee"}</dd>
 * <dt>Various word combinations, starting from the left</dt>
 * <dd>{@code "BbbCccDdd"}, {@code "BbbCcc"}, {@code "Bbb"}</dd>
 * <dt>Various word combinations, starting from the right</dt>
 * <dd>{@code "CccDddEee"}, {@code "DddEee"}, {@code "Eee"}</dd>
 * </dl>
 * <p>
 * Combinations of words found "in the middle" of the test file name (
 * {@code "CccDdd"}, {@code "Ccc"}, {@code "Ddd"}) would be discarded.
 * </p>
 */
public final class TestFileNamePattern
{
    public static final String SRC_FILE_VARIABLE = "${srcFile}";

    /* Various patterns */
    private static final String CONSECUTIVE_WILDCARDS = "(\\.\\*){2,}";

    private static final String VALIDATOR;
    static
    {
        String separatorAndOrStar = "(\\*?(${sep})?)?((${sep})?\\*?)";
        String authorizedChars = "[^\\(\\|\\)\\*]";
        String prefixOrSuffix = separatorAndOrStar + "(\\(" + authorizedChars + "+(\\|" + authorizedChars + "+)*\\)|" + authorizedChars + "*)" + separatorAndOrStar;

        VALIDATOR = "^" + prefixOrSuffix + quote(SRC_FILE_VARIABLE) + prefixOrSuffix + "$";
    }

    private static final Pattern GROUP_CONTENT_FINDER = Pattern.compile("[^\\(]*\\(([^\\)]*)\\)[^\\)]*");
    private static final Pattern SINGLE_GROUP;
    static
    {
        String specialChars = "[\\.\\*]";
        String authorizedChars = "[^\\(\\|\\)\\.\\*]";
        SINGLE_GROUP = Pattern.compile(specialChars + "*(" + authorizedChars + "+)" + specialChars + "*");
    }

    /* \ Various patterns */

    private static final Comparator<String> byDescendingLength = new Comparator<String>()
    {
        @Override
        public int compare(String s1, String s2)
        {
            int result = Integer.valueOf(s2.length()).compareTo(s1.length());
            return result != 0 ? result : s1.compareToIgnoreCase(s2);
        }
    };

    private final FileType fileType;
    private final String separator;
    private final NameTokenizer tokenizer;
    private final String prefix;
    private final String suffix;
    private final boolean wildCardBefore;
    private final boolean wildCardAfter;
    private final String patternString;
    private final List<Group> groups;
    private final Collection<Pattern> patterns;

    private TestFileNamePattern(String template, NameTokenizer tokenizer, FileType fileType)
    {
        this.fileType = fileType;

        separator = tokenizer.getSeparator();
        if(! isValid(template, separator))
        {
            throw new IllegalArgumentException("Invalid template: " + template);
        }

        this.tokenizer = tokenizer;

        // makes it a valid regex
        String tpl = template.replace("*", ".*");

        int varStart = tpl.indexOf("$");
        int varEnd = tpl.indexOf("}");

        // extracts part before variable
        String preOld = tpl.substring(0, varStart);

        String pre = changeWordsIntoGroup(preOld);

        // update tpl with new prefix
        tpl = tpl.replaceFirst(quote(preOld), pre);
        int varOffset = pre.length() - preOld.length();
        varStart += varOffset;
        varEnd += varOffset;

        pre = removeEndSeparatorIfPresent(pre);

        pre = orderGroupPartsByDescLength(pre);

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

        // extracts part after variable
        String sufOld = varEnd + 1 == tpl.length() ? "" : tpl.substring(varEnd + 1);

        String suf = changeWordsIntoGroup(sufOld);

        // update tpl with new suffix
        tpl = tpl.replaceFirst(quote(sufOld) + "$", suf);

        suf = removeStartSeparatorIfPresent(suf);

        suf = orderGroupPartsByDescLength(suf);

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

        patternString = tpl;

        // final step: makes valid regex from our custom template
        String finalPatternStr = patternString.replace(SRC_FILE_VARIABLE, ".*");

        // extracts prefix and suffix groups
        groups = findGroups(finalPatternStr);

        patterns = createEvaluationPatterns(finalPatternStr);
    }

    private String removeStartSeparatorIfPresent(String str)
    {
        if(str.startsWith(separator))
        {
            return str.substring(separator.length());
        }
        return str;
    }

    private String removeEndSeparatorIfPresent(String str)
    {
        if(str.endsWith(separator))
        {
            return str.substring(0, str.length() - separator.length());
        }
        return str;
    }

    /**
     * Creates a {@link TestFileNamePattern} with the given template and
     * {@link NameTokenizer}.
     */
    public TestFileNamePattern(String template, NameTokenizer tokenizer)
    {
        this(template, tokenizer, FileType.UNKNOWN);
    }

    /**
     * Creates a {@link TestFileNamePattern} with the given template and
     * separator. The separator will be used to create a {@link NameTokenizer}.
     * Should the separator be empty, a {@link CamelCaseTokenizer} will be
     * created instead of a {@link SeparatorTokenizer}.
     */
    public TestFileNamePattern(String template, String separator)
    {
        this(template, createTokenizer(separator), FileType.UNKNOWN);
    }

    /**
     * Creates a {@link TestFileNamePattern} in the same way as
     * {@link #TestFileNamePattern(String, String)} does, except that
     * {@link #evaluate(String)} won't guess the type of the file which name is
     * given, but will always treat it as a source file.
     */
    public static TestFileNamePattern forceEvaluationAsSourceFile(String template, String separator)
    {
        return new TestFileNamePattern(template, createTokenizer(separator), FileType.SOURCE);
    }

    /**
     * Creates a {@link TestFileNamePattern} in the same way as
     * {@link #TestFileNamePattern(String, String)} does, except that
     * {@link #evaluate(String)} won't guess the type of the file which name is
     * given, but will always treat it as a test file.
     */
    public static TestFileNamePattern forceEvaluationAsTestFile(String template, String separator)
    {
        return new TestFileNamePattern(template, createTokenizer(separator), FileType.TEST);
    }

    private static NameTokenizer createTokenizer(String separator)
    {
        if(separator.length() == 0)
        {
            return new CamelCaseNameTokenizer();
        }
        else
        {
            return new SeparatorNameTokenizer(separator);
        }
    }

    public static boolean isValid(String template, String separator)
    {
        // TODO Nicolas improve it to return failure reason
        return template.matches(VALIDATOR.replace("${sep}", separator));
    }

    private String changeWordsIntoGroup(String prefixOrSuffix)
    {
        Matcher m = SINGLE_GROUP.matcher(prefixOrSuffix);
        if(! m.matches())
        {
            return prefixOrSuffix;
        }
        return prefixOrSuffix.substring(0, m.start(1)) + "(" + m.group(1) + ")" + prefixOrSuffix.substring(m.end(1));
    }

    /**
     * Arranges order of group parts so that, for instance, template &quot;
     * <tt>(Test|Tests)${srcFile}</tt>&quot; will give &quot;<tt>Concept</tt>
     * &quot; as a preferred source file name for test file &quot;
     * <tt>TestsConcept</tt>&quot;, and not &quot;<tt>sConcept</tt>&quot;.
     *
     * @param prefixOrSuffixPattern
     * @return same pattern, with group parts ordered by descending length
     */
    private String orderGroupPartsByDescLength(String prefixOrSuffixPattern)
    {
        Matcher m = GROUP_CONTENT_FINDER.matcher(prefixOrSuffixPattern);
        if(! m.matches())
        {
            return prefixOrSuffixPattern;
        }

        List<String> parts = Strings.splitAsList(m.group(1), "\\|");
        sort(parts, byDescendingLength);

        StringBuilder sb = new StringBuilder("(");
        Strings.join(sb, "|", parts);
        String orderedParts = sb.append(")").toString();

        return prefixOrSuffixPattern.substring(0, m.start(1)) + orderedParts + prefixOrSuffixPattern.substring(m.end(1));
    }

    /**
     * <p>
     * Find groups of prefixes or suffixes in the given string (example of
     * group: {@code "(Part1|Part2)"}). A single prefix or suffix is considered
     * as a group of one part.
     * </p>
     * <p>
     * Possible group combinations are: no group, prefix group, suffix group, or
     * both prefix and suffix groups. The validation forbids any other
     * configuration
     * </p>
     *
     * @param patternStr the string in which to find groups
     * @return the found groups
     */
    private List<Group> findGroups(String patternStr)
    {
        List<Group> groups = new ArrayList<Group>();

        int firstGroupStart = firstIndexOfNonQuoted("(", patternStr);
        if(firstGroupStart != - 1)
        {
            int firstGroupEnd = patternStr.indexOf(")", firstGroupStart);
            groups.add(new Group(patternStr, firstGroupStart, firstGroupEnd));
        }

        int secondGroupStart = lastIndexOfNonQuoted("(", patternStr);
        if(secondGroupStart != - 1 && secondGroupStart != firstGroupStart)
        {
            int secondGroupEnd = patternStr.indexOf(")", secondGroupStart);
            groups.add(new Group(patternStr, secondGroupStart, secondGroupEnd));
        }

        return groups;
    }

    private int lastIndexOfNonQuoted(String query, String str)
    {
        int[] maybeQuotedPart = findQuotedPart(str);

        int resultIdx = str.length();
        do
        {
            resultIdx = str.lastIndexOf(query, resultIdx - 1);
        }
        while (maybeQuotedPart != null && resultIdx > maybeQuotedPart[0] && resultIdx < maybeQuotedPart[1]);

        return resultIdx;
    }

    /**
     * <p>
     * Creates patterns in charge to evaluate whether a file is a source file or
     * a test one:
     * </p>
     * <ul>
     * <li>if this object has been created via
     * {@link #forceEvaluationAsSourceFile(String, String)} or
     * {@link #forceEvaluationAsTestFile(String, String)}, no evaluation is
     * required so no pattern will be created,</li>
     * <li>if the given string contains both a prefix group and a suffix group,
     * those groups should be interpreted as a logical &quot;OR&quot;, so two
     * patterns are created, making each group optional in turn,</li>
     * <li>for all other cases, the given string is simply compiled.</li>
     * </ul>
     *
     * @param patternStr the string to be compiled as Pattern(s)
     * @return the compiled pattern(s)
     */
    private Collection<Pattern> createEvaluationPatterns(String patternStr)
    {
        if(fileType != FileType.UNKNOWN)
        {
            // no need to compute patterns
            return emptySet();
        }

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

                result.add(Pattern.compile(beforeGroup + g.group + "?" + afterGroup));
            }
        }

        return result;
    }

    /**
     * <p>
     * Evaluates the given file name in order to answer the following questions:
     * </p>
     * <ul>
     * <li>Does it correspond to a <strong>source file</strong> or a
     * <strong>test file</strong>, according to this pattern?</li>
     * <li>If it's the name of a <em>source</em> file: what <em>test</em> file
     * name(s) could correspond to it?
     * <li>If it's the name of a <em>test</em> file: what <em>source</em> file
     * name(s) could correspond to it?</li>
     * </ul>
     *
     * @param fileBaseName the file name to evaluate
     * @return the {@link FileNameEvaluation result} of the evaluation
     */
    public FileNameEvaluation evaluate(String fileBaseName)
    {
        if(fileType == FileType.TEST || (fileType == FileType.UNKNOWN && matchesAnyPattern(fileBaseName)))
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
        String preferredName = buildPreferredSrcFileName(fileBaseName);

        List<String> otherPatterns = buildOtherCorrespondingSrcFilePatterns(preferredName);

        return new FileNameEvaluation(fileBaseName, true, asList(quote(preferredName)), otherPatterns);
    }

    /**
     * Simply removes test prefixes or suffixes.
     */
    private String buildPreferredSrcFileName(String testFileName)
    {
        String maybeSeparator = String.format("(%s)?", quote(separator));
        return testFileName.replaceFirst("^" + prefix + maybeSeparator, "").replaceFirst(maybeSeparator + suffix + "$", "");
    }

    /**
     * If wildcards are specified, generates different combinations of the
     * tokens that compose {@code preferredName}.
     */
    private List<String> buildOtherCorrespondingSrcFilePatterns(String preferredName)
    {
        if(! (wildCardBefore || wildCardAfter))
        {
            return emptyList();
        }

        TokenizationResult result = tokenizer.tokenize(preferredName);

        List<String> patterns = new ArrayList<String>();
        if(wildCardBefore)
        {
            for (String c : result.getCombinationsFromEnd())
            {
                patterns.add(quote(c));
            }
        }
        if(wildCardAfter)
        {
            for (String c : result.getCombinationsFromStart())
            {
                patterns.add(quote(c));
            }
        }

        // if wildCardBefore && wildCardAfter, doesn't search for "middle" token
        // combinations as it would be very time consuming
        // to search for them, plus it would give lots of garbage results

        // makes pattern order predictable
        // long names should be searched preferentially to short ones
        sort(patterns, byDescendingLength);

        return patterns;
    }

    private FileNameEvaluation buildSrcFileResult(String srcFileName)
    {
        String testFileNameWithGroups = patternString.replace(SRC_FILE_VARIABLE, quote(srcFileName));

        List<String> preferredPatterns = buildPreferredTestFilePatterns(testFileNameWithGroups);

        List<String> otherPatterns = buildOtherCorrespondingTestFilePatterns(testFileNameWithGroups);

        return new FileNameEvaluation(srcFileName, false, preferredPatterns, otherPatterns);
    }

    /**
     * Builds a pattern for each possible prefix/suffix combination.
     */
    private List<String> buildPreferredTestFilePatterns(String testFileNameWithGroups)
    {
        List<String> namesToProcess = new ArrayList<String>();
        namesToProcess.add(testFileNameWithGroups);

        for (Group group : groups)
        {
            List<String> processedNames = new ArrayList<String>();

            for (String name : namesToProcess)
            {
                String[] surroundingParts = partsSurroundingFirstGroup(name);

                for (String part : group.possibleParts)
                {
                    if(part.length() != 0)
                    {
                        processedNames.add(surroundingParts[0] + part + surroundingParts[1]);
                    }
                }
            }

            namesToProcess = processedNames;
        }

        return namesToProcess;
    }

    private String[] partsSurroundingFirstGroup(String name)
    {
        int openingBracketIdx = firstIndexOfNonQuoted("(", name);

        int closingBracketIdx = name.indexOf(")", openingBracketIdx);

        String beforeGroup = name.substring(0, openingBracketIdx);
        String afterGroup = name.substring(closingBracketIdx + 1);
        return new String[] { beforeGroup, afterGroup };
    }

    private int firstIndexOfNonQuoted(String query, String str)
    {
        int[] maybeQuotedPart = findQuotedPart(str);

        int resultIdx = - 1;
        do
        {
            resultIdx = str.indexOf(query, resultIdx + 1);
        }
        while (maybeQuotedPart != null && resultIdx > maybeQuotedPart[0] && resultIdx < maybeQuotedPart[1]);

        return resultIdx;
    }

    private int[] findQuotedPart(String str)
    {
        int start = str.indexOf("\\Q");
        return start == - 1 ? null : new int[] { start, str.indexOf("\\E", start) };
    }

    /**
     * When both prefixes and suffixes are specified, builds a pattern for each
     * possible prefix or suffix, authorizing either prefix without suffix or
     * suffix without prefix.
     */
    private List<String> buildOtherCorrespondingTestFilePatterns(String testFileNameWithGroups)
    {
        if(groups.size() != 2)
        {
            return emptyList();
        }

        List<String> otherFileNames = new ArrayList<String>();

        List<Group> groupsInFileName = findGroups(testFileNameWithGroups);
        Group firstGroup = groupsInFileName.get(0);
        Group secondGroup = groupsInFileName.get(1);

        for (String part : firstGroup.possibleParts)
        {
            String name = testFileNameWithGroups.substring(0, firstGroup.start) //
                          + part //
                          + testFileNameWithGroups.substring(firstGroup.end + 1, secondGroup.start) //
                          + testFileNameWithGroups.substring(secondGroup.end + 1);

            name = name.replaceAll(CONSECUTIVE_WILDCARDS, ".*");

            otherFileNames.add(removeEndSeparatorIfPresent(name));
        }

        for (String part : secondGroup.possibleParts)
        {
            String name = testFileNameWithGroups.substring(0, firstGroup.start) //
                          + testFileNameWithGroups.substring(firstGroup.end + 1, secondGroup.start) //
                          + part //
                          + testFileNameWithGroups.substring(secondGroup.end + 1);

            name = name.replaceAll(CONSECUTIVE_WILDCARDS, ".*");

            otherFileNames.add(removeStartSeparatorIfPresent(name));
        }

        return otherFileNames;
    }

    public String getSeparator()
    {
        return separator;
    }

    /**
     * A group of name parts (prefixes or suffixes), regex-style (example:
     * {@code "(Part1|Part2)"}).
     */
    private static class Group
    {
        final int start;
        final int end;
        final String group;
        final List<String> possibleParts;

        Group(String pattern, int start, int end)
        {
            this.start = start;
            this.end = end;

            group = pattern.substring(start, end + 1);
            String groupContent = pattern.substring(start + 1, end);
            possibleParts = unmodifiableList(asList(groupContent.split("\\|")));
        }
    }

    /**
     * The type of the files that will be evaluated by a
     * {@link TestFileNamePattern}.
     */
    private static enum FileType
    {
        /** Type forced to "source", evaluation will be bypassed. */
        SOURCE,
        /** Type forced to "test", evaluation will be bypassed. */
        TEST,
        /** Type has to be determined, evaluation is required. */
        UNKNOWN
    }
}
