package org.moreunit.util;

import org.eclipse.jdt.core.Signature;

public final class JavaType
{
    private final String qualifiedName;
    private final String simpleName;
    private final String qualifier;

    public JavaType(String possiblyFullyQualifiedTypeName)
    {
        qualifiedName = possiblyFullyQualifiedTypeName;
        simpleName = Signature.getSimpleName(possiblyFullyQualifiedTypeName);
        qualifier = Signature.getQualifier(possiblyFullyQualifiedTypeName);
    }

    public JavaType(String simpleName, String packageName)
    {
        qualifiedName = packageName + "." + simpleName;
        this.simpleName = simpleName;
        qualifier = packageName;
    }

    public String getQualifiedName()
    {
        return qualifiedName;
    }

    public String getSimpleName()
    {
        return simpleName;
    }

    public String getQualifier()
    {
        return qualifier;
    }

    public String getQualifierWithFinalDot()
    {
        return qualifier.length() == 0 ? qualifier : qualifier + ".";
    }
}
