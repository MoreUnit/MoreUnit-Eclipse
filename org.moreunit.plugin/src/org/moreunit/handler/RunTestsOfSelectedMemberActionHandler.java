package org.moreunit.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.moreunit.util.PluginTools;

/**
 * This class delegates the shortcut action from the editor to run the test
 * corresponding to the open type or selected method.
 */
public class RunTestsOfSelectedMemberActionHandler extends AbstractHandler
{

    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        RunTestsActionExecutor.getInstance().executeRunTestsOfSelectedMemberAction(PluginTools.getOpenEditorPart());
        return null;
    }
}
