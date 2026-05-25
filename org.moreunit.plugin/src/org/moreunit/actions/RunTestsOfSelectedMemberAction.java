package org.moreunit.actions;

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.moreunit.handler.RunTestsActionExecutor;

/**
 * This class delegates the action from the menu in the editor to run the test
 * corresponding to the open type or selected method.
 */
public class RunTestsOfSelectedMemberAction implements IEditorActionDelegate
{

    private IEditorPart editorPart;

    @Override
    public void setActiveEditor(IAction action, IEditorPart targetEditor)
    {
        editorPart = targetEditor;
    }

    @Override
    public void run(IAction action)
    {
        RunTestsActionExecutor.getInstance().executeRunTestsOfSelectedMemberAction(editorPart, ILaunchManager.RUN_MODE);
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection)
    {
    }
}
