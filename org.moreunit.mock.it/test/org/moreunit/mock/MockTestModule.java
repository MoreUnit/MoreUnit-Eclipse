package org.moreunit.mock;

import org.moreunit.mock.WizardDriver.ConfigurableWizardFactory;
import org.moreunit.mock.config.MockModule;
import org.moreunit.mock.wizard.WizardFactory;

public class MockTestModule extends MockModule
{
    public WizardDriver wizardDriver = new WizardDriver();

    public MockTestModule()
    {
        super(true);
    }

    @Override
    public WizardFactory getWizardFactory()
    {
        return new ConfigurableWizardFactory(wizardDriver, getPreferences(), getTemplateStyleSelector(), getLogger());
    }
}
