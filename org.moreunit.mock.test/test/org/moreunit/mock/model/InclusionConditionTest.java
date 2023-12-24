package org.moreunit.mock.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class InclusionConditionTest
{
    @Test
    public void should_convert_values_as_enum() throws Exception
    {
        assertThat(new InclusionCondition(ConditionType.INJECTION_TYPE, "constructor")
        {
        }.valueAs(InjectionType.class)).isEqualTo(InjectionType.constructor);

        assertThat(new InclusionCondition(ConditionType.INJECTION_TYPE, "setter")
        {
        }.valueAs(InjectionType.class)).isEqualTo(InjectionType.setter);
    }

    @Test
    public void should_return_null_if_value_is_not_in_enum() throws Exception
    {
        assertThat(new InclusionCondition(ConditionType.INJECTION_TYPE, "does not exist")
        {
        }.valueAs(InjectionType.class)).isNull();
    }
}
