package org.moreunit.actions;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.moreunit.handler.RunTestsActionExecutor;

/**
 * This class delegates the action from the menu in the package explorer to run
 * the test case corresponding to the selected compilation unit.
 */
public class RunTestFromCompilationUnitAction implements IObjectActionDelegate
{

    private ICompilationUnit compilationUnit;

    public void setActivePart(IAction action, IWorkbenchPart targetPart)
    {
    }

    public void run(IAction action)
    {
        RunTestsActionExecutor.getInstance().executeRunTestAction(compilationUnit);
    }

    public void selectionChanged(IAction action, ISelection selection)
    {
        IStructuredSelection structuredSelection = (IStructuredSelection) selection;
        compilationUnit = (ICompilationUnit) structuredSelection.getFirstElement();
    }

}
