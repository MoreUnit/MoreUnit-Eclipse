package org.moreunit.mock.templates;

import java.net.URL;
import java.util.Collection;

import org.moreunit.mock.PluginResourceLoader;
import org.moreunit.mock.log.Logger;
import org.moreunit.mock.model.MockingTemplates;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class MockingTemplateLoader
{
    static final String TEMPLATE_DIRECTORY = "/templates/";

    private final PluginResourceLoader resourceLoader;
    private final XmlTemplateDefinitionReader templateReader;
    private final MockingTemplateStore templateStore;
    private final Logger logger;

    @Inject
    public MockingTemplateLoader(PluginResourceLoader resourceLoader, XmlTemplateDefinitionReader templateReader, MockingTemplateStore templateStore, Logger logger)
    {
        this.resourceLoader = resourceLoader;
        this.templateReader = templateReader;
        this.templateStore = templateStore;
        this.logger = logger;
    }

    public void loadTemplates()
    {
        resourceLoader.ensureStateExists(TEMPLATE_DIRECTORY);

        logger.debug("Loading templates...");

        templateStore.clear();

        Collection<URL> templates = resourceLoader.findBundleResources(TEMPLATE_DIRECTORY, "*.xml");
        for (URL template : templates)
        {
            loadTemplate(template, false);
        }
        logger.debug(String.format("%d default templates loaded", templates.size()));

        templates = resourceLoader.findWorkspaceStateResources(TEMPLATE_DIRECTORY, "*.xml");
        for (URL template : templates)
        {
            loadTemplate(template, true);
        }
        logger.debug(String.format("%d user templates loaded", templates.size()));

        if(templateStore.getCategories().isEmpty())
        {
            logger.error("Failed to find valid templates.");
        }
    }

    private void loadTemplate(final URL template, boolean isUserTemplate)
    {
        try
        {
            MockingTemplates templates = templateReader.read(template);
            logger.info("Loaded " + template + ": " + templates.categories());
            templateStore.store(templates);
        }
        catch (Exception e)
        {
            logger.error("Could not load template " + template, e);
        }
    }

    public String getTemplatesLocation()
    {
        String location = resourceLoader.getResourcesLocation();

        if(location.charAt(location.length() - 1) == '/')
        {
            location = location.substring(0, location.length() - 1);
        }

        int lastColumnIdx = location.lastIndexOf(":");
        if(lastColumnIdx != - 1)
        {
            location = location.substring(lastColumnIdx + 1);
        }

        return location + TEMPLATE_DIRECTORY;
    }
}
