package org.moreunit.mock.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.moreunit.mock.templates.MockingContext;

public class CodeTemplateTest
{
    @BeforeEach
    public void initMocks() {
        MockitoAnnotations.openMocks(this);
    }
    @Mock
    private MockingContext context;

    private Set<InclusionCondition> conditions = new HashSet<>();
    private CodeTemplate codeTemplate = new CodeTemplate(null, null, null, conditions);

    @Test
    public void should_not_be_included_if_it_has_an_exclusion_condition() throws Exception
    {
        // given
        conditions.add(new ExcludeIf(ConditionType.INJECTION_TYPE, InjectionType.constructor.name()));

        when(context.usesInjectionType(InjectionType.constructor)).thenReturn(true);

        // when
        assertFalse(codeTemplate.isIncluded(context));
    }

    @Test
    public void should_not_be_included_if_it_misses_an_inclusion_condition() throws Exception
    {
        // given
        conditions.add(new IncludeIf(ConditionType.INJECTION_TYPE, InjectionType.setter.name()));

        when(context.usesInjectionType(InjectionType.setter)).thenReturn(false);

        // when
        assertFalse(codeTemplate.isIncluded(context));
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
        assertFalse(codeTemplate.isIncluded(context));
    }

    @Test
    public void should_not_be_included_if_any_condition_fails() throws Exception
    {
        // given
        conditions.add(new ExcludeIf(ConditionType.INJECTION_TYPE, InjectionType.constructor.name()));
        conditions.add(new ExcludeIf(ConditionType.INJECTION_TYPE, InjectionType.setter.name()));

        when(context.usesInjectionType(InjectionType.setter)).thenReturn(true);

        // when
        assertFalse(codeTemplate.isIncluded(context));
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
        assertTrue(codeTemplate.isIncluded(context));
    }

    @Test
    public void should_be_included_when_it_has_no_condition()
    {
        // given
        CodeTemplate templateWithoutConditions = new CodeTemplate("id", Part.BEFORE_INSTANCE_METHOD, "pattern");

        // when
        assertTrue(templateWithoutConditions.isIncluded(context));

        // then
        verifyNoInteractions(context);
    }

    @Test
    public void should_expose_id_part_and_pattern()
    {
        CodeTemplate template = new CodeTemplate("an.id", Part.TEST_CLASS_FIELDS, "a pattern");

        assertEquals("an.id", template.id());
        assertEquals(Part.TEST_CLASS_FIELDS, template.part());
        assertEquals("a pattern", template.pattern());
    }

    @Test
    public void should_be_equal_to_itself()
    {
        CodeTemplate template = new CodeTemplate("an.id", null, null);

        assertTrue(template.equals(template));
    }

    @Test
    public void should_not_be_equal_when_id_differs()
    {
        CodeTemplate template1 = new CodeTemplate("id.1", null, null);
        CodeTemplate template2 = new CodeTemplate("id.2", null, null);

        assertFalse(template1.equals(template2));
    }

    @Test
    public void should_not_be_equal_when_id_is_null_and_other_id_is_not()
    {
        CodeTemplate template1 = new CodeTemplate(null, null, null);
        CodeTemplate template2 = new CodeTemplate("id.2", null, null);

        assertFalse(template1.equals(template2));
        assertFalse(template2.equals(template1));
    }

    @Test
    public void should_not_be_equal_to_null_or_to_object_of_different_class()
    {
        CodeTemplate template = new CodeTemplate("an.id", null, null);

        assertFalse(template.equals(null));
        Object objectOfDifferentClass = "an.id";
        assertFalse(template.equals(objectOfDifferentClass));
    }

    @Test
    public void should_have_same_hash_code_when_ids_match()
    {
        CodeTemplate template1 = new CodeTemplate("an.id", null, null);
        CodeTemplate template2 = new CodeTemplate("an.id", Part.TEST_CLASS_FIELDS, "another pattern");

        assertEquals(template1.hashCode(), template2.hashCode());
        assertTrue(template1.equals(template2));
    }

    @Test
    public void should_compute_hash_code_even_when_id_is_null()
    {
        CodeTemplate template = new CodeTemplate(null, null, null);

        assertEquals(31, template.hashCode());
    }

    @Test
    public void should_include_id_part_and_pattern_in_to_string()
    {
        CodeTemplate template = new CodeTemplate("an.id", Part.TEST_CLASS_FIELDS, "a pattern");
        String str = template.toString();

        assertNotNull(str);
        assertTrue(str.contains("an.id"));
        assertTrue(str.contains("TEST_CLASS_FIELDS"));
        assertTrue(str.contains("a pattern"));
    }
}
