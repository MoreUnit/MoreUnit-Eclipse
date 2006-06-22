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
 * @author vera
 * 24.10.2005
 */
public class CreateTestMethodActionHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		LogHandler.getInstance().handleInfoLog("CreateTestMethodActionHandler.execute()");
		 
		EditorActionExecutor.getInstance().executeCreateTestMethodAction(PluginTools.getOpenEditorPart());
		return null;
	}
}

// $Log: not supported by cvs2svn $
// Revision 1.1  2006/06/19 20:08:48  gianasista
// CVS Refactoring
//
// Revision 1.4  2006/05/20 16:08:02  gianasista
// Rename of MoreUnitActionHandler, new name EditorActionExecutor
//
// Revision 1.3  2006/05/12 17:53:07  gianasista
// added comments
//
// Revision 1.2  2006/01/19 21:39:44  gianasista
// Added CVS-commit-logging to all java-files
//