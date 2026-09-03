package org.moreunit.mock.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class ExcludeIfTest
{
    @Test
    public void should_create_exclude_if_with_condition_type_and_value()
    {
        final ExcludeIf excludeIf = new ExcludeIf(ConditionType.TEST_TYPE, "junit5");
        assertEquals(ConditionType.TEST_TYPE, excludeIf.type());
        assertEquals("junit5", excludeIf.value());
    }

    @Test
    public void should_be_equal_when_type_and_value_match()
    {
        final ExcludeIf e1 = new ExcludeIf(ConditionType.INJECTION_TYPE, "constructor");
        final ExcludeIf e2 = new ExcludeIf(ConditionType.INJECTION_TYPE, "constructor");
        assertEquals(e1, e2);
        assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    public void should_not_be_equal_when_type_differs()
    {
        final ExcludeIf e1 = new ExcludeIf(ConditionType.INJECTION_TYPE, "constructor");
        final ExcludeIf e2 = new ExcludeIf(ConditionType.TEST_TYPE, "constructor");
        assertNotEquals(e1, e2);
    }

    @Test
    public void should_not_be_equal_to_include_if()
    {
        final ExcludeIf excludeIf = new ExcludeIf(ConditionType.TEST_TYPE, "junit5");
        final IncludeIf includeIf = new IncludeIf(ConditionType.TEST_TYPE, "junit5");
        assertNotEquals(excludeIf, includeIf);
    }

    @Test
    public void should_include_class_name_in_toString()
    {
        final ExcludeIf e = new ExcludeIf(ConditionType.TEST_TYPE, "junit5");
        final String str = e.toString();
        assertNotNull(str);
        assert(str.contains("ExcludeIf"));
    }
}
