package org.moreunit.mock;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.util.Modules;

/**
 * Allows for overriding the bindings used by the tested instance of
 * MoreUnitMockPlugin. Also sets members of the test class annotated with @Inject
 * to the corresponding instances used by the plugin.
 * 
 * @see InjectionRule
 */
public class BindingOverridingRule extends InjectionRule
{
    private final Module module;

    /**
     * Constructs a new {@link BindingOverridingRule} with the given module.
     * 
     * @param module a module defining bindings that will override the default
     *            bindings of MoreUnitMockPlugin
     */
    public BindingOverridingRule(Module module)
    {
        this.module = module;
    }

    @Override
    protected void pluginLoaded(MoreUnitMockPlugin plugin)
    {
        plugin.init(Modules.override(new MockPluginCoreModule()).with(new Module()
        {
            public void configure(Binder binder)
            {
                module.configure(binder);
            }
        }));
    }
}
