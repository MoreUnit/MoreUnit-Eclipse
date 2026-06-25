package org.moreunit.core.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class ResourceOperationExceptionTest
{
    @Test
    public void should_create_with_cause()
    {
        RuntimeException cause = new RuntimeException("root");
        ResourceOperationException ex = new ResourceOperationException(cause);
        assertEquals(cause, ex.getCause());
        assertEquals(String.valueOf(cause), ex.getMessage());
    }

    @Test
    public void should_create_with_null_cause()
    {
        ResourceOperationException ex = new ResourceOperationException(null);
        assertNull(ex.getCause());
    }

    @Test
    public void should_be_runtime_exception()
    {
        ResourceOperationException ex = new ResourceOperationException(new RuntimeException());
        assertNotNull(ex instanceof RuntimeException);
    }
}
