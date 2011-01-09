package org.moreunit.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.moreunit.handler.RunTestsActionExecutor;

/**
 * This class delegates the action from the menu in the editor to run the test
 * corresponding to the open type.
 */
public class RunTestAction implements IEditorActionDelegate
{

    private IEditorPart editorPart;

    public void setActiveEditor(IAction action, IEditorPart targetEditor)
    {
        editorPart = targetEditor;
    }

    public void run(IAction action)
    {
        RunTestsActionExecutor.getInstance().executeRunTestAction(editorPart);
    }

    public void selectionChanged(IAction action, ISelection selection)
    {
    }
}
