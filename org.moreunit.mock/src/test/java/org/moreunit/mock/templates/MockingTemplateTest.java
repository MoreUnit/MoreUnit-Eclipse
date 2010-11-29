package org.moreunit.mock.templates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class MockingTemplateTest
{
    private MockingTemplates mockingTemplates = new MockingTemplates(new MockingTemplate("a template"), new MockingTemplate("another template"));

    @Test
    public void shouldReturnNullWhenTemplateIsNotFound() throws Exception
    {
        assertNull(mockingTemplates.findTemplate("unknown"));
    }

    @Test
    public void shouldReturnTemplateWhenKnown() throws Exception
    {
        assertEquals(new MockingTemplate("a template"), mockingTemplates.findTemplate("a template"));
        assertEquals(new MockingTemplate("another template"), mockingTemplates.findTemplate("another template"));
    }
}
