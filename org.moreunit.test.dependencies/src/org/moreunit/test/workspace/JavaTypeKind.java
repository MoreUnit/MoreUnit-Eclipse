package org.moreunit.test.workspace;

public enum JavaTypeKind
{
    CLASS, ENUM, INTERFACE;

    String toJavaCode()
    {
        return name().toLowerCase();
    }
}
