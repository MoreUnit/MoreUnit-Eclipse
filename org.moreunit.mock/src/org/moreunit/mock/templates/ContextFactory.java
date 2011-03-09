package org.moreunit.mock.templates;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;

public class ContextFactory
{
    public MockingContext createMockingContext(IType classUnderTest, ICompilationUnit compilationUnit) throws MockingTemplateException
    {
        return new MockingContext(classUnderTest, compilationUnit);
    }

    public EclipseTemplateContext createEclipseTemplateContext(MockingContext mockingContext)
    {
        return new EclipseTemplateContext(mockingContext);
    }
}
