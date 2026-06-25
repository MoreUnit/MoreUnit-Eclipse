package org.moreunit.mock.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class InclusionConditionTest
{
    @Test
    public void should_convert_values_as_enum() throws Exception
    {
        assertEquals(new InclusionCondition(ConditionType.INJECTION_TYPE, "constructor")
        {
        }.valueAs(InjectionType.class), InjectionType.constructor);

        assertEquals(new InclusionCondition(ConditionType.INJECTION_TYPE, "setter")
        {
        }.valueAs(InjectionType.class), InjectionType.setter);
    }

    @Test
    public void should_return_null_if_value_is_not_in_enum() throws Exception
    {
        assertNull(new InclusionCondition(ConditionType.INJECTION_TYPE, "does not exist")
        {
        }.valueAs(InjectionType.class));
    }
}
