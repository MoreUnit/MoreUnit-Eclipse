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

    private final IJavaSearchScope searchScope;
    private final MethodWrapper methodWrapper;

    protected MethodCallFinder(IMethod method, Collection< ? extends IJavaElement> searchScope)
    {
        checkNotNull(method, "Method can not be null");
        checkNotNullOrEmpty(searchScope, "Missing search scope");

        this.methodWrapper = CallHierarchy.getDefault().getCallerRoots(new IMethod[] { method })[0];

        this.searchScope = SearchEngine.createJavaSearchScope(JavaElementUtils.toArray(searchScope));
    }

    public Set<IMethod> getMatches(IProgressMonitor progressMonitor)
    {
        final CallHierarchy callHierarchy = CallHierarchy.getDefault();
        final IJavaSearchScope originalSearchScope = callHierarchy.getSearchScope();
        try
        {
            callHierarchy.setSearchScope(searchScope);

            final Set<IMethod> testCallers = new LinkedHashSet<>();
            final MethodWrapper[] calls = this.methodWrapper.getCalls(progressMonitor);
            for (final MethodWrapper call : calls)
            {
                final IMember member = call.getMember();
                if(! (member instanceof IMethod) || member.getCompilationUnit() == null)
                {
                    continue;
                }
                final IMethod method = getFirstNonAnonymousMethod(member);
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
