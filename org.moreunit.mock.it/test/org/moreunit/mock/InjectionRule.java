package org.moreunit.mock;

import static com.google.common.base.Preconditions.checkNotNull;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * Sets members of the test class annotated with @Inject to the corresponding
 * instances used by the tested instance of MoreUnitMockPlugin.
 */
public class InjectionRule implements MethodRule
{
    public Statement apply(Statement s, FrameworkMethod m, Object testCase)
    {
        MoreUnitMockPlugin plugin = MoreUnitMockPlugin.getDefault();

        checkNotNull(plugin, "MoreUnitMockPlugin not found. Are you running this test as a regular JUnit test?");

        pluginLoaded(plugin);

        plugin.getInjector().injectMembers(testCase);

        return s;
    }

    /**
     * To be overridden to modify the plug-in before its injector is used.
     */
    protected void pluginLoaded(MoreUnitMockPlugin plugin)
    {
        // void
    }
}
