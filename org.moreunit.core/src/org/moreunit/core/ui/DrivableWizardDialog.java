package org.moreunit.core.ui;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.moreunit.core.config.Config;

public class DrivableWizardDialog extends WizardDialog
{
    public DrivableWizardDialog(Shell parentShell, IWizard newWizard)
    {
        super(parentShell, newWizard);
    }

    @Override
    public int open()
    {
        if(Config.wizardDriver == null)
        {
            return super.open();
        }

        setBlockOnOpen(false);
        super.open();
        int result = Config.wizardDriver.open(this);
        close();
        return result;
    }

    @Override
    public IWizard getWizard()
    {
        return super.getWizard();
    }
}
