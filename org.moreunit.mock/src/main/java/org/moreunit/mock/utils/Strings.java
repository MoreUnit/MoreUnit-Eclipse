package org.moreunit.mock.utils;

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
}
