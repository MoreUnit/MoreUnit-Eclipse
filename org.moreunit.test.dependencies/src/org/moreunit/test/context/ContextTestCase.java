package org.moreunit.test.context;

import org.junit.jupiter.api.extension.RegisterExtension;

public abstract class ContextTestCase
{
    @RegisterExtension
    public final TestContextRule context = new TestContextRule();

    public org.moreunit.preferences.Preferences getPreferences()
    {
        return org.moreunit.preferences.Preferences.getInstance();
    }
}
