package org.moreunit.mock;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin
{
    public static final String PLUGIN_ID = "mock"; //$NON-NLS-1$

    private static Activator plugin;

    public Activator()
    {
    }

    public static Activator getDefault()
    {
        return plugin;
    }

    @Override
    public void start(BundleContext context) throws Exception
    {
        super.start(context);
        plugin = this;
        new POC().test();
    }

    @Override
    public void stop(BundleContext context) throws Exception
    {
        plugin = null;
        super.stop(context);
    }
}
