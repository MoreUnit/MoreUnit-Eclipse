package org.moreunit.core;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class MoreUnitCore extends AbstractUIPlugin
{
    public static final String PLUGIN_ID = "org.moreunit.core"; //$NON-NLS-1$

    private static MoreUnitCore instance;

    private Logger logger;
    private Preferences preferences;

    public static MoreUnitCore get()
    {
        return instance;
    }

    public void start(BundleContext context) throws Exception
    {
        super.start(context);
        instance = this;

        logger = new Logger(getLog());
        preferences = new Preferences(logger);
    }

    public void stop(BundleContext context) throws Exception
    {
        preferences = null;
        logger = null;

        instance = null;
        super.stop(context);
    }

    public Logger getLogger()
    {
        return logger;
    }

    public Preferences getPreferences()
    {
        return preferences;
    }
}
