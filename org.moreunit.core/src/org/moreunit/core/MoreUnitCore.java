package org.moreunit.core;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.moreunit.core.preferences.PageManager;
import org.moreunit.core.preferences.Preferences;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class MoreUnitCore extends AbstractUIPlugin
{
    public static final String PLUGIN_ID = "org.moreunit.core"; //$NON-NLS-1$

    private static MoreUnitCore instance;

    private Logger logger;
    private PageManager pageManager;
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
        preferences = new Preferences(getPreferenceStore(), logger);
        pageManager = new PageManager(preferences, logger);
        pageManager.startup();
    }

    public void stop(BundleContext context) throws Exception
    {
        pageManager.shutdown();
        preferences = null;
        logger = null;

        instance = null;
        super.stop(context);
    }

    public Logger getLogger()
    {
        return logger;
    }

    public PageManager getPageManager()
    {
        return pageManager;
    }

    public Preferences getPreferences()
    {
        return preferences;
    }
}
