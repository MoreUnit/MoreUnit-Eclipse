package org.moreunit.mock.templates;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.moreunit.mock.dependencies.Dependencies;

public class ContextFactory
{
    public MockingContext createMockingContext(Dependencies dependencies, IType classUnderTest, String testType, ICompilationUnit compilationUnit) throws MockingTemplateException
    {
        return new MockingContext(dependencies, classUnderTest, compilationUnit, testType);
    }

    public EclipseTemplateContext createEclipseTemplateContext(MockingContext mockingContext)
    {
        return new EclipseTemplateContext(mockingContext);
    }
}
