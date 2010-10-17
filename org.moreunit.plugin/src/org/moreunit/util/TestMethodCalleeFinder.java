package org.moreunit.util;

import java.util.Collection;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.moreunit.elements.TypeFacade;

public class TestMethodCalleeFinder extends MethodCallFinder
{

    public TestMethodCalleeFinder(IMethod testMethod, Collection< ? extends IJavaElement> searchScope)
    {
        super(testMethod, searchScope, Direction.CALLEE);
    }

    protected boolean methodMatch(IMethod method)
    {
        return ! TypeFacade.isTestCase(method.getCompilationUnit().findPrimaryType());
    }
}
