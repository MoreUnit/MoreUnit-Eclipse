package org.moreunit.mock;

import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.moreunit.mock.wizard.MockDependenciesWizard;
import org.moreunit.mock.wizard.MockDependenciesWizardPage;
import org.moreunit.mock.wizard.WizardFactory;

/**
 * A factory that allows for simulating user actions when a
 * MockDependenciesWizard is opened.
 */
public abstract class ConfigurableWizardFactory extends WizardFactory
{
    private MockDependenciesWizardPage page;

    public final MockDependenciesWizard createMockDependenciesWizard(MockDependenciesWizardPage page)
    {
        this.page = page;
        return new MockDependenciesWizard(this, page);
    }

    public final WizardDialog createWizardDialog(Shell shell, MockDependenciesWizard wizard)
    {
        return new WizardDialog(shell, wizard)
        {
            public int open()
            {
                setBlockOnOpen(false);
                super.open();
                whenMockDependenciesPageIsOpen();
                return Window.OK;
            }
        };
    }

    protected final void selectAllMockableElements()
    {
        page.checkElements(page.getCheckableElements());
    }

    protected abstract void whenMockDependenciesPageIsOpen();
}
