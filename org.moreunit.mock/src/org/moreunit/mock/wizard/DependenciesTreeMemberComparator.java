package org.moreunit.mock.wizard;

import java.util.Comparator;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

public class DependenciesTreeMemberComparator implements Comparator<IMember>
{
    public int compare(IMember m1, IMember m2)
    {
        int result = compareMemberTypes(m1, m2);
        if(result != 0)
        {
            return result;
        }

        if(m1 instanceof IMethod && m2 instanceof IMethod)
        {
            result = compareMethodTypes((IMethod) m1, (IMethod) m2);
            if(result != 0)
            {
                return result;
            }
        }

        return m1.getElementName().compareTo(m2.getElementName());
    }

    private int compareMemberTypes(IMember m1, IMember m2)
    {
        if(m1 instanceof IMethod && m2 instanceof IField)
        {
            return - 1;
        }
        if(m1 instanceof IField && m2 instanceof IMethod)
        {
            return 1;
        }
        return 0;
    }

    private int compareMethodTypes(IMethod m1, IMethod m2)
    {
        boolean c1 = false, c2 = false;
        try
        {
            c1 = m1.isConstructor();
            c2 = m2.isConstructor();
        }
        catch (JavaModelException e)
        {
            // ignored
        }

        if(c1 && ! c2)
        {
            return - 1;
        }
        if(! c1 && c2)
        {
            return 1;
        }
        if(c1 && c2)
        {
            int result = compareNumberOfParameters(m1, m2);
            if(result != 0)
            {
                return result;
            }
        }
        return 0;
    }

    private int compareNumberOfParameters(IMethod m1, IMethod m2)
    {
        int n1 = m1.getNumberOfParameters();
        int n2 = m2.getNumberOfParameters();
        if(n1 > n2)
        {
            return - 1;
        }
        if(n1 < n2)
        {
            return 1;
        }
        return 0;
    }
}
