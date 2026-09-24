package org.moreunit.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.moreunit.util.PluginTools;

/**
 * This class delegates the shortcut action going back to the location the user
 * was at before jumping to the corresponding class under test / test case.
 */
public class JumpBackActionHandler extends AbstractHandler
{

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        JumpActionExecutor.getInstance().executeJumpBackAction(PluginTools.getOpenEditorPart());
        return null;
    }
}
