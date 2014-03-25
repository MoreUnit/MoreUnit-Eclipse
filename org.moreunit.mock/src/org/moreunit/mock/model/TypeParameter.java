package org.moreunit.mock.model;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.addAll;

public class TypeParameter
{
    public static enum Kind
    {
        REGULAR, WILDARD_UNBOUNDED, WILDCARD_EXTENDS, WILDCARD_SUPER
    }

    public final String fullyQualifiedClassName;
    public final List<TypeParameter> internalParameters = new ArrayList<TypeParameter>();
    public final String simpleClassName;
    private String wildcardExpression = "";

    public static TypeParameter extending(String fullyQualifiedClassName)
    {
        TypeParameter p = new TypeParameter(fullyQualifiedClassName);
        p.wildcardExpression = "? extends ";
        return p;
    }

    public static TypeParameter superOf(String fullyQualifiedClassName)
    {
        TypeParameter p = new TypeParameter(fullyQualifiedClassName);
        p.wildcardExpression = "? super ";
        return p;
    }

    public static TypeParameter wildcard()
    {
        TypeParameter p = new TypeParameter("");
        p.wildcardExpression = "?";
        return p;
    }

    public static TypeParameter create(Kind kind, String fullyQualifiedClassName)
    {
        if(kind == Kind.WILDCARD_EXTENDS)
        {
            return extending(fullyQualifiedClassName);
        }
        else if(kind == Kind.WILDCARD_SUPER)
        {
            return superOf(fullyQualifiedClassName);
        }
        else if(kind == Kind.WILDARD_UNBOUNDED)
        {
            return wildcard();
        }
        return new TypeParameter(fullyQualifiedClassName);
    }

    public TypeParameter(String fullyQualifiedClassName)
    {
        this.fullyQualifiedClassName = fullyQualifiedClassName;
        simpleClassName = fullyQualifiedClassName.substring(fullyQualifiedClassName.lastIndexOf(".") + 1);
    }

    public TypeParameter withInternalParameters(TypeParameter... internalParameters)
    {
        addAll(this.internalParameters, internalParameters);
        return this;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fullyQualifiedClassName == null) ? 0 : fullyQualifiedClassName.hashCode());
        result = prime * result + ((internalParameters == null) ? 0 : internalParameters.hashCode());
        result = prime * result + ((wildcardExpression == null) ? 0 : wildcardExpression.hashCode());
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
        if(wildcardExpression == null)
        {
            if(other.wildcardExpression != null)
            {
                return false;
            }
        }
        else if(! wildcardExpression.equals(other.wildcardExpression))
        {
            return false;
        }
        return true;
    }

    public boolean hasName()
    {
        return ! fullyQualifiedClassName.equals("");
    }

    public String wildcardExpression()
    {
        return wildcardExpression;
    }

    @Override
    public String toString()
    {
        return String.format("TypeParameter[wildcardExpression=\"%s\", className=%s, internalParams=%s]", wildcardExpression, fullyQualifiedClassName, internalParameters);
    }
}
