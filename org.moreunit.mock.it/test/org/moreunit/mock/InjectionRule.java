package org.moreunit.mock;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class InjectionRule implements MethodRule
{
    public Statement apply(Statement s, FrameworkMethod m, Object testCase)
    {
        MoreUnitMockPlugin.getDefault().getInjector().injectMembers(testCase);
        return s;
    }
}
