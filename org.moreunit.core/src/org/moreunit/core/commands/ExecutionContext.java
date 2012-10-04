package org.moreunit.core.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.IDE;
import org.moreunit.core.log.Logger;
import org.moreunit.core.ui.Dialogs;
import org.moreunit.core.ui.NewFileWizard;

public class ExecutionContext
{
    private final ExecutionEvent event;
    private final Logger logger;

    public ExecutionContext(ExecutionEvent event, Logger logger)
    {
        this.event = event;
        this.logger = logger;
    }

    public IWorkbenchPage getActivePage()
    {
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
        return window == null ? null : window.getActivePage();
    }

    public Shell getActiveShell()
    {
        return HandlerUtil.getActiveShell(event);
    }

    public ExecutionEvent getEvent()
    {
        return event;
    }

    public Selection getSelection()
    {
        return new Selection(event, logger);
    }

    public IWorkbench getWorkbench()
    {
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
        return window == null ? null : window.getWorkbench();
    }

    public void openEditor(IFile file)
    {
        IWorkbenchPage activePage = getActivePage();
        if(activePage == null)
        {
            return;
        }

        try
        {
            IDE.openEditor(activePage, file, true);
        }
        catch (PartInitException e)
        {
            logger.error("Could not open editor for " + file, e);
        }
    }

    public void openDialog(NewFileWizard wizard)
    {
        Dialogs.open(getActiveShell(), wizard);
    }
}
