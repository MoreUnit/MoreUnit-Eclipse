package org.moreunit.mock.templates;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.moreunit.mock.model.MockingTemplate;
import org.moreunit.mock.model.MockingTemplates;

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
}
