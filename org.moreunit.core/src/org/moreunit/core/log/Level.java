package org.moreunit.core.log;

public enum Level
{
    DEBUG, INFO, WARNING, ERROR;

    public boolean isLowerThan(Level otherLevel)
    {
        return ordinal() < otherLevel.ordinal();
    }
}
