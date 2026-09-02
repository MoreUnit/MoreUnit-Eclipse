package org.moreunit.mock.templates;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.moreunit.mock.model.CodeTemplate;
import org.moreunit.mock.model.MockingTemplate;
import org.moreunit.mock.model.MockingTemplates;
import org.moreunit.mock.model.Part;

public class MockingTemplateTest
{
    private MockingTemplates mockingTemplates = new MockingTemplates(new ArrayList<>(),
                                                                     asList(new MockingTemplate("a template"), new MockingTemplate("another template")));

    @Test
    public void should_return_null_when_id_is_unknwon() throws Exception
    {
        assertNull(mockingTemplates.findTemplate("unknown"));
    }

    @Test
    public void should_return_template_when_id_is_knwon() throws Exception
    {
        assertEquals(mockingTemplates.findTemplate("a template"), new MockingTemplate("a template"));
        assertEquals(mockingTemplates.findTemplate("another template"), new MockingTemplate("another template"));
    }

    @Test
    public void should_return_empty_code_templates_when_none_was_defined() throws Exception
    {
        // given
        MockingTemplate template = new MockingTemplate("a template", "a category");

        // when + then
        assertTrue(template.codeTemplates().isEmpty());
    }

    @Test
    public void should_expose_id_and_category() throws Exception
    {
        MockingTemplate template = new MockingTemplate("a template", "a category", "a name", asList(new CodeTemplate("ct", Part.TEST_CLASS_FIELDS, "pattern")));

        assertEquals("a template", template.id());
        assertEquals("a category", template.categoryId());
        assertEquals("a name", template.name());
        assertEquals(1, template.codeTemplates().size());
    }

    @Test
    public void should_compute_hash_code_even_when_id_is_null() throws Exception
    {
        assertEquals(31, new MockingTemplate(null).hashCode());
    }

    @Test
    public void should_not_be_equal_when_id_is_null_and_other_id_is_not() throws Exception
    {
        MockingTemplate template1 = new MockingTemplate(null);
        MockingTemplate template2 = new MockingTemplate("a template");

        assertFalse(template1.equals(template2));
        assertFalse(template2.equals(template1));
    }

    @Test
    public void should_be_equal_to_itself() throws Exception
    {
        MockingTemplate template = new MockingTemplate("a template");
        assertTrue(template.equals(template));
    }

    @Test
    public void should_not_be_equal_to_null_or_to_object_of_different_class() throws Exception
    {
        MockingTemplate template = new MockingTemplate("a template");

        assertFalse(template.equals(null));
        assertFalse(template.equals("a template"));
    }

    @Test
    public void should_include_id_and_category_in_to_string() throws Exception
    {
        MockingTemplate template = new MockingTemplate("a template", "a category");
        String str = template.toString();

        assertNotNull(str);
        assertTrue(str.contains("a template"));
        assertTrue(str.contains("a category"));
    }
}
