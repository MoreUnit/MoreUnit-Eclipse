package org.moreunit.util;

import static org.moreunit.util.MethodCallFinder.Direction.CALLEE;

import org.eclipse.jdt.core.IMethod;
import org.moreunit.elements.TypeFacade;

public class TestMethodCalleeFinder extends MethodCallFinder
{

    public TestMethodCalleeFinder(IMethod testMethod)
    {
        super(testMethod, CALLEE);
    }

    protected boolean methodMatch(IMethod method)
    {
        return ! TypeFacade.isTestCase(method.getCompilationUnit().findPrimaryType());
    }
}
