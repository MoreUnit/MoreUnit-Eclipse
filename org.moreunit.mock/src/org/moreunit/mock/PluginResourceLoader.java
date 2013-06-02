package org.moreunit.mock;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.moreunit.core.log.Logger;
import org.moreunit.mock.utils.WildcardFileFilter;

public class PluginResourceLoader
{
    private static final String RESOURCE_FOLDER = "/resources/";

    private final MoreUnitMockPlugin plugin;
    private final Logger logger;

    public PluginResourceLoader(MoreUnitMockPlugin plugin, Logger logger)
    {
        this.plugin = plugin;
        this.logger = logger;
    }

    public Collection<URL> findBundleResources(String searchRoot, String filePattern)
    {
        Set<URL> resources = new LinkedHashSet<URL>();
        Enumeration<URL> bundleEntries = plugin.getBundle().findEntries(searchRoot, filePattern, true);
        if(bundleEntries == null)
        {
            // in dev mode, Eclipse prepends the source folder to resources, no
            // idea why...
            bundleEntries = plugin.getBundle().findEntries(RESOURCE_FOLDER + searchRoot, filePattern, true);
        }
        if(bundleEntries != null)
        {
            resources.addAll(Collections.list(bundleEntries));
        }
        return resources;
    }

    public Collection<URL> findWorkspaceStateResources(String searchRoot, String filePattern)
    {
        Set<URL> resources = new LinkedHashSet<URL>();
        File templateDirectory = plugin.getStateLocation().append(searchRoot).toFile();
        for (File f : templateDirectory.listFiles(new WildcardFileFilter(filePattern)))
        {
            try
            {
                resources.add(f.toURI().toURL());
            }
            catch (MalformedURLException e)
            {
                logger.error("Failed to resolve resource", e);
            }
        }
        return resources;
    }

    public boolean ensureStateExists(String subPath)
    {
        IPath userTemplateDir = MoreUnitMockPlugin.getDefault().getStateLocation().append(subPath);
        if(! userTemplateDir.toFile().exists())
        {
            userTemplateDir.toFile().mkdir();
            return false;
        }
        return true;
    }

    public String getWorkspaceResourceLocation(String resource)
    {
        return plugin.getStateLocation().append(resource).toOSString();
    }
}
