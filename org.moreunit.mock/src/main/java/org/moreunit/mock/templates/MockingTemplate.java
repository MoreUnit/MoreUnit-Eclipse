package org.moreunit.mock.templates;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "mocking-template")
public class MockingTemplate
{
    @XmlAttribute
    private String id;
    @XmlAttribute
    private String name;
    @XmlElementRef
    private CodeTemplates codeTemplates;

    public String id()
    {
        return id;
    }

    public String name()
    {
        return name;
    }

    public CodeTemplates codeTemplates()
    {
        return codeTemplates;
    }

    @Override
    public String toString()
    {
        return String.format("MockingTemplate [id=%s, name=%s]", id, name);
    }

}
