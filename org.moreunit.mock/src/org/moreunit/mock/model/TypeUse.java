package org.moreunit.mock.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;

public class TypeUse<T extends TypeUse<T>> extends TypeRef<TypeUse<T>>
{
    public final List<TypeAnnotation> annotations = new ArrayList<TypeAnnotation>();
    public final List<TypeParameter> typeParameters = new ArrayList<TypeParameter>();

    public TypeUse(String fullyQualifiedClassName)
    {
        super(fullyQualifiedClassName);
    }

    public T withAnnotations(String... annotations)
    {
        return withAnnotations(asList(annotations));
    }

    public T withAnnotations(Collection<String> annotations)
    {
        for (String a : annotations)
        {
            this.annotations.add(new TypeAnnotation(a));
        }
        return thisAsT();
    }

    public T withTypeParameters(TypeParameter... typeParameters)
    {
        return withTypeParameters(asList(typeParameters));
    }

    public T withTypeParameters(Collection<TypeParameter> typeParameters)
    {
        this.typeParameters.addAll(typeParameters);
        return thisAsT();
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + super.hashCode();
        result = prime * result + ((typeParameters == null) ? 0 : typeParameters.hashCode());
        result = prime * result + ((annotations == null) ? 0 : annotations.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(! super.equals(obj))
        {
            return false;
        }

        @SuppressWarnings("rawtypes")
        TypeUse other = (TypeUse) obj;

        if(typeParameters == null)
        {
            if(other.typeParameters != null)
            {
                return false;
            }
        }
        else if(! typeParameters.equals(other.typeParameters))
        {
            return false;
        }
        if(annotations == null)
        {
            if(other.annotations != null)
            {
                return false;
            }
        }
        else if(! annotations.equals(other.annotations))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return String.format("TypeUse[\nclassName=%s, \nannotations=%s, \ntypeParams=%s]", fullyQualifiedClassName, annotations, typeParameters);
    }

    @SuppressWarnings("unchecked")
    protected T thisAsT()
    {
        return (T) this;
    }

    public static class TypeAnnotation extends TypeRef<TypeAnnotation>
    {
        public TypeAnnotation(String fullyQualifiedClassName)
        {
            super(fullyQualifiedClassName);
        }
    }
}
