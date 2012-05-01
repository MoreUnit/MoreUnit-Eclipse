package org.moreunit.mock.wizard;

import org.eclipse.jdt.core.IType;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.moreunit.mock.dependencies.DependencyInjectionPointProvider;
import org.moreunit.mock.dependencies.DependencyInjectionPointStore;
import org.moreunit.mock.log.Logger;
import org.moreunit.mock.preferences.Preferences;
import org.moreunit.mock.preferences.TemplateStyleSelector;

import com.google.inject.Inject;

public class WizardFactory
{
    private Preferences preferences;
    private TemplateStyleSelector templateStyleSelector;
    private Logger logger;

    @Inject
    public WizardFactory(Preferences preferences, TemplateStyleSelector templateStyleSelector, Logger logger)
    {
        this.preferences = preferences;
        this.templateStyleSelector = templateStyleSelector;
        this.logger = logger;
    }

    public MockDependenciesWizardPage createMockDependenciesWizardPage(IType classUnderTest, DependencyInjectionPointProvider provider, DependencyInjectionPointStore store)
    {
        return new MockDependenciesWizardPage(classUnderTest, provider, store, preferences, templateStyleSelector, logger);
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
