package org.moreunit.mock.wizard;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.moreunit.core.log.Logger;
import org.moreunit.mock.dependencies.DependencyInjectionPointStore;
import org.moreunit.mock.preferences.Preferences;
import org.moreunit.mock.preferences.TemplateStyleSelector;

public class WizardFactory
{
    private Preferences preferences;
    private TemplateStyleSelector templateStyleSelector;
    private Logger logger;

    public WizardFactory(Preferences preferences, TemplateStyleSelector templateStyleSelector, Logger logger)
    {
        this.preferences = preferences;
        this.templateStyleSelector = templateStyleSelector;
        this.logger = logger;
    }

    public MockDependenciesWizardPage createMockDependenciesWizardPage(MockDependenciesWizardValues wizardValues, DependencyInjectionPointStore store)
    {
        return new MockDependenciesWizardPage(wizardValues, store, preferences, templateStyleSelector, logger);
    }

    public MockDependenciesWizard createMockDependenciesWizard(MockDependenciesWizardPage page)
    {
        return new MockDependenciesWizard(this, page);
    }

    public WizardDialog createWizardDialog(Shell shell, MockDependenciesWizard wizard)
    {
        return new WizardDialog(shell, wizard);
    }
}
