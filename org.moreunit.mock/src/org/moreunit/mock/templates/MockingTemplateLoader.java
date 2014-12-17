package org.moreunit.mock.templates;

import java.net.URL;
import java.util.Collection;

import org.moreunit.core.config.Service;
import org.moreunit.core.log.Logger;
import org.moreunit.mock.PluginResourceLoader;
import org.moreunit.mock.model.MockingTemplates;

public class MockingTemplateLoader implements Service
{
    public static final String TEMPLATE_DIRECTORY = "/templates/";

    private final PluginResourceLoader resourceLoader;
    private final XmlTemplateDefinitionReader templateReader;
    private final MockingTemplateStore templateStore;
    private final Logger logger;

    public MockingTemplateLoader(PluginResourceLoader resourceLoader, XmlTemplateDefinitionReader templateReader, MockingTemplateStore templateStore, Logger logger)
    {
        this.resourceLoader = resourceLoader;
        this.templateReader = templateReader;
        this.templateStore = templateStore;
        this.logger = logger;
    }

    public LoadingResult loadTemplates()
    {
        resourceLoader.ensureStateExists(TEMPLATE_DIRECTORY);

        logger.debug("Loading templates...");

        templateStore.clear();

        LoadingResult result = new LoadingResult();

        Collection<URL> templates = resourceLoader.findBundleResources(TEMPLATE_DIRECTORY, "*.xml");
        for (URL template : templates)
        {
            loadTemplate(template, result);
        }
        logger.debug(String.format("%d default templates loaded", templates.size()));

        templates = resourceLoader.findWorkspaceStateResources(TEMPLATE_DIRECTORY, "*.xml");
        for (URL template : templates)
        {
            loadTemplate(template, result);
        }
        logger.debug(String.format("%d user templates loaded", templates.size()));

        if(templateStore.getCategories().isEmpty())
        {
            logger.error("Failed to find valid templates.");
        }

        return result;
    }

    private void loadTemplate(final URL template, LoadingResult result)
    {
        try
        {
            MockingTemplates templates = templateReader.read(template);
            logger.info("Loaded " + template + ": " + templates.categories());
            templateStore.store(templates);
        }
        // message displayed differs depending on the exception type
        catch (TemplateAlreadyDefinedException e)
        {
            result.addInvalidTemplate(template, e);
            logger.error("Could not load template " + template, e);
        }
        catch (Exception e)
        {
            result.addInvalidTemplate(template, e);
            logger.error("Could not load template " + template, e);
        }
    }

    public String getWorkspaceTemplatesLocation()
    {
        return resourceLoader.getWorkspaceResourceLocation(TEMPLATE_DIRECTORY);
    }

    @Override
    public void start()
    {
        loadTemplates();
    }

    @Override
    public void stop()
    {
        // nothing to do
    }
}
