package org.moreunit.test.workspace;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

public class JavaType
{
    public final JavaTypeKind typeKind;
    public final String packageName;
    public final String typeName;

    private JavaType(JavaTypeKind typeKind, String packageName, String typeName)
    {
        this.typeKind = typeKind;
        this.packageName = checkNotNull(packageName);
        this.typeName = checkNotNull(typeName);
    }

    public static JavaType newEnum(String packageName, String typeName)
    {
        return new JavaType(JavaTypeKind.ENUM, packageName, typeName);
    }

    public static JavaType newClass(String packageName, String typeName)
    {
        return new JavaType(JavaTypeKind.CLASS, packageName, typeName);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(typeName);
    }

    @Override
    public boolean equals(Object other)
    {
        if(other == this)
            return true;
        if(! (other instanceof JavaType))
            return false;
        JavaType o = (JavaType) other;
        return Objects.equal(typeName, o.typeName) && Objects.equal(packageName, o.packageName) && Objects.equal(typeKind, o.typeKind);
    }

    @Override
    public String toString()
    {
        if("".equals(packageName))
        {
            return typeName;
        }
        return packageName + "." + typeName;
    }
}
