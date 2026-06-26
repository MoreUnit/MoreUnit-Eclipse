package org.moreunit.mock.utils;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

/**
 * Adapted from {@link org.eclipse.pde.internal.core.util.PatternConstructor}
 *
 * @author joe.thomas-kerr
 */
public class WildcardFileFilter implements FileFilter
{
    private final Pattern wildcardPattern;

    public WildcardFileFilter(String patternString)
    {
        wildcardPattern = Pattern.compile(asRegEx(patternString));
    }

    /**
     * Converts user string with wildcards '*' and '?' to a regular expression
     * pattern.
     */
    private static String asRegEx(String pattern)
    {
        String result = pattern;

        // Replace \ with \\, * with .* and ? with .
        // Quote remaining characters
        // PERFORMANCE: Use literal String.replace instead of regex Matcher.replaceAll
        result = result.replace("\\", "\\E\\\\\\Q"); //$NON-NLS-1$
        result = result.replace("*", "\\E.*\\Q"); //$NON-NLS-1$
        result = result.replace("?", "\\E.\\Q"); //$NON-NLS-1$
        return "\\Q" + result + "\\E"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    public boolean accept(File pathname)
    {
        return wildcardPattern.matcher(pathname.getName()).matches();
    }
}
