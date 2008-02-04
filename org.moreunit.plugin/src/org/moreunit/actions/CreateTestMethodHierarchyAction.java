package org.moreunit.actions;


import org.eclipse.jdt.core.IMethod;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.moreunit.elements.TestmethodCreator;
import org.moreunit.elements.TypeFacade;
import org.moreunit.preferences.Preferences;

/**
 * This class delegates the action from the menu in the package explorer
 * to create a new testmethod.
 */
public class CreateTestMethodHierarchyAction implements IObjectActionDelegate {

	IWorkbenchPart workbenchPart;
	ISelection selection;

	public void setActivePart(final IAction action, final IWorkbenchPart targetPart) {
		this.workbenchPart = targetPart;
	}

	public void run(final IAction action) {
		if((this.selection != null) && (this.selection instanceof IStructuredSelection)) {
			Object firstElement = ((IStructuredSelection)this.selection).getFirstElement();
			if(firstElement instanceof IMethod) {
				IMethod method = (IMethod)firstElement;
				if(!TypeFacade.isTestCase(method.getCompilationUnit().findPrimaryType())) {
					TestmethodCreator testmethodCreator =
							new TestmethodCreator(method.getCompilationUnit(), Preferences.getInstance().getTestType(method.getJavaProject()));
					testmethodCreator.createTestMethod(method);
				}
			}
		}
	}

	public void selectionChanged(final IAction action, final ISelection selection) {
		this.selection = selection;
	}
}

// $Log: not supported by cvs2svn $
// Revision 1.4  2007/11/19 20:47:19  gianasista
// Patch from Bjoern: project specific settings
//
// Revision 1.3  2007/08/12 17:08:48  gianasista
// Refactoring: Test method creation
//
// Revision 1.2  2006/11/04 08:50:18  channingwalton
// Fix for [ 1579660 ] Testcase selection dialog opens twice
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
// Revision 1.6  2006/05/23 19:36:47  gianasista
// Splitted JavaFileFacade into two classes
//
// Revision 1.5  2006/05/15 19:48:32  gianasista
// removed deprecated method call
//
// Revision 1.4  2006/05/12 17:51:41  gianasista
// added comments
//
// Revision 1.3  2006/01/30 21:12:31  gianasista
// Further Refactorings (moved methods from singleton classes like PluginTools to facade classes)
//
// Revision 1.2  2006/01/19 21:39:44  gianasista
// Added CVS-commit-logging to all java-files
//