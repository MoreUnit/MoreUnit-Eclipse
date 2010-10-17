package org.moreunit.util;

import java.util.Collection;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.moreunit.elements.TypeFacade;

public class MethodTestCallerFinder extends MethodCallFinder
{

    public MethodTestCallerFinder(IMethod methodUnderTest, Collection< ? extends IJavaElement> searchScope)
    {
        super(methodUnderTest, searchScope, Direction.CALLER);
    }

    protected boolean methodMatch(IMethod method)
    {
        return TypeFacade.isTestCase(method.getCompilationUnit().findPrimaryType());
    }
}
