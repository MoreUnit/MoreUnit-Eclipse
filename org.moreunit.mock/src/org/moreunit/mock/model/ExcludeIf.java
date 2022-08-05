package org.moreunit.mock.model;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "exclude-if")
public class ExcludeIf extends InclusionCondition
{
    @SuppressWarnings("unused")
    private ExcludeIf()
    {
        // used by XML unmarshaller
        super();
    }

    public ExcludeIf(ConditionType name, String value)
    {
        super(name, value);
    }
}
