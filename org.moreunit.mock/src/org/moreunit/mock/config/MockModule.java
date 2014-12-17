package org.moreunit.mock.config;

import java.net.URL;
import java.util.Collection;

import org.moreunit.core.config.Module;
import org.moreunit.core.log.DefaultLogger;
import org.moreunit.core.log.Logger;
import org.moreunit.mock.DependencyMocker;
import org.moreunit.mock.MoreUnitMockPlugin;
import org.moreunit.mock.PluginResourceLoader;
import org.moreunit.mock.elements.TypeFacadeFactory;
import org.moreunit.mock.preferences.PreferenceStoreManager;
import org.moreunit.mock.preferences.Preferences;
import org.moreunit.mock.preferences.TemplateStyleSelector;
import org.moreunit.mock.templates.ContextFactory;
import org.moreunit.mock.templates.MockingTemplateLoader;
import org.moreunit.mock.templates.MockingTemplateStore;
import org.moreunit.mock.templates.SourceFormatter;
import org.moreunit.mock.templates.TemplateProcessor;
import org.moreunit.mock.templates.XmlTemplateDefinitionReader;
import org.moreunit.mock.util.ConversionUtils;
import org.moreunit.mock.wizard.MockDependenciesPageManager;
import org.moreunit.mock.wizard.WizardFactory;

/**
 * A {@link Module} defining the configuration of the plugin
 * {@code org.moreunit.mock}. This class is a singleton.
 */
public class MockModule extends Module<MockModule>
{
    private static MockModule instance = new MockModule(false);

    private static final String LOG_LEVEL_PROPERTY = "org.moreunit.mock.log.level";

    private Logger logger;
    private MockingTemplateStore mockingTemplateStore;
    private Preferences preferences;
    private PreferenceStoreManager preferenceStoreManager;
    private MockingTemplateLoader templateLoader;

    protected MockModule(boolean override)
    {
        super(override);
    }

    /**
     * Returns the current instance of MockModule. The name of this method is
     * not very descriptive, but it avoids cluttering the code with statements
     * such as: {@code MockModule.getInstance()}.
     */
    public static MockModule $()
    {
        return instance;
    }

    @Override
    protected void setInstance(MockModule newInstance)
    {
        instance = newInstance;
    }

    @Override
    protected MockModule getInstance()
    {
        return instance;
    }

    @Override
    protected void prepare()
    {
        logger = new DefaultLogger(getPlugin().getLog(), MoreUnitMockPlugin.PLUGIN_ID, LOG_LEVEL_PROPERTY);

        preferenceStoreManager = new PreferenceStoreManager(getPlugin().getPreferenceStore(), getLogger());
        preferences = new Preferences(preferenceStoreManager);

        mockingTemplateStore = new MockingTemplateStore();
        registerService(mockingTemplateStore);

        PluginResourceLoader resourceLoader = getPluginResourceLoader();
        templateLoader = new MockingTemplateLoader(resourceLoader, getXmlTemplateDefinitionReader(resourceLoader), mockingTemplateStore, getLogger());
        registerService(templateLoader);
    }

    @Override
    protected void clean()
    {
        templateLoader = null;
        mockingTemplateStore = null;
        preferences = null;
        preferenceStoreManager = null;
        logger = null;
    }

    private ContextFactory getContextFactory()
    {
        return new ContextFactory();
    }

    public ConversionUtils getConversionUtils()
    {
        return new ConversionUtils();
    }

    private DependencyMocker getDependencyMocker()
    {
        return new DependencyMocker(getPreferences(), mockingTemplateStore, getTemplateProcessor(), getLogger());
    }

    public Logger getLogger()
    {
        return logger;
    }

    public MockDependenciesPageManager getMockDependenciesPageManager()
    {
        return new MockDependenciesPageManager(getWizardFactory(), getDependencyMocker(), getLogger());
    }

    public MoreUnitMockPlugin getPlugin()
    {
        return MoreUnitMockPlugin.getDefault();
    }

    private PluginResourceLoader getPluginResourceLoader()
    {
        return new PluginResourceLoader(getPlugin(), getLogger());
    }

    public Preferences getPreferences()
    {
        return preferences;
    }

    private SourceFormatter getSourceFormatter()
    {
        return new SourceFormatter();
    }

    public MockingTemplateLoader getTemplateLoader()
    {
        return templateLoader;
    }

    private TemplateProcessor getTemplateProcessor()
    {
        return new TemplateProcessor(getContextFactory(), getSourceFormatter(), getLogger());
    }

    public TemplateStyleSelector getTemplateStyleSelector()
    {
        return new TemplateStyleSelector(getPreferences(), mockingTemplateStore, getLogger());
    }

    public TypeFacadeFactory getTypeFacadeFactory()
    {
        return new TypeFacadeFactory();
    }

    public WizardFactory getWizardFactory()
    {
        return new WizardFactory(getPreferences(), getTemplateStyleSelector(), getLogger());
    }

    private XmlTemplateDefinitionReader getXmlTemplateDefinitionReader(PluginResourceLoader resourceLoader)
    {
        Collection<URL> xsds = resourceLoader.findBundleResources(MockingTemplateLoader.TEMPLATE_DIRECTORY, "mocking-templates.xsd");
        return new XmlTemplateDefinitionReader(xsds.iterator().next());
    }
}
