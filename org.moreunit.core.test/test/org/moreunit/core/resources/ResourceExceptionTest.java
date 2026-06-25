package org.moreunit.core.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class ResourceExceptionTest
{
    @Test
    public void should_create_with_message()
    {
        ResourceException ex = new ResourceException("something went wrong");
        assertEquals("something went wrong", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    public void should_create_with_cause()
    {
        RuntimeException cause = new RuntimeException("root");
        ResourceException ex = new ResourceException(cause);
        assertEquals(cause, ex.getCause());
    }

    @Test
    public void should_create_with_message_and_cause()
    {
        RuntimeException cause = new RuntimeException("root");
        ResourceException ex = new ResourceException("wrapper", cause);
        assertEquals("wrapper", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

    @Test
    public void should_be_runtime_exception()
    {
        ResourceException ex = new ResourceException("test");
        assertNotNull(ex instanceof RuntimeException);
    }
}
