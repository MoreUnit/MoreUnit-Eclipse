package org.moreunit.core.ui;

import org.eclipse.jface.wizard.IWizard;

public class WizardDialog<W extends IWizard> implements Dialog
{
    private final org.eclipse.jface.wizard.WizardDialog wizardDialog;
    private final W wizard;

    public WizardDialog(org.eclipse.jface.wizard.WizardDialog wizardDialog, W wizard)
    {
        this.wizardDialog = wizardDialog;
        this.wizard = wizard;
    }

    public void open()
    {
        wizardDialog.open();
    }

    public W getWizard()
    {
        return wizard;
    }
}
