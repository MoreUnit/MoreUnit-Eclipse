package org.moreunit.core.ui;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;

public class DrivableWizardFactory extends WizardFactory
{
    private final WizardDriver wizardDriver;

    public DrivableWizardFactory(IWorkbench workbench, Shell activeShell, WizardDriver wizardDriver)
    {
        super(workbench, activeShell);
        this.wizardDriver = wizardDriver;
    }

    @Override
    protected WizardDialog createWizardDialog(IWizard wizard)
    {
        return new DrivableWizardDialog(getActiveShell(), wizard, wizardDriver);
    }
}
