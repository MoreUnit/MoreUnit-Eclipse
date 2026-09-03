package org.moreunit.mock.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class IncludeIfTest
{
    @Test
    public void should_create_include_if_with_condition_type_and_value()
    {
        final IncludeIf includeIf = new IncludeIf(ConditionType.INJECTION_TYPE, "setter");
        assertEquals(ConditionType.INJECTION_TYPE, includeIf.type());
        assertEquals("setter", includeIf.value());
    }

    @Test
    public void should_be_equal_when_type_and_value_match()
    {
        final IncludeIf i1 = new IncludeIf(ConditionType.TEST_TYPE, "junit5");
        final IncludeIf i2 = new IncludeIf(ConditionType.TEST_TYPE, "junit5");
        assertEquals(i1, i2);
        assertEquals(i1.hashCode(), i2.hashCode());
    }

    @Test
    public void should_not_be_equal_when_value_differs()
    {
        final IncludeIf i1 = new IncludeIf(ConditionType.TEST_TYPE, "junit4");
        final IncludeIf i2 = new IncludeIf(ConditionType.TEST_TYPE, "junit5");
        assertNotEquals(i1, i2);
    }

    @Test
    public void should_not_be_equal_to_exclude_if()
    {
        final IncludeIf includeIf = new IncludeIf(ConditionType.TEST_TYPE, "junit5");
        final ExcludeIf excludeIf = new ExcludeIf(ConditionType.TEST_TYPE, "junit5");
        assertNotEquals(includeIf, excludeIf);
    }

    @Test
    public void should_include_class_name_in_toString()
    {
        final IncludeIf i = new IncludeIf(ConditionType.INJECTION_TYPE, "setter");
        final String str = i.toString();
        assertNotNull(str);
        assert(str.contains("IncludeIf"));
    }
}
