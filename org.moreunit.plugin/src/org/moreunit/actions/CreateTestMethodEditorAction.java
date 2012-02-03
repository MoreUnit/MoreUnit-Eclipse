package org.moreunit.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.moreunit.handler.CreateTestMethodActionExecutor;
import org.moreunit.log.LogHandler;

/**
 * This class delegates the menu action from the editor to create a new
 * testmethod.
 */
public class CreateTestMethodEditorAction implements IEditorActionDelegate
{

    private IEditorPart editorPart;

    public void setActiveEditor(IAction action, IEditorPart targetEditor)
    {
        editorPart = targetEditor;
    }

    public void run(IAction action)
    {
        LogHandler.getInstance().handleInfoLog("CreateTestMethodEditorAction.run()");
        CreateTestMethodActionExecutor.getInstance().executeCreateTestMethodAction(editorPart);
    }

    public void selectionChanged(IAction action, ISelection selection)
    {
    }
}