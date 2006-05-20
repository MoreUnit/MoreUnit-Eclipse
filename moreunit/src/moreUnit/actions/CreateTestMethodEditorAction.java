package moreUnit.actions;

import moreUnit.handler.EditorActionExecutor;
import moreUnit.log.LogHandler;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

/**
 * This class delegates the menu action from the editor to create a new
 * testmethod.
 */
public class CreateTestMethodEditorAction implements IEditorActionDelegate {
	
	IEditorPart editorPart;

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		editorPart = targetEditor;
	}

	public void run(IAction action) {
		LogHandler.getInstance().handleInfoLog("CreateTestMethodEditorAction.run()");
		EditorActionExecutor.getInstance().executeCreateTestMethodAction(editorPart);
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}
}

// $Log: not supported by cvs2svn $
// Revision 1.4  2006/05/12 17:51:41  gianasista
// added comments
//
// Revision 1.3  2006/03/21 20:59:23  gianasista
// Deleted Info-Log
//
// Revision 1.2  2006/01/19 21:39:44  gianasista
// Added CVS-commit-logging to all java-files
//