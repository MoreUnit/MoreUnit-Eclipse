package org.moreunit.core.ui;

import org.eclipse.jface.window.Window;

public abstract class WizardDriver
{
    protected void configure(DrivableWizardDialog dialog)
    {
    }

    protected int onOpen(DrivableWizardDialog dialog)
    {
        return Window.OK;
    }

    protected final int userValidatesCreation(DrivableWizardDialog dialog)
    {
        dialog.getWizard().performFinish();
        return Window.OK;
    }

    protected final int userCancelsCreation(DrivableWizardDialog dialog)
    {
        dialog.getWizard().performCancel();
        return Window.CANCEL;
    }
}
