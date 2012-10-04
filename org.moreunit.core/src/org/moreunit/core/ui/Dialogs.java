package org.moreunit.core.ui;

import static org.moreunit.core.config.Module.$;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

public class Dialogs
{
    public static WizardDialog createFor(Shell activeShell, IWizard wizard)
    {
        return new DrivableWizardDialog(activeShell, wizard, $().getWizardDriver());
    }

    public static int open(Shell activeShell, NewFileWizard wizard)
    {
        return createFor(activeShell, wizard).open();
    }
}
