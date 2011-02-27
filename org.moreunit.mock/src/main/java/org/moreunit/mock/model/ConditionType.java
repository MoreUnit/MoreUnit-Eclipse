package org.moreunit.mock.model;

import static java.util.Arrays.asList;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum ConditionType
{
    @XmlEnumValue("injection-type")
    INJECTION_TYPE("constructor", "setter", "field");

    private final Set<String> validValues;

    private ConditionType(String... validValues)
    {
        this.validValues = validValues == null ? null : new HashSet<String>(asList(validValues));
    }

    public boolean isValidValue(String value)
    {
        return validValues == null || validValues.contains(value);
    }
}
