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
        final MockDependenciesWizardValues wizardValues = mock(MockDependenciesWizardValues.class);
        final DependencyInjectionPointStore store = mock(DependencyInjectionPointStore.class);

        final MockDependenciesWizardPage page = factory().createMockDependenciesWizardPage(wizardValues, store);

        assertNotNull(page);
        assertSame(store, page.getInjectionPointStore());
    }

    @Test
    public void should_create_dependencies_wizard_with_page()
    {
        final WizardFactory factory = factory();
        final MockDependenciesWizardPage page = factory.createMockDependenciesWizardPage(mock(MockDependenciesWizardValues.class), mock(DependencyInjectionPointStore.class));

        final MockDependenciesWizard wizard = factory.createMockDependenciesWizard(page);

        final IWizardPage[] pages = wizard.getPages();
        assertEquals(1, pages.length);
        assertSame(page, pages[0]);
    }

    @Test
    public void should_create_wizard_dialog_for_shell_and_wizard()
    {
        final WizardFactory factory = factory();
        final MockDependenciesWizardPage page = factory.createMockDependenciesWizardPage(mock(MockDependenciesWizardValues.class), mock(DependencyInjectionPointStore.class));
        final MockDependenciesWizard wizard = factory.createMockDependenciesWizard(page);

        final Shell shell = new Shell();
        final WizardDialog dialog = factory.createWizardDialog(shell, wizard);

        assertNotNull(dialog);
        dialog.close();
        shell.dispose();
    }
}
