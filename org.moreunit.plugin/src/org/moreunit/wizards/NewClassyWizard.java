package org.moreunit.wizards;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.moreunit.MoreUnitPlugin;
import org.moreunit.log.LogHandler;

public abstract class NewClassyWizard extends Wizard implements INewWizard
{
    private WizardDialogFactory dialogFactory = new WizardDialogFactory();
    
    private final IType element;
    private IType createdType;

    public NewClassyWizard(IType element)
    {
        this.element = element;
        setNeedsProgressMonitor(true);
        setDefaultPageImageDescriptor(MoreUnitPlugin.getImageDescriptor("icons/moreunitWizard.png"));
    }

    protected IType getType()
    {
        return element;
    }

    public IType open()
    {
        WizardDialog dialog = dialogFactory.createWizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), this);
        if(dialog.open() == Window.OK)
        {
            typeCreated(createdType);
            return createdType;
        }
        creationAborted();
        return null;
    }

    /** To be overridden if needed */
    protected void typeCreated(IType createdType2)
    {
    }

    /** To be overridden if needed */
    protected void creationAborted()
    {
    }

    public boolean performFinish()
    {
        try
        {
            createdType = createClass();
            getDialogSettings().put(getPackageFragmentRootKey(), getPackageFragmentRoot().getHandleIdentifier());
        }
        catch (Exception e)
        {
            LogHandler.getInstance().handleExceptionLog(e);
        }
        return true;
    }

    public IDialogSettings getDialogSettings()
    {
        return MoreUnitPlugin.getDefault().getDialogSettings();
    }

    public void init(IWorkbench workbench, IStructuredSelection selection)
    {
    }

    public abstract IType createClass() throws CoreException, InterruptedException;

    protected abstract IPackageFragmentRoot getPackageFragmentRoot();

    protected final String getPackageFragmentRootKey()
    {
        return getClass().getName() + ".packageFragmentRoot";
    }

    public void setWizardDialogFactory(WizardDialogFactory factory)
    {
        dialogFactory = factory;
    }
}

// $Log: not supported by cvs2svn $
// Revision 1.4 2008/02/29 21:34:16 gianasista
// Minor refactorings
//
// Revision 1.3 2006/09/18 20:00:14 channingwalton
// the CVS substitions broke with my last check in because I put newlines in
// them
//
// Revision 1.2 2006/09/18 19:56:08 channingwalton
// Fixed bug [ 1537839 ] moreunit cannot find test class if it is in wrong
// package. Also found a classcast exception in UnitDecorator whicj I've guarded
// for. Fixed the Class wizard icon
//
// Revision 1.1.1.1 2006/08/13 14:31:16 gianasista
// initial
//
// Revision 1.1 2006/06/22 20:22:29 gianasista
// package rename
//
// Revision 1.1 2006/06/19 20:08:48 gianasista
// CVS Refactoring
//
// Revision 1.1 2006/05/12 22:33:41 channingwalton
// added class creation wizards if type to jump to does not exist
//
