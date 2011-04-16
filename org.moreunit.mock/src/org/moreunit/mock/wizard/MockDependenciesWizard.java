package org.moreunit.mock.wizard;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.moreunit.mock.MoreUnitMockPlugin;

public class MockDependenciesWizard extends Wizard implements INewWizard
{
    private Shell shell;

    public MockDependenciesWizard(MockDependenciesWizardPage page)
    {
        addPage(page);
    }

    public void init(IWorkbench workbench, IStructuredSelection selection)
    {
        this.shell = workbench.getActiveWorkbenchWindow().getShell();
    }

    public boolean openAndReturnIfOk()
    {
        return Window.OK == new WizardDialog(shell, this).open();
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
