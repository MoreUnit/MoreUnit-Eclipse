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
        return getClass().getName() + ".packageFragmentRoot." + element.getJavaProject().getElementName();
    }

    public void resetDialogSettings()
    {
        getDialogSettings().put(getPackageFragmentRootKey(), (String) null);
    }

    public void setWizardDialogFactory(WizardDialogFactory factory)
    {
        dialogFactory = factory;
    }
}
