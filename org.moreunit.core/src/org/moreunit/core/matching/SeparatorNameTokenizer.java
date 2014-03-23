package org.moreunit.core.matching;

import static java.util.Arrays.asList;
import static java.util.regex.Pattern.quote;

import java.util.List;

/**
 * Splits a name into tokens, using a given separator.
 */
public class SeparatorNameTokenizer extends NameTokenizer
{
    public SeparatorNameTokenizer(String separator)
    {
        super(checkNotNullOrEmpty(separator));
    }

    private static String checkNotNullOrEmpty(String separator)
    {
        if(separator == null || separator.length() == 0)
        {
            throw new IllegalArgumentException("Invalid separator: " + separator);
        }
        return separator;
    }

    @Override
    protected List<String> getWords(String name)
    {
        return asList(name.split(quote(separator())));
    }
}
