package org.moreunit.mock.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "include-if")
public class IncludeIf extends InclusionCondition
{
    @SuppressWarnings("unused")
    private IncludeIf()
    {
        // used by XML unmarshaller
        super();
    }

    public IncludeIf(ConditionType name, String value)
    {
        super(name, value);
    }
}
