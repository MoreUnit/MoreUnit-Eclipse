package org.moreunit.mock;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import com.google.inject.Module;

public class ReplaceInjectionModuleRule implements MethodRule
{
    private final Module module;

    public ReplaceInjectionModuleRule(Module module)
    {
        this.module = module;
    }

    public Statement apply(Statement s, FrameworkMethod m, Object testCase)
    {
        MoreUnitMockPlugin.getDefault().init(module);
        MoreUnitMockPlugin.getDefault().getInjector().injectMembers(testCase);
        return s;
    }
}
