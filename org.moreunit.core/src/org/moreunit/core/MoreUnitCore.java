package org.moreunit.core;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.moreunit.core.extension.LanguageExtensionManager;
import org.moreunit.core.languages.LanguageRepository;
import org.moreunit.core.languages.MainLanguageRepository;
import org.moreunit.core.log.DefaultLogger;
import org.moreunit.core.log.Logger;
import org.moreunit.core.preferences.LanguagePageManager;
import org.moreunit.core.preferences.Preferences;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class MoreUnitCore extends AbstractUIPlugin
{
    public static final String PLUGIN_ID = "org.moreunit.core"; //$NON-NLS-1$

    private static final String LOG_LEVEL_PROPERTY = "org.moreunit.core.log.level";

    private static MoreUnitCore instance;

    private List<Service> services = new ArrayList<Service>();
    private Logger logger;
    private LanguagePageManager pageManager;
    private LanguageExtensionManager languageExtensionManager;
    private MainLanguageRepository languageRepository;
    private Preferences preferences;

    public static MoreUnitCore get()
    {
        return instance;
    }

    public void start(BundleContext context) throws Exception
    {
        super.start(context);
        instance = this;

        logger = new DefaultLogger(getLog(), PLUGIN_ID, LOG_LEVEL_PROPERTY);
        preferences = new Preferences(getPreferenceStore(), logger);

        languageExtensionManager = new LanguageExtensionManager(context, logger);

        languageRepository = new MainLanguageRepository(preferences, languageExtensionManager);
        services.add(languageRepository);

        pageManager = new LanguagePageManager(languageRepository, preferences, logger);
        services.add(pageManager);
        languageRepository.addListener(pageManager);

        startServices();
    }

    private void startServices()
    {
        for (Service s : services)
        {
            try
            {
                s.start();
            }
            catch (Exception e)
            {
                logger.error("Could not start service " + s, e);
            }
        }
    }

    public void stop(BundleContext context) throws Exception
    {
        stopServices();
        pageManager = null;
        languageRepository = null;
        languageExtensionManager = null;
        preferences = null;
        logger = null;

        instance = null;
        super.stop(context);
    }

    private void stopServices()
    {
        for (ListIterator<Service> it = services.listIterator(services.size()); it.hasPrevious();)
        {
            Service s = it.previous();
            try
            {
                s.stop();
                it.remove();
            }
            catch (Exception e)
            {
                logger.error("Could not stop service " + s, e);
            }
        }
    }

    public LanguageExtensionManager getLanguageExtensionManager()
    {
        return languageExtensionManager;
    }

    public LanguageRepository getLanguageRepository()
    {
        return languageRepository;
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
