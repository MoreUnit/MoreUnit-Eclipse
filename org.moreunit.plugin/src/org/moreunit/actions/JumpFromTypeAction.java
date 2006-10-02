package org.moreunit.actions;

import org.eclipse.jdt.core.IType;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.moreunit.handler.EditorActionExecutor;

public class JumpFromTypeAction implements IObjectActionDelegate {

	private IType	type;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		
	}

	public void run(IAction action) {
		EditorActionExecutor.getInstance().executeJumpAction(type.getCompilationUnit());
	}

	public void selectionChanged(IAction action, ISelection selection) {
		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		type = (IType) structuredSelection.getFirstElement();
	}

}

// $Log: not supported by cvs2svn $
//