package org.moreunit.mock;

import org.junit.jupiter.api.extension.RegisterExtension;
import org.moreunit.test.context.TestContextRule;

public class UiTestCase
{
    @RegisterExtension
    public final TestContextRule context = new TestContextRule();

    private MockTestModule config = new MockTestModule()
    {
        {
            wizardDriver = new WizardDriver();
        }
    };

    protected final WizardDriver wizardDriver = config.wizardDriver;
}
