package org.moreunit.core.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizard;
import org.junit.jupiter.api.Test;

public class WizardDriverTest
{
    @Test
    public void configure_does_nothing()
    {
        WizardDriver driver = new WizardDriver() {};
        DrivableWizardDialog dialog = mock(DrivableWizardDialog.class);

        // This is mainly to satisfy coverage on the empty method
        driver.configure(dialog);
    }

    @Test
    public void onOpen_returns_OK_by_default()
    {
        WizardDriver driver = new WizardDriver() {};
        DrivableWizardDialog dialog = mock(DrivableWizardDialog.class);

        assertEquals(driver.onOpen(dialog), Window.OK);
    }

    @Test
    public void userValidatesCreation_performs_finish_and_returns_OK()
    {
        WizardDriver driver = new WizardDriver() {};
        DrivableWizardDialog dialog = mock(DrivableWizardDialog.class);
        IWizard wizard = mock(IWizard.class);
        when(dialog.getWizard()).thenReturn(wizard);

        assertEquals(driver.userValidatesCreation(dialog), Window.OK);
        verify(wizard).performFinish();
    }

    @Test
    public void userCancelsCreation_performs_cancel_and_returns_CANCEL()
    {
        WizardDriver driver = new WizardDriver() {};
        DrivableWizardDialog dialog = mock(DrivableWizardDialog.class);
        IWizard wizard = mock(IWizard.class);
        when(dialog.getWizard()).thenReturn(wizard);

        assertEquals(driver.userCancelsCreation(dialog), Window.CANCEL);
        verify(wizard).performCancel();
    }
}
