package org.moreunit.mock.model;

import java.util.ArrayList;
import java.util.List;

public class TypeParameter
{
    public final String fullyQualifiedClassName;
    public final List<TypeParameter> internalParameters;
    public final String simpleClassName;

    public TypeParameter(String fullyQualifiedClassName)
    {
        this(fullyQualifiedClassName, new ArrayList<TypeParameter>());
    }

    public TypeParameter(String fullyQualifiedClassName, List<TypeParameter> internalParameters)
    {
        this.fullyQualifiedClassName = fullyQualifiedClassName;
        this.internalParameters = internalParameters;
        simpleClassName = fullyQualifiedClassName.substring(fullyQualifiedClassName.lastIndexOf(".") + 1);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fullyQualifiedClassName == null) ? 0 : fullyQualifiedClassName.hashCode());
        result = prime * result + ((internalParameters == null) ? 0 : internalParameters.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(this == obj)
        {
            return true;
        }
        if(obj == null)
        {
            return false;
        }
        if(! (obj instanceof TypeParameter))
        {
            return false;
        }
        TypeParameter other = (TypeParameter) obj;
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
        if(internalParameters == null)
        {
            if(other.internalParameters != null)
            {
                return false;
            }
        }
        else if(! internalParameters.equals(other.internalParameters))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return String.format("TypeParameter [className=%s, internalParams=%s]", fullyQualifiedClassName, internalParameters);
    }
}
