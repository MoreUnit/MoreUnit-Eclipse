package org.moreunit.core.log;

public enum Level
{
    TRACE, DEBUG, INFO, WARNING, ERROR;

    public boolean isLowerThan(Level otherLevel)
    {
        return ordinal() < otherLevel.ordinal();
    }
}
