package org.moreunit.util;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.callhierarchy.CallHierarchy;
import org.eclipse.jdt.internal.corext.callhierarchy.MethodWrapper;

//@SuppressWarnings("restriction")
public abstract class MethodCallFinder
{

    public static enum Direction
    {
        CALLEE, CALLER
    }

    private final MethodWrapper method;

    protected MethodCallFinder(IMethod method, Direction direction)
    {
        if(Direction.CALLEE == direction)
        {
            this.method = CallHierarchy.getDefault().getCalleeRoots(new IMethod[] { method })[0];
        }
        else
        {
            this.method = CallHierarchy.getDefault().getCallerRoots(new IMethod[] { method })[0];
        }
    }

    public Set<IMethod> getMatches(IProgressMonitor progressMonitor)
    {
        Set<IMethod> testCallers = new LinkedHashSet<IMethod>();
        MethodWrapper[] calls = this.method.getCalls(progressMonitor);
        for (int i = 0; i < calls.length; i++)
        {
            IMember member = calls[i].getMember();
            if(! (member instanceof IMethod) || member.getCompilationUnit() == null)
            {
                continue;
            }
            IMethod method = getFirstMethodInNonAnonymousType(member);
            if(methodMatch(method))
            {
                testCallers.add(method);
            }
        }
        return testCallers;
    }

    private IMethod getFirstMethodInNonAnonymousType(IMember member)
    {
        IMethod method = (IMethod) member;
        try
        {
            while (method.getParent() instanceof IType
                    && ((IType) method.getParent()).isAnonymous()
                    && method.getParent().getParent() instanceof IMethod)
            {
                method = (IMethod) method.getParent().getParent();
            }
        }
        catch (JavaModelException e)
        {
            // OK: method = method
        }
        return method;
    }

    abstract protected boolean methodMatch(IMethod method);
}
