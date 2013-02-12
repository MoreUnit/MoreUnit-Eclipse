package org.moreunit.core.commands;

import static org.moreunit.core.config.CoreModule.$;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.moreunit.core.log.Logger;
import org.moreunit.core.ui.UserInterface;

public class ExecutionContext
{
    private final ExecutionEvent event;
    private final Logger logger;

    public ExecutionContext(ExecutionEvent event, Logger logger)
    {
        this.event = event;
        this.logger = logger;
    }

    public IEvaluationContext getApplicationContext()
    {
        if(! (event.getApplicationContext() instanceof IEvaluationContext))
        {
            logger.trace("Unsupported context: " + event.getApplicationContext()); //$NON-NLS-1$
            return null;
        }

        return (IEvaluationContext) event.getApplicationContext();
    }

    // implemented in HandlerUtil only since 3.7
    public IEditorPart getActiveEditorPart()
    {
        Object editor = HandlerUtil.getVariable(event, ISources.ACTIVE_EDITOR_NAME);
        if(editor instanceof IEditorPart)
        {
            return (IEditorPart) editor;
        }
        return null;
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
        return new Selection(this);
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
