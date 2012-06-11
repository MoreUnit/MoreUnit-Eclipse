package org.moreunit.mock.model;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

import org.moreunit.mock.templates.MockingContext;

@XmlRootElement(name = "code-template")
public class CodeTemplate
{
    @XmlAttribute(required = true)
    private String id;

    @XmlAttribute(required = true)
    private Part part;

    @XmlElement(required = true)
    private String pattern;

    @XmlElementRefs(value = { @XmlElementRef(type = ExcludeIf.class), @XmlElementRef(type = IncludeIf.class) })
    private Set<InclusionCondition> inclusionConditions;

    @SuppressWarnings("unused")
    private CodeTemplate()
    {
        // used by XML unmarshaller
    }

    public CodeTemplate(String id, Part part, String pattern)
    {
        this(id, part, pattern, new HashSet<InclusionCondition>());
    }

    public CodeTemplate(String id, Part part, String pattern, Set<InclusionCondition> inclusionConditions)
    {
        this.id = id;
        this.part = part;
        this.pattern = pattern;
        this.inclusionConditions = inclusionConditions;
    }

    public String id()
    {
        return id;
    }

    public Part part()
    {
        return part;
    }

    public String pattern()
    {
        return pattern;
    }

    public boolean isIncluded(MockingContext context)
    {
        if(inclusionConditions == null)
        {
            return true;
        }

        for (InclusionCondition condition : inclusionConditions)
        {
            if(condition instanceof ExcludeIf)
            {
                if(condition.type() == ConditionType.INJECTION_TYPE && context.usesInjectionType(condition.valueAs(InjectionType.class)))
                {
                    return false;
                }
                if(condition.type() == ConditionType.TEST_TYPE && context.isTestType(condition.value()))
                {
                    return false;
                }
            }
            else
            {
                if(condition.type() == ConditionType.INJECTION_TYPE && ! context.usesInjectionType(condition.valueAs(InjectionType.class)))
                {
                    return false;
                }
                if(condition.type() == ConditionType.TEST_TYPE && ! context.isTestType(condition.value()))
                {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public String toString()
    {
        return String.format("CodeTemplate [id=%s, part=%s, pattern=%s]", id, part, pattern);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        if(getClass() != obj.getClass())
        {
            return false;
        }
        CodeTemplate other = (CodeTemplate) obj;
        if(id == null)
        {
            if(other.id != null)
            {
                return false;
            }
        }
        else if(! id.equals(other.id))
        {
            return false;
        }
        return true;
    }
}
