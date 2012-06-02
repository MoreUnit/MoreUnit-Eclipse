package org.moreunit.core.util;

import java.util.ArrayList;
import java.util.List;

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

    public static String[] split(String str, String separator)
    {
        List<String> parts = new ArrayList<String>();
        for (String part : str.split(separator))
        {
            String trimmed = part.trim();
            if(trimmed.length() != 0)
            {
                parts.add(trimmed);
            }
        }
        return parts.toArray(new String[parts.size()]);
    }
}
