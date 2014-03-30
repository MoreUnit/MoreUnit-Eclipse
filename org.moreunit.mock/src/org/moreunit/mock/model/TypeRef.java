package org.moreunit.mock.model;

import static org.moreunit.core.util.Preconditions.checkNotNull;

public class TypeRef<T extends TypeRef<T>>
{
    public final String fullyQualifiedClassName;
    public final String simpleClassName;

    public TypeRef(String fullyQualifiedClassName)
    {
        this.fullyQualifiedClassName = checkNotNull(fullyQualifiedClassName);
        simpleClassName = getSimpleName(fullyQualifiedClassName);
    }

    private static String getSimpleName(String fullyQualifiedClassName)
    {
        return fullyQualifiedClassName.substring(fullyQualifiedClassName.lastIndexOf(".") + 1);
    }

    @Override
    public int hashCode()
    {
        return fullyQualifiedClassName.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if(this == obj)
        {
            return true;
        }
        if(obj == null || obj.getClass() != getClass())
        {
            return false;
        }

        @SuppressWarnings("rawtypes")
        TypeRef other = (TypeRef) obj;

        if(fullyQualifiedClassName == null)
        {
            if(other.fullyQualifiedClassName != null)
            {
                return false;
            }
        }
        else if(! fullyQualifiedClassName.equals(other.fullyQualifiedClassName))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return String.format("TypeRef[%s]", fullyQualifiedClassName);
    }
}
