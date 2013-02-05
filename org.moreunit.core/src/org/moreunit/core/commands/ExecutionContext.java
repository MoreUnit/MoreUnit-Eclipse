package org.moreunit.core.commands;

import static org.moreunit.core.config.CoreModule.$;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.moreunit.core.ui.UserInterface;

public class ExecutionContext
{
    private final ExecutionEvent event;

    public ExecutionContext(ExecutionEvent event)
    {
        this.event = event;
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
        return new Selection(event);
    }

    public UserInterface getUserInterface()
    {
        return $().getUserInterface(getWorkbench(), getActivePage(), getActiveShell());
    }

    private IWorkbench getWorkbench()
    {
        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
        return window == null ? null : window.getWorkbench();
    }
}
