package org.moreunit.mock;

import static org.ops4j.peaberry.Peaberry.osgiModule;
import static org.ops4j.peaberry.eclipse.EclipseRegistry.eclipseRegistry;

import java.io.InputStream;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.moreunit.mock.log.Logger;
import org.moreunit.mock.templates.MockingTemplateStore;
import org.moreunit.mock.templates.MockingTemplates;
import org.moreunit.mock.templates.XmlTemplateDefinitionReader;
import org.moreunit.mock.utils.IOUtils;
import org.moreunit.wizards.MoreUnitStatus;
import org.osgi.framework.BundleContext;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class MoreUnitMockPlugin extends AbstractUIPlugin
{
    public static final String PLUGIN_ID = "org.moreunit.mock"; //$NON-NLS-1$

    static final String TEMPLATE_DIRECTORY = "/templates/";

    private static MoreUnitMockPlugin plugin;

    private Injector injector;
    private Logger logger;
    private PluginResourceLoader pluginResourceLoader;
    private XmlTemplateDefinitionReader templateDefinitionReader;
    private MockingTemplateStore mockingTemplateStore;

    public static MoreUnitMockPlugin getDefault()
    {
        return plugin;
    }

    @Override
    public void start(BundleContext context) throws Exception
    {
        super.start(context);
        log("Starting MoreUnit Mock Plugin...");

        plugin = this;

        Guice.createInjector(osgiModule(context, eclipseRegistry()), new MockPluginCoreModule()).injectMembers(this);

        loadDefaultMockingTemplates();
        getLogger().info("MoreUnit Mock Plugin started.");
    }

    private void log(String message)
    {
        getLog().log(new MoreUnitStatus(IStatus.INFO, message));
    }

    @Inject
    void initDependencies(Injector injector, Logger logger, PluginResourceLoader pluginResourceLoader, XmlTemplateDefinitionReader templateDefinitionReader, MockingTemplateStore templateStore)
    {
        this.injector = injector;
        this.logger = logger;
        this.pluginResourceLoader = pluginResourceLoader;
        this.templateDefinitionReader = templateDefinitionReader;
        this.mockingTemplateStore = templateStore;
    }

    void loadDefaultMockingTemplates()
    {
        getLogger().info("Loading default templates...");

        final String templateFile = TEMPLATE_DIRECTORY + "mockitoWithAnnotationsAndJUnitRunner.xml";

        InputStream definitionStream = pluginResourceLoader.getResourceAsStream(templateFile);
        if(definitionStream == null)
        {
            getLogger().error("Resource not found: " + templateFile);
            return;
        }

        try
        {
            MockingTemplates templates = templateDefinitionReader.read(definitionStream);
            mockingTemplateStore.store(templates);
            getLogger().info("Default templates loaded...");
        }
        catch (Exception e)
        {
            getLogger().error("Could not load default templates", e);
        }
        finally
        {
            IOUtils.closeQuietly(definitionStream);
        }
    }

    public Logger getLogger()
    {
        return logger;
    }

    public String getDefaultTemplateId()
    {
        return "org.moreunit.mock.mockitoWithAnnotationsAndJUnitRunner";
    }

    @Override
    public void stop(BundleContext context) throws Exception
    {
        getLogger().info("Stopping MoreUnit Mock Plugin...");

        mockingTemplateStore.clear();
        plugin = null;

        log("MoreUnit Mock Plugin stopped.");
        super.stop(context);
    }

    public Injector getInjector()
    {
        return injector;
    }

    public MockingTemplateStore getTemplateStore()
    {
        return mockingTemplateStore;
    }
}
