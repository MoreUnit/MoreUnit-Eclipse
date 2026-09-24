package org.moreunit.ui;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.texteditor.ITextEditor;
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
        catch (final PartInitException exc)
        {
            LogHandler.getInstance().handleExceptionLog(exc);
        }
        catch (final JavaModelException exc)
        {
            LogHandler.getInstance().handleExceptionLog(exc);
        }
        return openedEditorPart;
    }

    public void reveal(IEditorPart editorPart, IJavaElement element)
    {
        JavaUI.revealInEditor(editorPart, element);
    }

    /**
     * Reveals the given position in the given editor, thus moving the cursor to
     * it. Does nothing if the position is unknown (negative offset) or if the
     * editor is not a text editor.
     */
    public void revealOffset(IEditorPart editorPart, int offset)
    {
        if(editorPart instanceof final ITextEditor textEditor && offset >= 0)
        {
            textEditor.selectAndReveal(offset, 0);
        }
    }
}
