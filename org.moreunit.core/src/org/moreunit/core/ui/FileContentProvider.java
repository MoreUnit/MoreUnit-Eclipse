package org.moreunit.core.ui;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;

public class FileContentProvider implements ITreeContentAndDefaultSelectionProvider
{
    private final Object[] elements;
    private final ISelection defaultSelection;

    public FileContentProvider(Collection<IFile> files, IFile defaultSelection)
    {
        this.elements = files.toArray();
        this.defaultSelection = new StructuredSelection(defaultSelection != null ? defaultSelection : files.iterator().next());
    }

    public Object[] getChildren(Object element)
    {
        return null;
    }

    public Object[] getElements(Object inputElement)
    {
        return elements;
    }

    public Object getParent(Object element)
    {
        return null;
    }

    public boolean hasChildren(Object arg0)
    {
        return false;
    }

    public void dispose()
    {
    }

    public void inputChanged(Viewer arg0, Object arg1, Object arg2)
    {
    }

    public ISelection getDefaultSelection()
    {
        return defaultSelection;
    }
}
