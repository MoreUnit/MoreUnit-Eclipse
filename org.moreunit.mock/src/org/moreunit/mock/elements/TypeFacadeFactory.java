package org.moreunit.mock.elements;

import org.eclipse.jdt.core.ICompilationUnit;
import org.moreunit.elements.ClassTypeFacade;
import org.moreunit.elements.TestCaseTypeFacade;
import org.moreunit.elements.TypeFacade;

public class TypeFacadeFactory
{
    public boolean isTestCase(ICompilationUnit compilationUnit)
    {
        return TypeFacade.isTestCase(compilationUnit);
    }

    public TypeFacade createFacade(ICompilationUnit compilationUnit)
    {
        return TypeFacade.createFacade(compilationUnit);
    }

    public TestCaseTypeFacade createTestCaseFacade(ICompilationUnit compilationUnit)
    {
        return new TestCaseTypeFacade(compilationUnit);
    }

    public ClassTypeFacade createClassFacade(ICompilationUnit compilationUnit)
    {
        return new ClassTypeFacade(compilationUnit);
    }
}
