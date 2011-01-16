package org.moreunit.mock.templates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class MockingTemplateTest
{
    private MockingTemplates mockingTemplates = new MockingTemplates(new MockingTemplate("a template"), new MockingTemplate("another template"));

    @Test
    public void should_return_null_when_id_is_unknwon() throws Exception
    {
        assertNull(mockingTemplates.findTemplate("unknown"));
    }

    @Test
    public void should_return_template_when_id_is_knwon() throws Exception
    {
        assertEquals(new MockingTemplate("a template"), mockingTemplates.findTemplate("a template"));
        assertEquals(new MockingTemplate("another template"), mockingTemplates.findTemplate("another template"));
    }
}
