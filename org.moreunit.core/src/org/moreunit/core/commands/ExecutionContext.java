package org.moreunit.core.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.moreunit.core.config.Module;
import org.moreunit.core.log.Logger;
import org.moreunit.core.ui.UserInterface;

public class ExecutionContext
{
    private final ExecutionEvent event;
    private final Module module;
    private final Logger logger;

    public ExecutionContext(ExecutionEvent event, Module module, Logger logger)
    {
        this.event = event;
        this.module = module;
        this.logger = logger;
    }

    private IWorkbenchPage getActivePage()
    {
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
        return window == null ? null : window.getActivePage();
    }

    private Shell getActiveShell()
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

    public UserInterface getUserInterface()
    {
        return module.getUserInterface(getWorkbench(), getActivePage(), getActiveShell());
    }

    private IWorkbench getWorkbench()
    {
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
        return window == null ? null : window.getWorkbench();
    }
}
