package org.moreunit.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.moreunit.handler.JumpActionExecutor;

/**
 * This class delegates the editor action switching between class and class
 * under test or method and testmethod
 * 
 * @author vera
 */
public class JumpAction implements IEditorActionDelegate
{

    private IEditorPart editorPart;

    public void setActiveEditor(IAction action, IEditorPart targetEditor)
    {
        editorPart = targetEditor;
    }

    public void run(IAction action)
    {
        JumpActionExecutor.getInstance().executeJumpAction(editorPart);
    }

    public void selectionChanged(IAction action, ISelection selection)
    {
    }
}