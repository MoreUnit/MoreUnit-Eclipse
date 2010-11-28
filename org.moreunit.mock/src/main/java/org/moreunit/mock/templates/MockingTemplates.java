package org.moreunit.mock.templates;

import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "mocking-templates")
public class MockingTemplates implements Iterable<MockingTemplate>
{
    @XmlElementRefs(value = { @XmlElementRef(type = MockingTemplate.class) })
    private List<MockingTemplate> mockingTemplates;

    public Iterator<MockingTemplate> iterator()
    {
        return mockingTemplates.iterator();
    }
}
