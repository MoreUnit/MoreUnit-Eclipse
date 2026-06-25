package org.moreunit.mock.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ConditionTypeTest
{
    @Test
    public void should_accept_valid_injection_type_values()
    {
        assertTrue(ConditionType.INJECTION_TYPE.isValidValue("constructor"));
        assertTrue(ConditionType.INJECTION_TYPE.isValidValue("setter"));
        assertTrue(ConditionType.INJECTION_TYPE.isValidValue("field"));
    }

    @Test
    public void should_reject_invalid_injection_type_value()
    {
        assertFalse(ConditionType.INJECTION_TYPE.isValidValue("invalid"));
        assertFalse(ConditionType.INJECTION_TYPE.isValidValue(""));
        assertFalse(ConditionType.INJECTION_TYPE.isValidValue("Constructor"));
    }

    @Test
    public void should_accept_valid_test_type_values()
    {
        assertTrue(ConditionType.TEST_TYPE.isValidValue("junit3"));
        assertTrue(ConditionType.TEST_TYPE.isValidValue("junit4"));
        assertTrue(ConditionType.TEST_TYPE.isValidValue("junit5"));
        assertTrue(ConditionType.TEST_TYPE.isValidValue("testng"));
    }

    @Test
    public void should_reject_invalid_test_type_value()
    {
        assertFalse(ConditionType.TEST_TYPE.isValidValue("invalid"));
        assertFalse(ConditionType.TEST_TYPE.isValidValue(""));
        assertFalse(ConditionType.TEST_TYPE.isValidValue("JUnit5"));
    }

    @Test
    public void should_be_case_sensitive()
    {
        assertFalse(ConditionType.INJECTION_TYPE.isValidValue("Constructor"));
        assertFalse(ConditionType.TEST_TYPE.isValidValue("JUnit5"));
    }
}
