package org.moreunit.mock.templates;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.junit.jupiter.api.Test;
import org.moreunit.mock.dependencies.Dependencies;

public class ContextFactoryTest
{
    @Test
    public void should_create_mocking_context() throws MockingTemplateException
    {
        ContextFactory factory = new ContextFactory();
        MockingContext ctx = factory.createMockingContext(
            mock(Dependencies.class), mock(IType.class), "JUNIT5", mock(ICompilationUnit.class));

        assertNotNull(ctx);
    }

    @Test
    public void should_create_eclipse_template_context() throws MockingTemplateException
    {
        ContextFactory factory = new ContextFactory();
        MockingContext ctx = new MockingContext(
            mock(Dependencies.class), mock(IType.class), mock(ICompilationUnit.class), "JUNIT5",
            java.util.Collections.emptyList());

        assertNotNull(factory.createEclipseTemplateContext(ctx));
    }
}
