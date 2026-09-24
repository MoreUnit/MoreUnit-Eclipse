package org.moreunit.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.moreunit.util.PluginTools;

/**
 * This class delegates the shortcut action going forward to the location the
 * user jumped back from.
 */
public class JumpForwardActionHandler extends AbstractHandler
{

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        JumpActionExecutor.getInstance().executeJumpForwardAction(PluginTools.getOpenEditorPart());
        return null;
    }
}
