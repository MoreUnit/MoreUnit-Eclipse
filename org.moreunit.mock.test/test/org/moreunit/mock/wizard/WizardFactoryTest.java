package org.moreunit.mock.wizard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.junit.jupiter.api.Test;
import org.moreunit.core.log.Logger;
import org.moreunit.mock.dependencies.DependencyInjectionPointStore;
import org.moreunit.mock.preferences.Preferences;
import org.moreunit.mock.preferences.TemplateStyleSelector;

public class WizardFactoryTest
{
    private static WizardFactory factory()
    {
        return new WizardFactory(mock(Preferences.class), mock(TemplateStyleSelector.class), mock(Logger.class));
    }

    @Test
    public void should_create_dependencies_wizard_page()
    {
        MockDependenciesWizardValues wizardValues = mock(MockDependenciesWizardValues.class);
        DependencyInjectionPointStore store = mock(DependencyInjectionPointStore.class);

        MockDependenciesWizardPage page = factory().createMockDependenciesWizardPage(wizardValues, store);

        assertNotNull(page);
        assertSame(store, page.getInjectionPointStore());
    }

    @Test
    public void should_create_dependencies_wizard_with_page()
    {
        WizardFactory factory = factory();
        MockDependenciesWizardPage page = factory.createMockDependenciesWizardPage(mock(MockDependenciesWizardValues.class), mock(DependencyInjectionPointStore.class));

        MockDependenciesWizard wizard = factory.createMockDependenciesWizard(page);

        IWizardPage[] pages = wizard.getPages();
        assertEquals(1, pages.length);
        assertSame(page, pages[0]);
    }

    @Test
    public void should_create_wizard_dialog_for_shell_and_wizard()
    {
        WizardFactory factory = factory();
        MockDependenciesWizardPage page = factory.createMockDependenciesWizardPage(mock(MockDependenciesWizardValues.class), mock(DependencyInjectionPointStore.class));
        MockDependenciesWizard wizard = factory.createMockDependenciesWizard(page);

        Shell shell = new Shell();
        WizardDialog dialog = factory.createWizardDialog(shell, wizard);

        assertNotNull(dialog);
        dialog.close();
        shell.dispose();
    }
}
