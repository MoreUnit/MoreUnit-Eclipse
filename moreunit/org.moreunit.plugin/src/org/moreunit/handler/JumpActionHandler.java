package org.moreunit.handler;


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.moreunit.util.PluginTools;

/**
 * This class delegates the shortcut action switching between class and
 * class under test or method and testmethod
 * 
 * @author vera
 * 25.10.2005
 */
public class JumpActionHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		EditorActionExecutor.getInstance().executeJumpAction(PluginTools.getOpenEditorPart());
		return null;
	}
}

// $Log: not supported by cvs2svn $
// Revision 1.1  2006/06/19 20:08:48  gianasista
// CVS Refactoring
//
// Revision 1.1  2006/05/20 16:09:08  gianasista
// Rename of classname because jump to class under test is handled here too
//
// Revision 1.4  2006/05/12 17:53:07  gianasista
// added comments
//
// Revision 1.3  2006/04/21 05:57:05  gianasista
// Feature: Jump from testcase back to class under test
//
// Revision 1.2  2006/01/19 21:39:44  gianasista
// Added CVS-commit-logging to all java-files
//