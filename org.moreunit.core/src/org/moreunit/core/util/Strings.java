package org.moreunit.core.util;

public class Strings
{
    public static boolean isBlank(String string)
    {
        return string == null || string.trim().length() == 0;
    }

    public static String emptyIfNull(String string)
    {
        return string == null ? "" : string;
    }

    public static boolean isEmpty(String str)
    {
        return str == null || str.length() == 0;
    }

    public static int countOccurrences(String string, String pattern)
    {
        if(isEmpty(pattern) || isEmpty(string))
        {
            return 0;
        }

        int occurrences = 0;

        char[] pat = pattern.toCharArray();
        int patIdx = 0;

        for (char c : string.toCharArray())
        {
            if(c == pat[patIdx])
            {
                if(patIdx == pat.length - 1)
                {
                    occurrences++;
                    patIdx = 0;
                }
                else
                {
                    patIdx++;
                }
            }
        }

        return occurrences;
    }
}
