package org.moreunit.mock;

import org.junit.Rule;
import org.moreunit.test.context.TestContextRule;

public class UiTestCase
{
    @Rule
    public final TestContextRule context = new TestContextRule();

    private MockTestModule config = new MockTestModule()
    {
        {
            wizardDriver = new WizardDriver();
        }
    };

    protected final WizardDriver wizardDriver = config.wizardDriver;
}
