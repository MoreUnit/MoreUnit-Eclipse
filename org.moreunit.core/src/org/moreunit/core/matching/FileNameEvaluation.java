package org.moreunit.core.matching;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.moreunit.core.util.StringConstants;

/**
 * The result of a file name evaluation by a {@link TestFileNamePattern pattern}
 * . It contains answers to the following questions:
 * <ul>
 * <li>Does the evaluated file name correspond to a <strong>source file</strong>
 * or a <strong>test file</strong>, according to the pattern?</li>
 * <li>If it's the name of a <em>source</em> file: what <em>test</em> file
 * name(s) could correspond to it?
 * <li>If it's the name of a <em>test</em> file: what <em>source</em> file
 * name(s) could correspond to it?</li>
 * </ul>
 */
public final class FileNameEvaluation
{
    private static final Pattern QUOTE_SEPARATORS = Pattern.compile("(?:\\\\Q|\\\\E)");
    private static final Pattern SUCCESSIVE_QUOTE_SEPARATORS = Pattern.compile("\\\\E\\\\Q");
    private static final Pattern WILDCARDS = Pattern.compile("\\.\\*");

    private final String evaluatedFileName;
    private final boolean testFile;
    private final Collection<String> otherCorrespondingFilePatterns;
    private final Collection<String> preferredCorrespondingFilePatterns;
    private final String preferredCorrespondingFileName;

    public FileNameEvaluation(String evaluatedFileName, boolean testFile, String preferredCorrespondingFileName, Collection<String> preferredCorrespondingFilePatterns, Collection<String> otherCorrespondingFilePatterns)
    {
        this.evaluatedFileName = evaluatedFileName;
        this.testFile = testFile;
        this.preferredCorrespondingFileName = preferredCorrespondingFileName;
        this.preferredCorrespondingFilePatterns = simplify(preferredCorrespondingFilePatterns);
        this.otherCorrespondingFilePatterns = simplify(otherCorrespondingFilePatterns);
    }

    private static Collection<String> simplify(Collection<String> patterns)
    {
        List<String> result = new ArrayList<String>();
        for (String pattern : patterns)
        {
            result.add(SUCCESSIVE_QUOTE_SEPARATORS.matcher(pattern).replaceAll(""));
        }
        return result;
    }

    /**
     * Is the evaluated name the one of a <em>source</em> file or of a
     * <em>test</em> file?
     */
    public boolean isTestFile()
    {
        return testFile;
    }

    public Collection<String> getAllCorrespondingFilePatterns()
    {
        Collection<String> result = new ArrayList<String>(preferredCorrespondingFilePatterns.size() + otherCorrespondingFilePatterns.size());
        result.addAll(preferredCorrespondingFilePatterns);
        result.addAll(otherCorrespondingFilePatterns);
        return result;
    }

    public List<String> getAllCorrespondingFileEclipsePatterns()
    {
        List<String> result = new ArrayList<String>(preferredCorrespondingFilePatterns.size() + otherCorrespondingFilePatterns.size());
        for (String p : preferredCorrespondingFilePatterns)
        {
            result.add(convertWildcards(removeQuotes(p)));
        }
        for (String p : otherCorrespondingFilePatterns)
        {
            result.add(convertWildcards(removeQuotes(p)));
        }
        return result;
    }

    private String convertWildcards(String str)
    {
        return WILDCARDS.matcher(str).replaceAll("*");
    }

    /**
     * Returns a collection of preferred <em>test</em> file name
     * <strong>patterns</strong> corresponding to the evaluated <em>source</em>
     * file name - respectively of preferred <em>source</em> file name patterns
     * corresponding to the evaluated <em>test</em> file name (may contain only
     * one pattern).
     * <p>
     * Other - non-preferred - names may be retrieved with a call to
     * {@link #getOtherCorrespondingFilePatterns()}.
     * </p>
     */
    public Collection<String> getPreferredCorrespondingFilePatterns()
    {
        return preferredCorrespondingFilePatterns;
    }

    /**
     * Returns a collection of non-preferred file name <strong>patterns</strong>
     * corresponding to the evaluated file name (may be empty). See
     * {@link #getPreferredCorrespondingFilePatterns()} for preferred patterns,
     * and for more explanation.
     */
    public Collection<String> getOtherCorrespondingFilePatterns()
    {
        return otherCorrespondingFilePatterns;
    }

    /**
     * Returns a single preferred <em>test</em> file <strong>name</strong>
     * corresponding to the evaluated <em>source</em> file name - respectively a
     * single preferred <em>source</em> file name corresponding to the evaluated
     * <em>test</em> file name.
     */
    public String getPreferredCorrespondingFileName()
    {
        return preferredCorrespondingFileName;
    }

    private String removeQuotes(String str)
    {
        return QUOTE_SEPARATORS.matcher(str).replaceAll("");
    }

    @Override
    public String toString()
    {
        return String.format("%s(%s\t%s%s\t%s%s\t%s%s\t%s%s)", //
                             getClass().getSimpleName(), StringConstants.NEWLINE, //
                             evaluatedFileName, StringConstants.NEWLINE, //
                             (testFile ? "test file" : "src file"), StringConstants.NEWLINE, //
                             preferredCorrespondingFileName, StringConstants.NEWLINE, //
                             preferredCorrespondingFilePatterns, StringConstants.NEWLINE, //
                             otherCorrespondingFilePatterns, StringConstants.NEWLINE);
    }
}
