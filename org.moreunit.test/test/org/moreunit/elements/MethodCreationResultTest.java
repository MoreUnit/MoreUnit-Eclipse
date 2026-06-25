package org.moreunit.elements;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.eclipse.jdt.core.IMethod;
import org.junit.jupiter.api.Test;

public class MethodCreationResultTest
{
    @Test
    public void should_create_result_for_existing_method()
    {
        IMethod method = mock(IMethod.class);
        MethodCreationResult result = MethodCreationResult.methodAlreadyExists(method);

        assertTrue(result.methodAlreadyExists());
        assertFalse(result.methodCreated());
        assertNotNull(result.getMethod());
    }

    @Test
    public void should_create_result_for_created_method()
    {
        IMethod method = mock(IMethod.class);
        MethodCreationResult result = MethodCreationResult.from(method);

        assertFalse(result.methodAlreadyExists());
        assertTrue(result.methodCreated());
        assertNotNull(result.getMethod());
    }

    @Test
    public void should_create_result_when_no_method_created()
    {
        MethodCreationResult result = MethodCreationResult.noMethodCreated();

        assertFalse(result.methodAlreadyExists());
        assertFalse(result.methodCreated());
        assertNull(result.getMethod());
    }

    @Test
    public void should_return_null_from_from_when_method_is_null()
    {
        MethodCreationResult result = MethodCreationResult.from(null);

        assertFalse(result.methodAlreadyExists());
        assertFalse(result.methodCreated());
        assertNull(result.getMethod());
    }
}
