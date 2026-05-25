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

    @Override
    public Object[] getChildren(Object element)
    {
        return null;
    }

    @Override
    public Object[] getElements(Object inputElement)
    {
        return elements;
    }

    @Override
    public Object getParent(Object element)
    {
        return null;
    }

    @Override
    public boolean hasChildren(Object arg0)
    {
        return false;
    }

    @Override
    public void dispose()
    {
    }

    @Override
    public void inputChanged(Viewer arg0, Object arg1, Object arg2)
    {
    }

    @Override
    public ISelection getDefaultSelection()
    {
        return defaultSelection;
    }
}
