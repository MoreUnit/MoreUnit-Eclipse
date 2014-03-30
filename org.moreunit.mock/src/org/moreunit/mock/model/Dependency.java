package org.moreunit.mock.model;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;

import static org.moreunit.core.util.Preconditions.checkArgument;
import static org.moreunit.core.util.Preconditions.checkNotNull;

public class Dependency extends TypeUse<Dependency> implements Comparable<Dependency>
{
    public final String name;

    public Dependency(String fullyQualifiedClassName, String name)
    {
        this(fullyQualifiedClassName, name, new ArrayList<TypeParameter>());
    }

    public Dependency(String fullyQualifiedClassName, String name, List<TypeParameter> typeParameters)
    {
        super(fullyQualifiedClassName);
        checkArgument(fullyQualifiedClassName.length() > 0);
        this.name = checkNotNull(name);
        checkArgument(name.length() > 0);
        withTypeParameters(checkNotNull(typeParameters));
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
