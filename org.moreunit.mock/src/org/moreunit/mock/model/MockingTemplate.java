package org.moreunit.mock.model;

import java.text.Collator;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

import static java.util.Collections.emptyList;
import static org.moreunit.core.util.Strings.emptyIfNull;

@XmlRootElement(name = "mocking-template")
public class MockingTemplate implements Comparable<MockingTemplate>
{
    @XmlAttribute(required = true, name = "id")
    private String id;

    @XmlAttribute(required = true, name = "category")
    private String categoryId;

    @XmlAttribute(required = true, name = "name")
    private String name;

    @XmlElementRefs(value = { @XmlElementRef(type = CodeTemplate.class) })
    private List<CodeTemplate> codeTemplates;

    @SuppressWarnings("unused")
    private MockingTemplate()
    {
        // used by XML unmarshaller
    }

    /** For testing only. */
    public MockingTemplate(String id)
    {
        this.id = id;
    }

    /** For testing only. */
    public MockingTemplate(String id, String categoryId)
    {
        this.id = id;
        this.categoryId = categoryId;
    }

    /** For testing only. */
    public MockingTemplate(String id, String categoryId, String name, List<CodeTemplate> codeTemplates)
    {
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
        this.codeTemplates = codeTemplates;
    }

    public String id()
    {
        return id;
    }

    public String categoryId()
    {
        return categoryId;
    }

    public String name()
    {
        return name;
    }

    public List<CodeTemplate> codeTemplates()
    {
        if(codeTemplates == null)
        {
            return emptyList();
        }
        return codeTemplates;
    }

    @Override
    public String toString()
    {
        return String.format("MockingTemplate [id=%s, category=%s]", id, categoryId);
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
        MockingTemplate other = (MockingTemplate) obj;
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

    public int compareTo(MockingTemplate otherTemplate)
    {
        return Collator.getInstance().compare(emptyIfNull(name), emptyIfNull(otherTemplate.name));
    }
}
