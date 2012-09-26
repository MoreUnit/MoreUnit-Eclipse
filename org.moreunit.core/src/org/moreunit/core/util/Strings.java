package org.moreunit.core.util;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Strings
{
    private static final String[] EMPTY_ARRAY = new String[0];

    public static boolean isBlank(String string)
    {
        return string == null || string.trim().length() == 0;
    }

    public static String emptyIfNull(String string)
    {
        return string == null ? "" : string;
    }

    public static String nullIfBlank(String string)
    {
        return isBlank(string) ? null : string;
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

    public static String[] split(String str, String regex)
    {
        List<String> parts = splitAsList(str, regex);
        return parts.toArray(new String[parts.size()]);
    }

    public static List<String> splitAsList(String str, String regex)
    {
        List<String> parts = new ArrayList<String>();
        for (String part : str.split(regex))
        {
            String trimmed = part.trim();
            if(trimmed.length() != 0)
            {
                parts.add(trimmed);
            }
        }
        return parts;
    }

    public static String ucFirst(String str)
    {
        if(isEmpty(str))
        {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String join(String separator, String... strings)
    {
        return join(separator, asList(strings));
    }

    public static String join(String separator, Collection<String> strings)
    {
        StringBuilder sb = new StringBuilder();
        join(sb, separator, strings);
        return sb.toString();
    }

    public static void join(StringBuilder sb, String separator, String[] strings)
    {
        join(sb, separator, asList(strings));
    }

    public static void join(StringBuilder sb, String separator, Collection<String> strings)
    {
        for (Iterator<String> it = strings.iterator(); it.hasNext();)
        {
            String str = (String) it.next();
            sb.append(str);
            if(it.hasNext())
            {
                sb.append(separator);
            }
        }
    }

    public static String[] emptyArray()
    {
        return EMPTY_ARRAY;
    }
}
