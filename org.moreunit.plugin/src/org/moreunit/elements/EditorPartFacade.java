/**
 * 
 */
package org.moreunit.elements;

import static org.moreunit.core.util.Preconditions.checkNotNull;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.moreunit.log.LogHandler;
import org.moreunit.util.MoreUnitContants;

/**
 * @author vera 25.01.2006 21:50:37 EditorPartFacade offers easy access to
 *         {@link IEditorPart}
 */
public class EditorPartFacade
{

    private final IEditorPart editorPart;
    private final IFile file;

    public EditorPartFacade(IEditorPart editorPart)
    {
        checkNotNull(editorPart, "Can not wrap a null editor part");
        this.editorPart = editorPart;
        this.file = (IFile) editorPart.getEditorInput().getAdapter(IFile.class);
    }

    public IFile getFile()
    {
        return file;
    }

    public boolean isJavaLikeFile()
    {
        return file != null && MoreUnitContants.SUPPORTED_EXTENSIONS.contains(file.getFileExtension());
    }

    public ICompilationUnit getCompilationUnit()
    {
        return file == null ? null : JavaCore.createCompilationUnitFrom(file);
    }

    public ITextSelection getTextSelection()
    {
        IWorkbenchPartSite site = editorPart.getSite();
        ISelectionProvider selectionProvider = site.getSelectionProvider();
        return (ITextSelection) selectionProvider.getSelection();
    }

    /**
     * Returns the method that directly surrounds the cursor position, even if
     * it is part of an anonymous type (you may want to use
     * {@link #getFirstNonAnonymousMethodSurroundingCursorPosition()} instead).
     */
    public IMethod getMethodUnderCursorPosition()
    {
        IMethod method = null;
        try
        {
            ICompilationUnit compilationUnit = getCompilationUnit();
            if(compilationUnit == null)
                return null;

            IJavaElement javaElement = compilationUnit.getElementAt(getTextSelection().getOffset());
            if(javaElement instanceof IMethod)
            {
                method = (IMethod) javaElement;
            }
            else
                LogHandler.getInstance().handleInfoLog("No method found under cursor position.");
        }
        catch (JavaModelException e)
        {
            LogHandler.getInstance().handleExceptionLog(e);
        }

        return method;
    }

    public IJavaProject getJavaProject()
    {
        ICompilationUnit compilationUnit = getCompilationUnit();
        return compilationUnit == null ? null : compilationUnit.getJavaProject();
    }

    public IEditorPart getEditorPart()
    {
        return editorPart;
    }

    /**
     * Returns the first method that surrounds the cursor position and that is
     * not part of an anonymous type.
     */
    public IMethod getFirstNonAnonymousMethodSurroundingCursorPosition()
    {
        IMethod method = getFirstMethodSurroundingCursorPosition();
        return method == null ? null : new MethodFacade(method).getFirstNonAnonymousMethodCallingThisMethod();
    }

    private IMethod getFirstMethodSurroundingCursorPosition()
    {
        IMethod method = null;
        try
        {
            ICompilationUnit compilationUnit = getCompilationUnit();
            if(compilationUnit == null)
                return null;

            IJavaElement javaElement = compilationUnit.getElementAt(getTextSelection().getOffset());
            if(javaElement instanceof IMethod)
            {
                method = (IMethod) javaElement;
            }
            else if(javaElement instanceof IType && ((IType) javaElement).isAnonymous() && javaElement.getParent() instanceof IMethod)
            {
                method = (IMethod) javaElement.getParent();
            }
            else
                LogHandler.getInstance().handleInfoLog("No method found surrounding cursor position.");
        }
        catch (JavaModelException e)
        {
            LogHandler.getInstance().handleExceptionLog(e);
        }
        return method;
    }
}
