package org.moreunit.core.matching;

import static java.util.Arrays.asList;

import java.util.List;

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
        return asList(name.split(getSeparator()));
    }
}
