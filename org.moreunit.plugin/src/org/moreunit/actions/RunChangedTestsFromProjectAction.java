package org.moreunit.actions;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.moreunit.handler.RunChangedTestsActionExecutor;
import org.moreunit.util.JavaProjects;

/**
 * This class delegates the action from the menu in the package explorer to run
 * the tests of the files which changed since the last Git commit.
 */
public class RunChangedTestsFromProjectAction implements IObjectActionDelegate
{
    private Collection<IJavaProject> projects = Collections.emptyList();

    @Override
    public void setActivePart(IAction action, IWorkbenchPart targetPart)
    {
    }

    @Override
    public void run(IAction action)
    {
        RunChangedTestsActionExecutor.getInstance().execute(projects, ILaunchManager.RUN_MODE);
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection)
    {
        projects = JavaProjects.of(selection);
    }
}
