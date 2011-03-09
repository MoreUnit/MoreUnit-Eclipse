package org.moreunit.mock;

import org.eclipse.core.runtime.ILog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.moreunit.mock.log.DefaultLogger;
import org.moreunit.mock.log.Logger;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;

public class MockPluginCoreModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(Logger.class).to(DefaultLogger.class);
    }

    @Provides
    protected MoreUnitMockPlugin providePlugin()
    {
        return MoreUnitMockPlugin.getDefault();
    }

    @Provides
    @Inject
    protected ILog provideLog(MoreUnitMockPlugin plugin)
    {
        return plugin.getLog();
    }

    @Provides
    @Inject
    protected IPreferenceStore provideWorkspacePreferenceStore(MoreUnitMockPlugin plugin)
    {
        return plugin.getPreferenceStore();
    }
}
