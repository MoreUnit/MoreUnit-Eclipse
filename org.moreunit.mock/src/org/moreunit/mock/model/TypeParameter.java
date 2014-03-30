package org.moreunit.mock.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;

public class TypeParameter extends TypeUse<TypeParameter>
{
    public static enum Kind
    {
        REGULAR, WILDARD_UNBOUNDED, WILDCARD_EXTENDS, WILDCARD_SUPER
    }

    public final List<TypeAnnotation> baseTypeAnnotations = new ArrayList<TypeAnnotation>();
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
        super(fullyQualifiedClassName);
    }

    public TypeParameter withBaseTypeAnnotations(String... annotations)
    {
        return withBaseTypeAnnotations(asList(annotations));
    }

    public TypeParameter withBaseTypeAnnotations(Collection<String> annotations)
    {
        for (String a : annotations)
        {
            this.baseTypeAnnotations.add(new TypeAnnotation(a));
        }
        return this;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + super.hashCode();
        result = prime * result + ((wildcardExpression == null) ? 0 : wildcardExpression.hashCode());
        result = prime * result + ((baseTypeAnnotations == null) ? 0 : baseTypeAnnotations.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(! super.equals(obj))
        {
            return false;
        }
        TypeParameter other = (TypeParameter) obj;
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
        if(baseTypeAnnotations == null)
        {
            if(other.baseTypeAnnotations != null)
            {
                return false;
            }
        }
        else if(! baseTypeAnnotations.equals(other.baseTypeAnnotations))
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
        return String.format("TypeParameter[\nwildcardExpression=\"%s\", \nclassName=%s, \nannotations=%s, \nbaseTypeAnnotations=%s, \ntypeParams=%s]", wildcardExpression, fullyQualifiedClassName, annotations, baseTypeAnnotations, typeParameters);
    }
}
