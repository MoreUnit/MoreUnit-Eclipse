package moreUnit.handler;

import moreUnit.util.PluginTools;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * @author vera
 * 25.10.2005
 */
public class JumpToTestActionHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		MoreUnitActionHandler.getInstance().executeJumpToTestAction(PluginTools.getOpenEditorPart());
		return null;
	}
}