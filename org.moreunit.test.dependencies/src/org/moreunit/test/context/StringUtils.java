package org.moreunit.test.context;

import java.util.Arrays;
import java.util.function.Predicate;

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
        return Arrays.stream(str.split("\\Q" + separator + "\\E"))
                .filter(Predicate.not(StringUtils::isNullOrEmpty))
                .map(String::strip)
                .toArray(String[]::new);
    }

    public static boolean atLeastOneNotEmpty(String... strings)
    {
        return Arrays.stream(strings)
                .anyMatch(Predicate.not(StringUtils::isNullOrEmpty));
    }

    public static boolean isNullOrEmpty(String input)
    {
        return input == null || input.isEmpty();
    }
}
