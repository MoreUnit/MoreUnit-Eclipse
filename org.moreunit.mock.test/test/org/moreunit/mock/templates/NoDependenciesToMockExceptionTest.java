package org.moreunit.mock.templates;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.jdt.core.IType;
import org.junit.jupiter.api.Test;

public class NoDependenciesToMockExceptionTest
{
    @Test
    public void should_throw_exception_for_class_with_no_dependencies()
    {
        IType classUnderTest = mock(IType.class);
        when(classUnderTest.getElementName()).thenReturn("MyClass");

        NoDependenciesToMockException exception = new NoDependenciesToMockException(classUnderTest);

        assertNotNull(exception);
        // message contains class name
        assertNotNull(exception.getMessage());
        assertSame(MockingTemplateException.class, exception.getClass().getSuperclass());
    }
}
