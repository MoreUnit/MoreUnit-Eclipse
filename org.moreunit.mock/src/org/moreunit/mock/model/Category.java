package org.moreunit.mock.model;

import java.text.Collator;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import static org.moreunit.core.util.Strings.emptyIfNull;

@XmlRootElement(name = "category")
public class Category implements Comparable<Category>
{
    @XmlAttribute(required = true, name = "id")
    private String id;

    @XmlAttribute(required = true, name = "name")
    private String name;

    @SuppressWarnings("unused")
    private Category()
    {
        // used by XML unmarshaller
    }

    public Category(String id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public String id()
    {
        return id;
    }

    public String name()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return String.format("Category [id=%s]", id);
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
        Category other = (Category) obj;
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

    public int compareTo(Category otherCategory)
    {
        return Collator.getInstance().compare(emptyIfNull(name), emptyIfNull(otherCategory.name));
    }
}
