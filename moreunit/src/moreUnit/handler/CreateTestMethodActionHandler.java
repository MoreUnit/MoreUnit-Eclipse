package moreUnit.handler;

import moreUnit.log.LogHandler;
import moreUnit.util.PluginTools;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * This class delegates the shortcut action from the editor to create a new
 * testmethod.
 * 
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

// $Log: not supported by cvs2svn $
// Revision 1.2  2006/01/19 21:39:44  gianasista
// Added CVS-commit-logging to all java-files
//