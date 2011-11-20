package org.moreunit.mock.wizard;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.moreunit.MoreUnitResources;
import org.moreunit.mock.MoreUnitMockPlugin;

public class MockDependenciesWizard extends Wizard implements INewWizard
{
    private final WizardFactory wizardFactory;
    private Shell shell;

    public MockDependenciesWizard(WizardFactory wizardFactory, MockDependenciesWizardPage page)
    {
        this.wizardFactory = wizardFactory;
        addPage(page);
        setDefaultPageImageDescriptor(new MoreUnitResources().getMediumLogoDescriptor());
    }

    public void init(IWorkbench workbench, IStructuredSelection selection)
    {
        this.shell = workbench.getActiveWorkbenchWindow().getShell();
    }

    public boolean openAndReturnIfOk()
    {
        WizardDialog dialog = wizardFactory.createWizardDialog(shell, this);
        return Window.OK == dialog.open();
    }

    @Override
    public boolean performFinish()
    {
        return true;
    }

    @Override
    public IDialogSettings getDialogSettings()
    {
        return MoreUnitMockPlugin.getDefault().getDialogSettings();
    }
}
