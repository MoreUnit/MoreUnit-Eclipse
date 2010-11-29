package org.moreunit.mock;

import java.io.FileNotFoundException;
import java.io.InputStream;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.moreunit.mock.templates.MockingTemplateStore;
import org.moreunit.mock.templates.MockingTemplates;
import org.moreunit.mock.templates.TemplateException;
import org.moreunit.mock.templates.XmlTemplateDefinitionReader;
import org.moreunit.wizards.MoreUnitStatus;
import org.osgi.framework.BundleContext;

public class MoreUnitMockPlugin extends AbstractUIPlugin
{
    public static final String PLUGIN_ID = "org.moreunit.mock"; //$NON-NLS-1$

    static final String TEMPLATE_DIRECTORY = "/templates/";

    private static MoreUnitMockPlugin plugin;

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
        plugin = this;
        initDependencies(new PluginResourceLoader(), new XmlTemplateDefinitionReader(), new MockingTemplateStore());
        loadDefaultMockingTemplates();
    }

    void initDependencies(PluginResourceLoader pluginResourceLoader, XmlTemplateDefinitionReader templateDefinitionReader, MockingTemplateStore templateStore)
    {
        this.pluginResourceLoader = pluginResourceLoader;
        this.templateDefinitionReader = templateDefinitionReader;
        this.mockingTemplateStore = templateStore;
    }

    void loadDefaultMockingTemplates() throws FileNotFoundException, TemplateException
    {
        logInfo("Loading default templates...");

        final String templateFile = TEMPLATE_DIRECTORY + "mockitoWithAnnotationsAndJUnitRunner.xml";

        InputStream definitionStream = pluginResourceLoader.getResourceAsStream(templateFile);
        if(definitionStream == null)
        {
            throw new FileNotFoundException("Resource not found: " + templateFile);
        }

        MockingTemplates templates = templateDefinitionReader.read(definitionStream);

        mockingTemplateStore.store(templates);

        logInfo("Default templates loaded...");
    }

    public void logInfo(String message)
    {
        getLog().log(new MoreUnitStatus(IStatus.INFO, message));
    }

    public String getDefaultTemplateId()
    {
        return "org.moreunit.mock.mockitoWithAnnotationsAndJUnitRunner";
    }

    @Override
    public void stop(BundleContext context) throws Exception
    {
        mockingTemplateStore.clear();
        mockingTemplateStore = null;
        templateDefinitionReader = null;
        plugin = null;
        super.stop(context);
    }

    public MockingTemplateStore getTemplateStore()
    {
        return mockingTemplateStore;
    }
}
