package moreUnit.actions;

import moreUnit.elements.JavaFileFacade;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/** 
 * This class delegates the action from the menu in the package explorer
 * to create a new testmethod.
 */
public class CreateTestMethodHierarchyAction implements IObjectActionDelegate {
	
	IWorkbenchPart workbenchPart;
	ISelection selection;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		workbenchPart = targetPart;
	}

	public void run(IAction action) {
		if(selection != null && selection instanceof IStructuredSelection) {
			Object firstElement = ((IStructuredSelection)selection).getFirstElement();
			if(firstElement instanceof IMethod) {
				IMethod method = (IMethod)firstElement;
				JavaFileFacade javaFileFacade = new JavaFileFacade(method.getCompilationUnit());
				IType type = javaFileFacade.getCorrespondingTestCase();
				if(type != null)
					(new JavaFileFacade(type.getCompilationUnit())).createTestMethodForMethod(method);
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}
}

// $Log: not supported by cvs2svn $
// Revision 1.3  2006/01/30 21:12:31  gianasista
// Further Refactorings (moved methods from singleton classes like PluginTools to facade classes)
//
// Revision 1.2  2006/01/19 21:39:44  gianasista
// Added CVS-commit-logging to all java-files
//