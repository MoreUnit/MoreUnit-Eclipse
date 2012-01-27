package org.moreunit.mock;

import org.junit.Rule;
import org.moreunit.test.context.TestContextRule;

public class UiTestCase
{
    protected final WizardDriver wizardDriver = new WizardDriver();

    @Rule
    public final BindingOverridingRule bindingOverridingRule = new BindingOverridingRule(wizardDriver.createModule());

    @Rule
    public final TestContextRule context = new TestContextRule();
}
