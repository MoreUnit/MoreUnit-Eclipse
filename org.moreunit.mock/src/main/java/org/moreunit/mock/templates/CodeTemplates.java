package org.moreunit.mock.templates;

import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "code-templates")
public class CodeTemplates implements Iterable<CodeTemplate>
{
    @XmlElementRefs(value = { @XmlElementRef(type = CodeTemplate.class) })
    private List<CodeTemplate> codeTemplates;

    public Iterator<CodeTemplate> iterator()
    {
        return codeTemplates.iterator();
    }
}
