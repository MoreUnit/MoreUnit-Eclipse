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
		MoreUnitActionHandler.getInstance().executeJumpAction(PluginTools.getOpenEditorPart());
		return null;
	}
}

// $Log: not supported by cvs2svn $
// Revision 1.2  2006/01/19 21:39:44  gianasista
// Added CVS-commit-logging to all java-files
//