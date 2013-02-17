package org.moreunit.core.config;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.search.core.text.TextSearchEngine;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.moreunit.core.MoreUnitCore;
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
import org.moreunit.core.resources.EclipseWorkspace;
import org.moreunit.core.resources.SrcFile;
import org.moreunit.core.resources.Workspace;
import org.moreunit.core.ui.DialogFactory;
import org.moreunit.core.ui.ImageRegistry;
import org.moreunit.core.ui.UserInterface;
import org.moreunit.core.ui.WizardFactory;

/**
 * A {@link Module} defining the configuration of the plugin
 * {@code org.moreunit.core}. This class is a singleton.
 */
public class CoreModule extends Module<CoreModule>
{
    private static CoreModule instance = new CoreModule(false);

    private static final String LOG_LEVEL_PROPERTY = "org.moreunit.core.log.level";

    private Logger logger;
    private LanguagePageManager pageManager;
    private LanguageExtensionManager languageExtensionManager;
    private MainLanguageRepository languageRepository;
    private Preferences preferences;
    private ImageRegistry imageRegistry;

    protected CoreModule(boolean override)
    {
        super(override);
    }

    /**
     * Returns the current instance of CoreModule. The name of this method is
     * not very descriptive, but it avoids cluttering the code with statements
     * such as: {@code CoreModule.getInstance()}.
     */
    public static CoreModule $()
    {
        return instance;
    }

    @Override
    protected void setInstance(CoreModule newInstance)
    {
        instance = newInstance;
    }

    @Override
    protected CoreModule getInstance()
    {
        return instance;
    }

    @Override
    protected void prepare()
    {
        logger = new DefaultLogger(getPlugin().getLog(), MoreUnitCore.PLUGIN_ID, LOG_LEVEL_PROPERTY);
        preferences = new Preferences(getPlugin().getPreferenceStore(), logger);
        imageRegistry = new ImageRegistry();

        languageExtensionManager = new LanguageExtensionManager(getContext(), logger);

        languageRepository = new MainLanguageRepository(preferences, languageExtensionManager);
        registerService(languageRepository);

        pageManager = new LanguagePageManager(languageRepository, preferences, logger);
        registerService(pageManager);
        languageRepository.addListener(pageManager);
    }

    @Override
    protected void clean()
    {
        pageManager = null;
        languageRepository = null;
        languageExtensionManager = null;
        imageRegistry = null;
        preferences = null;
        logger = null;
    }

    public DialogFactory getDialogFactory(Shell activeShell)
    {
        return new DialogFactory(activeShell);
    }

    public ExecutionContext getExecutionContext(ExecutionEvent event)
    {
        return new ExecutionContext(event, getLogger());
    }

    public FileMatcher createFileMatcherFor(SrcFile srcFile)
    {
        return new FileMatcher(srcFile, getSearchEngine(), getFileMatchSelector());
    }

    public FileMatchSelector getFileMatchSelector()
    {
        return new DefaultFileMatchSelector(getLogger());
    }

    public ImageRegistry getImageRegistry()
    {
        return imageRegistry;
    }

    public JumpActionExecutor getJumpActionExecutor()
    {
        return new JumpActionExecutor(getJumperExtensionManager());
    }

    public JumperExtensionManager getJumperExtensionManager()
    {
        return new JumperExtensionManager(getLanguageExtensionManager(), getLogger());
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

    public UserInterface getUserInterface(IWorkbench workbench, IWorkbenchPage activePage, Shell activeShell)
    {
        return new UserInterface(workbench, activePage, getDialogFactory(activeShell), getWizardFactory(workbench, activeShell), getLogger());
    }

    public WizardFactory getWizardFactory(IWorkbench workbench, Shell activeShell)
    {
        return new WizardFactory(workbench, activeShell);
    }

    public Workspace getWorkspace()
    {
        return EclipseWorkspace.get();
    }
}
