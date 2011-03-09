package org.moreunit.mock.model;

import static org.moreunit.util.Preconditions.checkArgument;
import static org.moreunit.util.Preconditions.checkNotNull;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;

public class Dependency implements Comparable<Dependency>
{
    public final String fullyQualifiedClassName;
    public final String simpleClassName;
    public final String name;
    public final List<TypeParameter> typeParameters;

    public Dependency(String fullyQualifiedClassName, String name)
    {
        this(fullyQualifiedClassName, name, new ArrayList<TypeParameter>());
    }

    public Dependency(String fullyQualifiedClassName, String name, List<TypeParameter> typeParameters)
    {
        this.fullyQualifiedClassName = checkNotNull(fullyQualifiedClassName);
        checkArgument(fullyQualifiedClassName.length() > 0);
        this.name = checkNotNull(name);
        checkArgument(name.length() > 0);
        this.typeParameters = checkNotNull(typeParameters);

        simpleClassName = fullyQualifiedClassName.substring(fullyQualifiedClassName.lastIndexOf(".") + 1);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fullyQualifiedClassName == null) ? 0 : fullyQualifiedClassName.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        if(! (obj instanceof Dependency))
        {
            return false;
        }
        Dependency other = (Dependency) obj;
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
        if(name == null)
        {
            if(other.name != null)
            {
                return false;
            }
        }
        else if(! name.equals(other.name))
        {
            return false;
        }
        return true;
    }

    public int compareTo(Dependency otherDependency)
    {
        return Collator.getInstance().compare(name, otherDependency.name);
    }

    @Override
    public String toString()
    {
        return String.format("Dependency[%s]", name);
    }
}
