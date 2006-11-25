package org.moreunit.actions;


import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.moreunit.handler.EditorActionExecutor;
import org.moreunit.log.LogHandler;

/**
 * This class delegates the editor action switching between class and
 * class under test or method and testmethod
 * 
 * @author vera
 */
public class JumpAction implements IEditorActionDelegate{
	
	private IEditorPart editorPart;

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		editorPart = targetEditor;
	}

	public void run(IAction action) {
		EditorActionExecutor.getInstance().executeJumpAction(editorPart);
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}
}

// $Log: not supported by cvs2svn $
// Revision 1.3  2006/10/02 18:22:24  channingwalton
// added actions for jumping from views. added some tests for project properties. improved some of the text
//
// Revision 1.2  2006/09/19 21:48:28  channingwalton
// added some tests and logging to help debug a problem
//
// Revision 1.1.1.1  2006/08/13 14:31:15  gianasista
// initial
//
// Revision 1.1  2006/06/22 20:22:29  gianasista
// package rename
//
// Revision 1.1  2006/06/19 20:08:48  gianasista
// CVS Refactoring
//
// Revision 1.3  2006/05/20 16:05:20  gianasista
// Rename of MoreUnitActionHandler, new name EditorActionExecutor
//
// Revision 1.2  2006/05/12 17:51:41  gianasista
// added comments
//
// Revision 1.1  2006/04/21 05:55:24  gianasista
// Feature: Jump from testcase back to class under test
//
// Revision 1.2  2006/01/19 21:39:44  gianasista
// Added CVS-commit-logging to all java-files
//