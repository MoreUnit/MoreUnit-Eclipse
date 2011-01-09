package org.moreunit.ui;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.moreunit.log.LogHandler;

public class EditorUI
{
    public IEditorPart open(IJavaElement element)
    {
        IEditorPart openedEditorPart = null;
        try
        {
            openedEditorPart = JavaUI.openInEditor(element);
        }
        catch (PartInitException exc)
        {
            LogHandler.getInstance().handleExceptionLog(exc);
        }
        catch (JavaModelException exc)
        {
            LogHandler.getInstance().handleExceptionLog(exc);
        }
        return openedEditorPart;
    }

    public void reveal(IEditorPart editorPart, IJavaElement element)
    {
        JavaUI.revealInEditor(editorPart, element);
    }
}
