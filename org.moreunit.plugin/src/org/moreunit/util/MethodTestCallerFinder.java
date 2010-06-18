package org.moreunit.util;

import static org.moreunit.util.MethodCallFinder.Direction.CALLER;

import org.eclipse.jdt.core.IMethod;
import org.moreunit.elements.TypeFacade;

public class MethodTestCallerFinder extends MethodCallFinder
{

    public MethodTestCallerFinder(IMethod methodUnderTest)
    {
        super(methodUnderTest, CALLER);
    }

    protected boolean methodMatch(IMethod method)
    {
        return TypeFacade.isTestCase(method.getCompilationUnit().findPrimaryType());
    }
}
