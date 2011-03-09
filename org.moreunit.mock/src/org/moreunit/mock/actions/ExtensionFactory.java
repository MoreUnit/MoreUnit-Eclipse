package org.moreunit.mock.actions;

import org.moreunit.dependencies.guice.AbstractGuiceInjectorExtensionFactory;
import org.moreunit.mock.MoreUnitMockPlugin;

import com.google.inject.Injector;

public class ExtensionFactory extends AbstractGuiceInjectorExtensionFactory
{
    @Override
    protected Injector getInjector()
    {
        return MoreUnitMockPlugin.getDefault().getInjector();
    }
}
