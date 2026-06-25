package org.moreunit.mock.templates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class MockingTemplateExceptionTest
{
    @Test
    public void should_create_with_message_and_user_message_flag_true()
    {
        MockingTemplateException ex = new MockingTemplateException("user friendly message", true);
        assertEquals("user friendly message", ex.getMessage());
        assertTrue(ex.isUserMessage());
    }

    @Test
    public void should_create_with_message_and_default_user_message_flag_false()
    {
        MockingTemplateException ex = new MockingTemplateException("internal error");
        assertEquals("internal error", ex.getMessage());
        assertFalse(ex.isUserMessage());
    }

    @Test
    public void should_create_with_cause_and_user_message_flag_true()
    {
        RuntimeException cause = new RuntimeException("root cause");
        MockingTemplateException ex = new MockingTemplateException("user message", cause, true);
        assertEquals("user message", ex.getMessage());
        assertEquals(cause, ex.getCause());
        assertTrue(ex.isUserMessage());
    }

    @Test
    public void should_create_with_cause_and_default_user_message_flag_false()
    {
        RuntimeException cause = new RuntimeException("root cause");
        MockingTemplateException ex = new MockingTemplateException("internal error", cause);
        assertEquals("internal error", ex.getMessage());
        assertEquals(cause, ex.getCause());
        assertFalse(ex.isUserMessage());
    }

    @Test
    public void should_create_with_cause_only_and_default_user_message_flag_false()
    {
        RuntimeException cause = new RuntimeException("root cause");
        MockingTemplateException ex = new MockingTemplateException(cause);
        assertEquals(cause, ex.getCause());
        assertFalse(ex.isUserMessage());
    }
}
