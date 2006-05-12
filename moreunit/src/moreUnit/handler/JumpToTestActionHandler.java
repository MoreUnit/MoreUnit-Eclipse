package moreUnit.handler;

import moreUnit.util.PluginTools;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * This class delegates the shortcut action switching between class and
 * class under test or method and testmethod
 * 
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
// Revision 1.3  2006/04/21 05:57:05  gianasista
// Feature: Jump from testcase back to class under test
//
// Revision 1.2  2006/01/19 21:39:44  gianasista
// Added CVS-commit-logging to all java-files
//