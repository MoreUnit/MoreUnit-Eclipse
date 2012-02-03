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
 * This class delegates the action from the menu in the package explorer to
 * create a new testmethod.
 */
public class CreateTestMethodHierarchyAction implements IObjectActionDelegate
{

    //private IWorkbenchPart workbenchPart;

    private ISelection selection;

    public void setActivePart(final IAction action, final IWorkbenchPart targetPart)
    {
        //this.workbenchPart = targetPart;
    }

    public void run(final IAction action)
    {
        if((this.selection != null) && (this.selection instanceof IStructuredSelection))
        {
            Object firstElement = ((IStructuredSelection) this.selection).getFirstElement();
            if(firstElement instanceof IMethod)
            {
                IMethod method = (IMethod) firstElement;
                if(! TypeFacade.isTestCase(method.getCompilationUnit().findPrimaryType()))
                {
                    TestmethodCreator testmethodCreator = new TestmethodCreator(method.getCompilationUnit(), Preferences.getInstance().getTestType(method.getJavaProject()), Preferences.getInstance().getTestMethodDefaultContent(method.getJavaProject()));
                    testmethodCreator.createTestMethod(method);
                }
            }
        }
    }

    public void selectionChanged(final IAction action, final ISelection selection)
    {
        this.selection = selection;
    }
}