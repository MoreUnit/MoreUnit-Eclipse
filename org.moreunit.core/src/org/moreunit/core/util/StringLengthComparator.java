package org.moreunit.core.util;

import java.io.Serializable;
import java.util.Comparator;

public class StringLengthComparator implements Comparator<String>, Serializable
{
    private static final long serialVersionUID = 1462228841996373439L;

    public int compare(String aString, String bString)
    {
        return aString.length() - bString.length();
    }
}
