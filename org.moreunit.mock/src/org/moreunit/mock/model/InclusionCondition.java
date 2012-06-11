package org.moreunit.mock.model;

import javax.xml.bind.annotation.XmlAttribute;

public abstract class InclusionCondition
{
    @XmlAttribute(name = "condition", required = true)
    private ConditionType type;

    @XmlAttribute(required = true)
    private String value;

    protected InclusionCondition()
    {
        // used by XML unmarshaller
    }

    protected InclusionCondition(ConditionType type, String value)
    {
        this.type = type;
        this.value = value;
    }

    public ConditionType type()
    {
        return type;
    }

    public String value()
    {
        return value;
    }

    public <E extends Enum<E>> Enum<E> valueAs(Class< ? extends Enum<E>> enumClass)
    {
        for (Enum<E> e : enumClass.getEnumConstants())
        {
            if(e.name().equals(value))
            {
                return e;
            }
        }
        return null;
    }

    public boolean isValid()
    {
        return type != null && value != null && type.isValidValue(value);
    }

    @Override
    public final int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public final boolean equals(Object obj)
    {
        if(this == obj)
        {
            return true;
        }
        if(obj == null)
        {
            return false;
        }
        if(getClass() != obj.getClass())
        {
            return false;
        }
        InclusionCondition other = (InclusionCondition) obj;
        if(type != other.type)
        {
            return false;
        }
        if(value == null)
        {
            if(other.value != null)
            {
                return false;
            }
        }
        else if(! value.equals(other.value))
        {
            return false;
        }
        return true;
    }

    @Override
    public final String toString()
    {
        return String.format("%s [%s = %s]", getClass().getSimpleName(), type, value);
    }
}
