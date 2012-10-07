package org.moreunit.core.config;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.search.core.text.TextSearchEngine;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.moreunit.core.MoreUnitCore;
import org.moreunit.core.Service;
import org.moreunit.core.commands.ExecutionContext;
import org.moreunit.core.commands.JumpActionExecutor;
import org.moreunit.core.extension.JumperExtensionManager;
import org.moreunit.core.extension.LanguageExtensionManager;
import org.moreunit.core.languages.LanguageRepository;
import org.moreunit.core.languages.MainLanguageRepository;
import org.moreunit.core.log.DefaultLogger;
import org.moreunit.core.log.Logger;
import org.moreunit.core.matching.DefaultFileMatchSelector;
import org.moreunit.core.matching.FileMatchSelector;
import org.moreunit.core.matching.FileMatcher;
import org.moreunit.core.matching.SearchEngine;
import org.moreunit.core.preferences.LanguagePageManager;
import org.moreunit.core.preferences.Preferences;
import org.moreunit.core.ui.DialogFactory;
import org.moreunit.core.ui.UserInterface;
import org.moreunit.core.ui.WizardFactory;
import org.osgi.framework.BundleContext;

public class Module
{
    static Module instance = new Module();

    private static final String LOG_LEVEL_PROPERTY = "org.moreunit.core.log.level";

    private final List<Service> services = new ArrayList<Service>();
    private BundleContext context;
    private Logger logger;
    private LanguagePageManager pageManager;
    private LanguageExtensionManager languageExtensionManager;
    private MainLanguageRepository languageRepository;
    private Preferences preferences;

    private Module()
    {
        this(false);
    }

    protected Module(boolean override)
    {
        instance = override ? handleReplacement() : this;
    }

    protected Module handleReplacement()
    {
        BundleContext ctxt = null;
        if(instance != null)
        {
            ctxt = instance.context;
            instance.stop();
        }

        if(ctxt != null)
        {
            this.start(ctxt);
        }

        return this;
    }

    public static Module $()
    {
        return instance;
    }

    public void start(BundleContext context)
    {
        this.context = context;
        logger = new DefaultLogger(getPlugin().getLog(), MoreUnitCore.PLUGIN_ID, LOG_LEVEL_PROPERTY);
        preferences = new Preferences(getPlugin().getPreferenceStore(), logger);

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

    public void stop()
    {
        stopServices();

        pageManager = null;
        languageRepository = null;
        languageExtensionManager = null;
        preferences = null;
        logger = null;

        context = null;
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

    public DialogFactory getDialogFactory(Shell activeShell)
    {
        return new DialogFactory(activeShell);
    }

    public ExecutionContext getExecutionContext(ExecutionEvent event)
    {
        return new ExecutionContext(event, this, getLogger());
    }

    public FileMatcher getFileMatcher()
    {
        return new FileMatcher(getSearchEngine(), getPreferences(), getFileMatchSelector());
    }

    public FileMatchSelector getFileMatchSelector()
    {
        return new DefaultFileMatchSelector(getLogger());
    }

    public JumperExtensionManager getJumperExtensionManager()
    {
        return new JumperExtensionManager(getLanguageExtensionManager(), getLogger());
    }

    public LanguageExtensionManager getLanguageExtensionManager()
    {
        return languageExtensionManager;
    }

    public Logger getLogger()
    {
        return logger;
    }

    public MoreUnitCore getPlugin()
    {
        return MoreUnitCore.get();
    }

    public Preferences getPreferences()
    {
        return preferences;
    }

    public SearchEngine getSearchEngine()
    {
        return new SearchEngine(TextSearchEngine.create(), getLogger());
    }

    public LanguageRepository getLanguageRepository()
    {
        return languageRepository;
    }

    public JumpActionExecutor getJumpActionExecutor()
    {
        return new JumpActionExecutor(getJumperExtensionManager(), getFileMatcher());
    }

    public UserInterface getUserInterface(IWorkbench workbench, IWorkbenchPage activePage, Shell activeShell)
    {
        return new UserInterface(workbench, activePage, getDialogFactory(activeShell), getWizardFactory(workbench, activeShell), getLogger());
    }

    public WizardFactory getWizardFactory(IWorkbench workbench, Shell activeShell)
    {
        return new WizardFactory(workbench, activeShell, getLogger());
    }
}
