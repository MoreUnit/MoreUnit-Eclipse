package org.moreunit.wizards;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.junit.jupiter.api.Test;

public class WizardDialogFactoryTest
{
    @Test
    public void should_create_wizard_dialog()
    {
        WizardDialogFactory factory = new WizardDialogFactory();
        Shell shell = mock(Shell.class);
        NewClassyWizard wizard = mock(NewClassyWizard.class);

        WizardDialog dialog = factory.createWizardDialog(shell, wizard);

        assertNotNull(dialog);
    }
}
