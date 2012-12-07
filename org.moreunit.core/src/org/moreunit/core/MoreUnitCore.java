package org.moreunit.core;

import static org.moreunit.core.config.CoreModule.$;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class MoreUnitCore extends AbstractUIPlugin
{
    public static final String PLUGIN_ID = "org.moreunit.core"; //$NON-NLS-1$

    private static MoreUnitCore instance;

    public static MoreUnitCore get()
    {
        return instance;
    }

    public void start(BundleContext context) throws Exception
    {
        super.start(context);
        instance = this;

        $().start(context);
    }

    public void stop(BundleContext context) throws Exception
    {
        $().stop();

        instance = null;
        super.stop(context);
    }
}
