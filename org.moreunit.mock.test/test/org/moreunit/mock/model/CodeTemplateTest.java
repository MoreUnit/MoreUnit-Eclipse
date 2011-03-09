package org.moreunit.mock.model;

import static com.google.common.collect.Sets.newHashSet;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.moreunit.mock.templates.MockingContext;

@RunWith(MockitoJUnitRunner.class)
public class CodeTemplateTest
{
    @Mock
    private MockingContext context;

    private Set<InclusionCondition> conditions = newHashSet();
    private CodeTemplate codeTemplate = new CodeTemplate(null, null, null, conditions);

    @Test
    public void should_not_be_included_if_it_has_an_exclusion_condition() throws Exception
    {
        // given
        conditions.add(new ExcludeIf(ConditionType.INJECTION_TYPE, InjectionType.constructor.name()));

        when(context.usesInjectionType(InjectionType.constructor)).thenReturn(true);

        // when
        assertThat(codeTemplate.isIncluded(context)).isFalse();
    }

    @Test
    public void should_not_be_included_if_it_misses_an_inclusion_condition() throws Exception
    {
        // given
        conditions.add(new IncludeIf(ConditionType.INJECTION_TYPE, InjectionType.setter.name()));

        when(context.usesInjectionType(InjectionType.setter)).thenReturn(false);

        // when
        assertThat(codeTemplate.isIncluded(context)).isFalse();
    }

    @Test
    public void should_evaluate_exclusion_conditions_before_inclusion_ones() throws Exception
    {
        // given
        conditions.add(new ExcludeIf(ConditionType.INJECTION_TYPE, InjectionType.constructor.name()));
        // contradiction
        conditions.add(new IncludeIf(ConditionType.INJECTION_TYPE, InjectionType.constructor.name()));

        when(context.usesInjectionType(InjectionType.constructor)).thenReturn(true);

        // when
        assertThat(codeTemplate.isIncluded(context)).isFalse();
    }

    @Test
    public void should_not_be_included_if_any_condition_fails() throws Exception
    {
        // given
        conditions.add(new ExcludeIf(ConditionType.INJECTION_TYPE, InjectionType.constructor.name()));
        conditions.add(new ExcludeIf(ConditionType.INJECTION_TYPE, InjectionType.setter.name()));

        when(context.usesInjectionType(InjectionType.constructor)).thenReturn(false);
        when(context.usesInjectionType(InjectionType.setter)).thenReturn(true);

        // when
        assertThat(codeTemplate.isIncluded(context)).isFalse();
    }

    @Test
    public void should_be_included_when_all_conditions_pass() throws Exception
    {
        // given
        conditions.add(new ExcludeIf(ConditionType.INJECTION_TYPE, InjectionType.constructor.name()));
        conditions.add(new ExcludeIf(ConditionType.INJECTION_TYPE, InjectionType.setter.name()));
        conditions.add(new IncludeIf(ConditionType.INJECTION_TYPE, InjectionType.field.name()));

        when(context.usesInjectionType(InjectionType.constructor)).thenReturn(false);
        when(context.usesInjectionType(InjectionType.setter)).thenReturn(false);
        when(context.usesInjectionType(InjectionType.field)).thenReturn(true);

        // when
        assertThat(codeTemplate.isIncluded(context)).isTrue();
    }
}
