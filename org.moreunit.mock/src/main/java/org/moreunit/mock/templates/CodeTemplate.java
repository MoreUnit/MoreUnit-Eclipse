package org.moreunit.mock.templates;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "code-template")
public class CodeTemplate
{
    @XmlAttribute
    private String id;

    @XmlAttribute
    private String part;

    @XmlElement
    private String pattern;

    public CodeTemplate()
    {
    }

    public CodeTemplate(String id, String part, String pattern)
    {
        this.id = id;
        this.part = part;
        this.pattern = pattern;
    }

    public String id()
    {
        return id;
    }

    public String part()
    {
        return part;
    }

    public String pattern()
    {
        return pattern;
    }

    @Override
    public String toString()
    {
        return String.format("CodeTemplate [id=%s, part=%s, pattern=%s]", id, part, pattern);
    }

}
