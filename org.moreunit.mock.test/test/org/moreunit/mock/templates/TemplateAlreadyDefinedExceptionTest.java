package org.moreunit.mock.templates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TemplateAlreadyDefinedExceptionTest
{
    @Test
    public void should_store_template_id()
    {
        TemplateAlreadyDefinedException ex = new TemplateAlreadyDefinedException("myTemplateId");
        assertEquals("myTemplateId", ex.getTemplateId());
    }

    @Test
    public void should_include_template_id_in_message()
    {
        TemplateAlreadyDefinedException ex = new TemplateAlreadyDefinedException("myTemplateId");
        assertTrue(ex.getMessage().contains("myTemplateId"));
    }

    @Test
    public void should_have_message_about_duplicate_id()
    {
        TemplateAlreadyDefinedException ex = new TemplateAlreadyDefinedException("abc");
        assertTrue(ex.getMessage().contains("already exists"));
    }
}
