package org.moreunit.mock.model;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

import org.moreunit.preferences.PreferenceConstants;

import static java.util.Arrays.asList;

@XmlEnum
public enum ConditionType
{
    @XmlEnumValue("injection-type")
    INJECTION_TYPE("constructor", "setter", "field"),

    @XmlEnumValue("test-type")
    TEST_TYPE(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_3, PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4, PreferenceConstants.TEST_TYPE_VALUE_TESTNG);

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
