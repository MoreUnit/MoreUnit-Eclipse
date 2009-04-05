package org.moreunit.util;

import java.util.Comparator;

public class StringLengthComparator implements Comparator<String>
{

    public int compare(String aString, String bString)
    {
        return aString.length() - bString.length();
    }
}
