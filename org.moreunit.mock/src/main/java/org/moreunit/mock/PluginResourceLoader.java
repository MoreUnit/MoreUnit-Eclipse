package org.moreunit.mock;

import java.io.InputStream;

public class PluginResourceLoader
{
    public InputStream getResourceAsStream(String resourceName)
    {
        return getClass().getResourceAsStream(resourceName);
    }
}
