package org.moreunit.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.moreunit.util.PluginTools;

/**
 * This class delegates the shortcut action switching between class and class
 * under test or method and testmethod
 * 
 * @author vera 25.10.2005
 */
public class JumpActionHandler extends AbstractHandler
{

    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        JumpActionExecutor.getInstance().executeJumpAction(PluginTools.getOpenEditorPart());
        return null;
    }
}