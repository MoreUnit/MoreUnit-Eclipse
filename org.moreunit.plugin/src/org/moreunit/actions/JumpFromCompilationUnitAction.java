package org.moreunit.actions;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.moreunit.handler.EditorActionExecutor;

public class JumpFromCompilationUnitAction implements IObjectActionDelegate {

	private ICompilationUnit	compilationUnit;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		
	}

	public void run(IAction action) {
		EditorActionExecutor.getInstance().executeJumpAction(compilationUnit);
	}

	public void selectionChanged(IAction action, ISelection selection) {
		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		compilationUnit = (ICompilationUnit) structuredSelection.getFirstElement();
	}

}

// $Log: not supported by cvs2svn $
//