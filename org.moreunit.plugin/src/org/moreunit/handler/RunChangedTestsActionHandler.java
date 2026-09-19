package org.moreunit.handler;

import java.util.Collection;
import java.util.LinkedHashSet;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.handlers.HandlerUtil;
import org.moreunit.util.JavaProjects;

/**
 * This class delegates the "Run Tests for Changed Files" command to the
 * executor, using the selected projects, or the project of the active editor.
 */
public class RunChangedTestsActionHandler extends AbstractHandler
{
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        final Collection<IJavaProject> projects = selectedProjects(event);
        if(projects.isEmpty())
        {
            MessageDialog.openInformation(HandlerUtil.getActiveShell(event), "MoreUnit", "Please select a Java project.");
            return null;
        }

        RunChangedTestsActionExecutor.getInstance().execute(projects, ILaunchManager.RUN_MODE);
        return null;
    }

    private Collection<IJavaProject> selectedProjects(ExecutionEvent event)
    {
        final Collection<IJavaProject> projects = new LinkedHashSet<>(JavaProjects.of(HandlerUtil.getCurrentSelection(event)));
        if(projects.isEmpty())
        {
            projects.addAll(JavaProjects.of(HandlerUtil.getActiveEditor(event)));
        }
        return projects;
    }
}
