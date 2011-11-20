package org.moreunit.mock.wizard;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

public class WizardFactory
{
    public MockDependenciesWizard createMockDependenciesWizard(MockDependenciesWizardPage page)
    {
        return new MockDependenciesWizard(this, page);
    }

    public WizardDialog createWizardDialog(Shell shell, MockDependenciesWizard wizard)
    {
        return new WizardDialog(shell, wizard);
    }
}
