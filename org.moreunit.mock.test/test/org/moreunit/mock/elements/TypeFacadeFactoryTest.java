package org.moreunit.mock.elements;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import org.eclipse.jdt.core.ICompilationUnit;
import org.junit.jupiter.api.Test;

public class TypeFacadeFactoryTest
{
    @Test
    public void should_create_and_check()
    {
        final TypeFacadeFactory factory = new TypeFacadeFactory();
        assertFalse(factory.isTestCase(mock(ICompilationUnit.class)));
    }

    @Test
    public void should_create_facade()
    {
        final TypeFacadeFactory factory = new TypeFacadeFactory();
        assertNotNull(factory.createFacade(mock(ICompilationUnit.class)));
    }
}
