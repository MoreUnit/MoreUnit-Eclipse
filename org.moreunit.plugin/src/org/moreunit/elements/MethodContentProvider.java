package org.moreunit.elements;

import java.util.List;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * This content provider is used to show a list dialog of corresponding
 * testmethods if a use renames a method.
 * 
 * @author vera 29.03.2006 21:35:05
 */
public class MethodContentProvider implements IStructuredContentProvider
{

    List<IMethod> methods;

    public MethodContentProvider(List<IMethod> methods)
    {
        this.methods = methods;
    }

    public Object[] getElements(Object inputElement)
    {
        return methods.toArray();
    }

    public void dispose()
    {
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
    }
}