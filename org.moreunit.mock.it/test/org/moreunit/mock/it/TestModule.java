package org.moreunit.mock.it;

import org.moreunit.mock.MockPluginCoreModule;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.util.Modules;

public class TestModule implements Module
{
    private Module delegate = Modules.override(new MockPluginCoreModule()).with(new AbstractModule()
    {
        @Override
        protected void configure()
        {
            System.err.println("test module loaded");
        }
    });

    public void configure(Binder binder)
    {
        delegate.configure(binder);
    }
}
