package org.moreunit.wizards;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

public class WizardDialogFactory
{
    public WizardDialog createWizardDialog(Shell shell, NewClassyWizard wizard)
    {
        return new WizardDialog(shell, wizard);
    }
}
