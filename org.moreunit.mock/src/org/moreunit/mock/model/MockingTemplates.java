package org.moreunit.mock.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "mocking-templates")
public class MockingTemplates implements Iterable<MockingTemplate>
{
    @XmlElementRefs(value = { @XmlElementRef(type = Category.class) })
    private List<Category> categories;

    @XmlElementRefs(value = { @XmlElementRef(type = MockingTemplate.class) })
    private List<MockingTemplate> mockingTemplates;

    @SuppressWarnings("unused")
    private MockingTemplates()
    {
        // used by XML unmarshaller
    }

    public MockingTemplates(List<Category> categories, List<MockingTemplate> mockingTemplates)
    {
        this.categories = categories;
        this.mockingTemplates = mockingTemplates;
    }

    public Collection<Category> categories()
    {
        return categories;
    }

    public Iterator<MockingTemplate> iterator()
    {
        return mockingTemplates.iterator();
    }

    public MockingTemplate findTemplate(String templateId)
    {
        for (MockingTemplate template : mockingTemplates)
        {
            if(templateId.equals(template.id()))
            {
                return template;
            }
        }
        return null;
    }
}
