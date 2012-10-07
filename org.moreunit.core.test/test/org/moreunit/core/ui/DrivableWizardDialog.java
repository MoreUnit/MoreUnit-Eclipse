package org.moreunit.core.ui;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

public class DrivableWizardDialog extends WizardDialog
{
    private WizardDriver driver;

    public DrivableWizardDialog(Shell parentShell, IWizard newWizard, WizardDriver driver)
    {
        super(parentShell, newWizard);
        this.driver = driver;
    }

    @Override
    public int open()
    {
        setBlockOnOpen(false);
        super.open();
        int result = driver.open(this);
        close();
        return result;
    }

    // increases getWizard()'s visibility
    @Override
    public IWizard getWizard()
    {
        return super.getWizard();
    }
}
