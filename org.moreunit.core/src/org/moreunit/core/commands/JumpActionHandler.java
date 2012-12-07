package org.moreunit.core.commands;

import static org.moreunit.core.config.CoreModule.$;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class JumpActionHandler extends AbstractHandler
{
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        // For easier testing: wraps execution event and delegates to
        // JumpActionExecutor (give us back the decision to recreate the handler
        // for each call or not)
        $().getJumpActionExecutor().execute($().getExecutionContext(event));
        return null;
    }
}
