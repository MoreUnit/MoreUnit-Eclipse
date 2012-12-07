package org.moreunit.mock;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import static org.moreunit.mock.config.MockModule.$;

public class MoreUnitMockPlugin extends AbstractUIPlugin
{
    public static final String PLUGIN_ID = "org.moreunit.mock"; //$NON-NLS-1$

    private static MoreUnitMockPlugin plugin;

    public static MoreUnitMockPlugin getDefault()
    {
        return plugin;
    }

    @Override
    public void start(BundleContext context) throws Exception
    {
        super.start(context);
        plugin = this;
        $().start(context);
    }

    @Override
    public void stop(BundleContext context) throws Exception
    {
        $().stop();
        plugin = null;
        super.stop(context);
    }
}
