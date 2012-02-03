package org.moreunit.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.moreunit.log.LogHandler;
import org.moreunit.util.PluginTools;

/**
 * This class delegates the shortcut action from the editor to create a new
 * testmethod.
 * 
 * @author vera 24.10.2005
 */
public class CreateTestMethodActionHandler extends AbstractHandler
{

    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        LogHandler.getInstance().handleInfoLog("CreateTestMethodActionHandler.execute()");

        CreateTestMethodActionExecutor.getInstance().executeCreateTestMethodAction(PluginTools.getOpenEditorPart());
        return null;
    }
}