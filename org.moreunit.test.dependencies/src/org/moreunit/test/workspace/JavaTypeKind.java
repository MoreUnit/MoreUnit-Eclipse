package org.moreunit.test.workspace;

public enum JavaTypeKind
{
    CLASS, ENUM;

    String toJavaCode()
    {
        return name().toLowerCase();
    }
}
