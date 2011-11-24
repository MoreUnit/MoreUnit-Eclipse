package org.moreunit.test.context;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

public class StringUtils
{
    public static String firstNonBlank(String str1, String str2)
    {
        if(isBlank(str1))
        {
            return str2;
        }
        return str1.trim();
    }

    public static boolean isBlank(String str)
    {
        return str == null || str.trim().length() == 0;
    }

    public static String[] split(String str, String separator)
    {
        return Iterables.toArray(Splitter.on(separator).trimResults().omitEmptyStrings().split(str), String.class);
    }

    public static boolean atLeastOneNotEmpty(String... strings)
    {
        for (String str : strings)
        {
            if(! Strings.isNullOrEmpty(str))
            {
                return true;
            }
        }
        return false;
    }
}
