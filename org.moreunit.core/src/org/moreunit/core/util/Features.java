package org.moreunit.core.util;

/**
 * This class helps managing "conditional" features, i.e. features that are not
 * automatically active, for instance because they are still in development.
 */
public class Features
{
    public static boolean isActive(String feature)
    {
        return Boolean.valueOf(System.getProperty(feature, "false"));
    }
}
