package org.moreunit.core.matching;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Collections.sort;
import static java.util.regex.Matcher.quoteReplacement;
import static java.util.regex.Pattern.compile;
import static java.util.regex.Pattern.quote;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import org.moreunit.core.matching.NameTokenizer.TokenizationResult;
import org.moreunit.core.matching.TestFileNamePatternParser.UserDefinedPart;

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
    public static final String SRC_FILE_VARIABLE = TestFileNamePatternParser.SRC_FILE_VARIABLE;
    private static final Pattern SRC_FILE_VARIABLE_PATTERN = compile(quote(SRC_FILE_VARIABLE));

    /* Various patterns */

    private static final String VALIDATOR;
    static
    {
        // ${sep} must be replaced with the actual separator before use
        String separatorAndOrStar = "(\\*?(${sep})?)?((${sep})?\\*?)";
        // non-brackets/pipe/star or protected ones
        String authorizedChars = "(?:[^\\(\\|\\)\\*]|" + quote("\\)") + "|" + quote("\\(") + "|" + quote("\\*") + "|" + quote("\\|") + ")";
        String prefixOrSuffix = separatorAndOrStar + "(\\(" + authorizedChars + "+?(\\|" + authorizedChars + "+?)*?\\)|" + authorizedChars + "*?)" + separatorAndOrStar;

        VALIDATOR = "^" + prefixOrSuffix + quote(SRC_FILE_VARIABLE) + prefixOrSuffix + "$";
    }

    private static final Pattern QUOTE_SEPARATORS_AND_WILDCARDS = compile("(?:\\\\Q|\\\\E|\\.\\*)");

    /* /end Various patterns */

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
    private final TestFileNamePatternParser.Success parserResult;
    private final String prefix;
    private final String suffix;
    private final boolean wildCardBeforeVariable;
    private final boolean wildCardAfterVariable;
    private final String patternString;
    private final List<Group> groups;
    private final Collection<Pattern> patterns;

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

    private TestFileNamePattern(String template, NameTokenizer tokenizer, FileType fileType)
    {
        this.fileType = fileType;

        separator = tokenizer.separator();
        if(! isValid(template, separator))
        {
            throw new IllegalArgumentException("Invalid template: " + template);
        }

        this.tokenizer = tokenizer;

        parserResult = new TestFileNamePatternParser(template, tokenizer).parse().get();

        wildCardBeforeVariable = parserResult.prefix().hasWildcardAfter();
        prefix = toPrefixPattern(parserResult.prefix());

        wildCardAfterVariable = parserResult.suffix().hasWildcardBefore();
        suffix = toSuffixPattern(parserResult.suffix());

        patternString = toPattern(parserResult.prefix()) + SRC_FILE_VARIABLE + toPattern(parserResult.suffix());

        // extracts prefix and suffix groups
        groups = createGroups();

        patterns = createEvaluationPatterns();
    }

    public static boolean isValid(String template, String separator)
    {
        // TODO Nicolas use TestFileNamePatternParser to validate template and
        // return failure reason, get rid of VALIDATOR.
        return template.matches(VALIDATOR.replace("${sep}", quote(separator)));
    }

    private static String toPrefixPattern(UserDefinedPart part)
    {
        if(! part.hasAlternatives())
            return "";

        StringBuilder buffer = new StringBuilder();

        if(part.hasWildcardBefore())
            buffer.append(".*");

        appendAlternatives(buffer, part);

        return buffer.toString();
    }

    private static String toSuffixPattern(UserDefinedPart part)
    {
        if(! part.hasAlternatives())
            return "";

        StringBuilder buffer = new StringBuilder();

        appendAlternatives(buffer, part);

        if(part.hasWildcardAfter())
            buffer.append(".*");

        return buffer.toString();
    }

    private static void appendAlternatives(StringBuilder buffer, UserDefinedPart part)
    {
        if(! part.hasAlternatives())
            return;

        buffer.append('(');

        boolean first = true;
        for (String s : part.alternatives())
        {
            if(first)
                first = false;
            else
                buffer.append('|');

            buffer.append(quote(s));
        }

        buffer.append(')');
    }

    private static String toPattern(UserDefinedPart part, String alternative)
    {
        if(! part.hasAlternatives())
            return "";

        StringBuilder buffer = new StringBuilder();

        String beforeAlt = part.before();
        if(! beforeAlt.isEmpty())
            buffer.append(beforeAlt);

        buffer.append(quote(alternative));

        String afterAlt = part.after();
        if(! afterAlt.isEmpty())
            buffer.append(afterAlt);

        return buffer.toString();
    }

    private static String toOptionalPattern(UserDefinedPart part)
    {
        if(! part.hasAlternatives())
            return "";

        StringBuilder buffer = new StringBuilder();

        appendAlternatives(buffer, part);
        buffer.append('?');

        return buffer.toString();
    }

    private static String toPattern(UserDefinedPart part)
    {
        if(! part.hasAlternatives())
            return "";

        StringBuilder buffer = new StringBuilder();

        String beforeAlt = part.before();
        if(! beforeAlt.isEmpty())
            buffer.append(beforeAlt);

        appendAlternatives(buffer, part);

        String afterAlt = part.after();
        if(! afterAlt.isEmpty())
            buffer.append(afterAlt);

        return buffer.toString();
    }

    private List<Group> createGroups()
    {
        List<Group> result = new ArrayList<Group>(2);
        if(parserResult.prefix().hasAlternatives())
        {
            result.add(new Group(parserResult.prefix().alternatives()));
        }
        if(parserResult.suffix().hasAlternatives())
        {
            result.add(new Group(parserResult.suffix().alternatives()));
        }
        return result;
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
     * <li>if both a prefix group and a suffix group have been specified, those
     * groups should be interpreted as a logical &quot;OR&quot;, so two patterns
     * are created, making each group optional in turn,</li>
     * <li>for all other cases, a unique pattern is created.</li>
     * </ul>
     *
     * @param patternStr the string to be compiled as Pattern(s)
     * @return the compiled pattern(s)
     */
    private Collection<Pattern> createEvaluationPatterns()
    {
        if(fileType != FileType.UNKNOWN)
        {
            // no need to compute patterns
            return emptySet();
        }

        Collection<Pattern> result = new ArrayList<Pattern>(2);
        if(groups.size() < 2)
        {
            result.add(compile(SRC_FILE_VARIABLE_PATTERN.matcher(patternString).replaceAll(".*")));
        }
        else
        {
            // makes one or the other group optional
            result.add(compile(toOptionalPattern(parserResult.prefix()) + ".*" + toPattern(parserResult.suffix())));
            result.add(compile(toPattern(parserResult.prefix()) + ".*" + toOptionalPattern(parserResult.suffix())));
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

        return new FileNameEvaluation(fileBaseName, true, preferredName, asList(quote(preferredName)), otherPatterns);
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
        if(! (wildCardBeforeVariable || wildCardAfterVariable))
        {
            return emptyList();
        }

        TokenizationResult result = tokenizer.tokenize(preferredName);

        List<String> patterns = new ArrayList<String>();
        if(wildCardBeforeVariable)
        {
            for (String c : result.getCombinationsFromEnd())
            {
                patterns.add(quote(c));
            }
        }
        if(wildCardAfterVariable)
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
        String preferredTestFileName = buildPreferredTestFileName(srcFileName);
        ;

        String quotedSrcFileName = quote(srcFileName);

        List<String> preferredPatterns = buildPreferredTestFilePatterns(quotedSrcFileName);

        List<String> otherPatterns = buildOtherCorrespondingTestFilePatterns(quotedSrcFileName);

        return new FileNameEvaluation(srcFileName, false, preferredTestFileName, preferredPatterns, otherPatterns);
    }

    private String buildPreferredTestFileName(String srcFileName)
    {
        final String result;
        UserDefinedPart prefixPart = parserResult.prefix();
        UserDefinedPart suffixPart = parserResult.suffix();

        if(! prefixPart.hasAlternatives() && ! suffixPart.hasAlternatives())
        {
            result = SRC_FILE_VARIABLE_PATTERN.matcher(patternString).replaceAll(quoteReplacement(srcFileName));
        }
        else if(! prefixPart.hasAlternatives())
        {
            result = srcFileName + toPattern(suffixPart, suffixPart.firstAlternative());
        }
        else if(! suffixPart.hasAlternatives())
        {
            result = toPattern(prefixPart, prefixPart.firstAlternative()) + srcFileName;
        }
        else
        {
            result = toPattern(prefixPart, prefixPart.firstAlternative()) + srcFileName + toPattern(suffixPart, suffixPart.firstAlternative());
        }

        return removeQuotesAndWildcards(result);
    }

    private String removeQuotesAndWildcards(String str)
    {
        return QUOTE_SEPARATORS_AND_WILDCARDS.matcher(str).replaceAll("");
    }

    /**
     * Builds a pattern for each possible prefix/suffix combination.
     */
    private List<String> buildPreferredTestFilePatterns(String quotedSrcFileName)
    {
        List<String> result = new ArrayList<String>();
        UserDefinedPart prefixPart = parserResult.prefix();
        UserDefinedPart suffixPart = parserResult.suffix();

        if(! prefixPart.hasAlternatives() && ! suffixPart.hasAlternatives())
        {
            result.add(SRC_FILE_VARIABLE_PATTERN.matcher(patternString).replaceAll(quoteReplacement(quotedSrcFileName)));
        }
        else if(! prefixPart.hasAlternatives())
        {
            for (String alternative : suffixPart.alternatives())
            {
                result.add(quotedSrcFileName + toPattern(suffixPart, alternative));
            }
        }
        else
        {
            for (String preAlt : prefixPart.alternatives())
            {
                if(! suffixPart.hasAlternatives())
                {
                    result.add(toPattern(prefixPart, preAlt) + quotedSrcFileName);
                }
                else
                {
                    for (String sufAlt : suffixPart.alternatives())
                    {
                        result.add(toPattern(prefixPart, preAlt) + quotedSrcFileName + toPattern(suffixPart, sufAlt));
                    }
                }
            }
        }

        return result;
    }

    /**
     * When both prefixes and suffixes are specified, builds a pattern for each
     * possible prefix or suffix, authorizing either prefix without suffix or
     * suffix without prefix.
     */
    private List<String> buildOtherCorrespondingTestFilePatterns(String quotedSrcFileName)
    {
        if(! parserResult.prefix().hasAlternatives() || ! parserResult.suffix().hasAlternatives())
        {
            return emptyList();
        }

        String beforeVar = wildCardBeforeVariable ? ".*" : "";
        String afterVar = wildCardAfterVariable ? ".*" : "";

        List<String> result = new ArrayList<String>();

        for (String preAlt : parserResult.prefix().alternatives())
        {
            result.add(toPattern(parserResult.prefix(), preAlt) + quotedSrcFileName + afterVar);
        }
        for (String sufAlt : parserResult.suffix().alternatives())
        {
            result.add(beforeVar + quotedSrcFileName + toPattern(parserResult.suffix(), sufAlt));
        }
        return result;
    }

    public String getSeparator()
    {
        return separator;
    }

    private static class Group
    {
        final List<String> alternatives;

        public Group(List<String> alternatives)
        {
            this.alternatives = alternatives;
        }

        @Override
        public String toString()
        {
            return alternatives.toString();
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
