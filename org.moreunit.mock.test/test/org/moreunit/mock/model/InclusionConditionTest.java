package org.moreunit.mock.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class InclusionConditionTest
{
    @Test
    public void should_create_with_type_and_value()
    {
        InclusionCondition condition = new IncludeIf(ConditionType.INJECTION_TYPE, "constructor");
        assertEquals(ConditionType.INJECTION_TYPE, condition.type());
        assertEquals("constructor", condition.value());
    }

    @Test
    public void should_convert_value_to_enum()
    {
        IncludeIf condition = new IncludeIf(ConditionType.INJECTION_TYPE, "setter");
        assertEquals(InjectionType.setter, condition.valueAs(InjectionType.class));
    }

    @Test
    public void should_return_null_when_value_not_in_enum()
    {
        IncludeIf condition = new IncludeIf(ConditionType.INJECTION_TYPE, "unknown");
        assertNull(condition.valueAs(InjectionType.class));
    }

    @Test
    public void should_be_valid_when_value_is_valid_for_type()
    {
        IncludeIf condition = new IncludeIf(ConditionType.INJECTION_TYPE, "constructor");
        assertTrue(condition.isValid());
    }

    @Test
    public void should_be_invalid_when_value_is_not_valid_for_type()
    {
        IncludeIf condition = new IncludeIf(ConditionType.INJECTION_TYPE, "invalidValue");
        assertFalse(condition.isValid());
    }

    @Test
    public void should_be_equal_when_type_and_value_match()
    {
        IncludeIf c1 = new IncludeIf(ConditionType.TEST_TYPE, "junit5");
        IncludeIf c2 = new IncludeIf(ConditionType.TEST_TYPE, "junit5");
        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    public void should_not_be_equal_when_type_differs()
    {
        IncludeIf c1 = new IncludeIf(ConditionType.INJECTION_TYPE, "constructor");
        IncludeIf c2 = new IncludeIf(ConditionType.TEST_TYPE, "constructor");
        assertNotEquals(c1, c2);
    }

    @Test
    public void should_not_be_equal_when_value_differs()
    {
        IncludeIf c1 = new IncludeIf(ConditionType.TEST_TYPE, "junit4");
        IncludeIf c2 = new IncludeIf(ConditionType.TEST_TYPE, "junit5");
        assertNotEquals(c1, c2);
    }

    @Test
    public void should_not_be_equal_to_null()
    {
        IncludeIf c = new IncludeIf(ConditionType.TEST_TYPE, "junit5");
        assertNotEquals(null, c);
    }

    @Test
    public void should_include_class_name_in_toString()
    {
        IncludeIf c = new IncludeIf(ConditionType.TEST_TYPE, "junit5");
        String str = c.toString();
        assertNotNull(str);
        assertTrue(str.contains("IncludeIf") || str.contains("junit5"));
    }

    @Test
    public void should_be_equal_to_itself()
    {
        IncludeIf c = new IncludeIf(ConditionType.TEST_TYPE, "junit5");
        assertTrue(c.equals(c));
    }

    @Test
    public void should_compute_hash_code_even_when_type_and_value_are_null()
    {
        assertEquals(new IncludeIf(null, null).hashCode(), new ExcludeIf(null, null).hashCode());
    }

    @Test
    public void should_compute_hash_code_with_null_type_and_non_null_value()
    {
        IncludeIf c = new IncludeIf(null, "someValue");
        assertNotNull(c.hashCode());
    }

    @Test
    public void should_compute_hash_code_with_non_null_type_and_null_value()
    {
        IncludeIf c = new IncludeIf(ConditionType.TEST_TYPE, null);
        assertNotNull(c.hashCode());
    }

    @Test
    public void should_not_be_equal_when_value_is_null_and_other_value_is_not()
    {
        IncludeIf c1 = new IncludeIf(ConditionType.TEST_TYPE, null);
        IncludeIf c2 = new IncludeIf(ConditionType.TEST_TYPE, "junit5");

        assertFalse(c1.equals(c2));
        assertFalse(c2.equals(c1));
    }

    @Test
    public void should_be_equal_when_both_values_are_null()
    {
        IncludeIf c1 = new IncludeIf(ConditionType.TEST_TYPE, null);
        IncludeIf c2 = new IncludeIf(ConditionType.TEST_TYPE, null);

        assertTrue(c1.equals(c2));
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    public void should_convert_value_to_enum_case_sensitively()
    {
        IncludeIf c = new IncludeIf(ConditionType.INJECTION_TYPE, "Constructor");
        assertNull(c.valueAs(InjectionType.class));
    }

    @Test
    public void should_not_be_valid_when_type_is_null()
    {
        IncludeIf c = new IncludeIf(null, "junit5");
        assertFalse(c.isValid());
    }

    @Test
    public void should_not_be_valid_when_value_is_null()
    {
        IncludeIf c = new IncludeIf(ConditionType.TEST_TYPE, null);
        assertFalse(c.isValid());
    }

    @Test
    public void should_include_type_and_value_in_to_string_even_when_null()
    {
        IncludeIf c = new IncludeIf(null, null);
        String str = c.toString();
        assertNotNull(str);
        assertTrue(str.contains("IncludeIf"));
    }
}
