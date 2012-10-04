package org.moreunit.core;

import org.moreunit.core.config.Module;
import org.moreunit.core.ui.WizardDriver;

public class TestModule extends Module
{
    public ConfigItem<WizardDriver> wizardDriver = ConfigItem.useDefault();

    public TestModule()
    {
        super(true);
    }

    @Override
    public WizardDriver getWizardDriver()
    {
        if(wizardDriver.isOverridden())
        {
            return wizardDriver.get();
        }
        return super.getWizardDriver();
    }
}
