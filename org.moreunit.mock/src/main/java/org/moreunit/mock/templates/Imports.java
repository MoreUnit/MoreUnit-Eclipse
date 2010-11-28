package org.moreunit.mock.templates;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "imports")
public class Imports
{
    @XmlValue
    private String imports;

    @Override
    public String toString()
    {
        return imports;
    }
}
