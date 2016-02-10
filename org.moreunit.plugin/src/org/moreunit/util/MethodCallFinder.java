package org.moreunit.util;

import static org.moreunit.core.util.Preconditions.checkNotNull;
import static org.moreunit.core.util.Preconditions.checkNotNullOrEmpty;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.corext.callhierarchy.CallHierarchy;
import org.eclipse.jdt.internal.corext.callhierarchy.MethodWrapper;
import org.moreunit.elements.MethodFacade;

public abstract class MethodCallFinder
{

    public static enum Direction
    {
        CALLEE, CALLER
    }

    private final IJavaSearchScope searchScope;
    private final MethodWrapper methodWrapper;

    protected MethodCallFinder(IMethod method, Collection< ? extends IJavaElement> searchScope, Direction direction)
    {
        checkNotNull(method, "Method can not be null");
        checkNotNullOrEmpty(searchScope, "Missing search scope");
        checkNotNull(direction, "Missing direction");

        if(Direction.CALLEE == direction)
        {
            this.methodWrapper = CallHierarchy.getDefault().getCalleeRoots(new IMethod[] { method })[0];
        }
        else
        {
            this.methodWrapper = CallHierarchy.getDefault().getCallerRoots(new IMethod[] { method })[0];
        }

        this.searchScope = SearchEngine.createJavaSearchScope(JavaElementUtils.toArray(searchScope));
    }

    public Set<IMethod> getMatches(IProgressMonitor progressMonitor)
    {
        CallHierarchy callHierarchy = CallHierarchy.getDefault();
        IJavaSearchScope originalSearchScope = callHierarchy.getSearchScope();
        try
        {
            callHierarchy.setSearchScope(searchScope);

            Set<IMethod> testCallers = new LinkedHashSet<IMethod>();
            MethodWrapper[] calls = this.methodWrapper.getCalls(progressMonitor);
            for (int i = 0; i < calls.length; i++)
            {
                IMember member = calls[i].getMember();
                if(! (member instanceof IMethod) || member.getCompilationUnit() == null)
                {
                    continue;
                }
                IMethod method = getFirstNonAnonymousMethod(member);
                if(methodMatch(method))
                {
                    testCallers.add(method);
                }
            }
            return testCallers;
        }
        finally
        {
            callHierarchy.setSearchScope(originalSearchScope);
        }
    }

    private IMethod getFirstNonAnonymousMethod(IMember member)
    {
        return new MethodFacade((IMethod) member).getFirstNonAnonymousMethodCallingThisMethod();
    }

    abstract protected boolean methodMatch(IMethod method);
}
