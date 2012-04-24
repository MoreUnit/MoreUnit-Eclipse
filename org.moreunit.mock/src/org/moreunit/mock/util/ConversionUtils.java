package org.moreunit.mock.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ui.IEditorPart;

public class ConversionUtils
{
    @SuppressWarnings("unchecked")
    public <T> T adapt(IAdaptable adaptable, Class<T> requiredClass)
    {
        return (T) adaptable.getAdapter(requiredClass);
    }

    public IFile getFile(IEditorPart editorPart)
    {
        return adapt(editorPart.getEditorInput(), IFile.class);
    }

    public ICompilationUnit getCompilationUnit(IFile file)
    {
        return JavaCore.createCompilationUnitFrom(file);
    }

    public ICompilationUnit getCompilationUnit(IEditorPart editorPart)
    {
        return getCompilationUnit(getFile(editorPart));
    }

    public IType getPrimaryType(ICompilationUnit compilationUnit)
    {
        return compilationUnit.findPrimaryType();
    }

    public IType getPrimaryType(IEditorPart IEditorPart)
    {
        return getPrimaryType(getCompilationUnit(IEditorPart));
    }
}
