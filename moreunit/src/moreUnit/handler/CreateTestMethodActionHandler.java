package moreUnit.handler;

import moreUnit.log.LogHandler;
import moreUnit.util.PluginTools;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * @author vera
 * 24.10.2005
 */
public class CreateTestMethodActionHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		LogHandler.getInstance().handleInfoLog("CreateTestMethodActionHandler.execute()");
		 
		MoreUnitActionHandler.getInstance().executeCreateTestMethodAction(PluginTools.getOpenEditorPart());
		return null;
	}
}