package org.moreunit.mock;

import java.io.InputStream;

import com.google.inject.Singleton;

@Singleton
public class PluginResourceLoader
{
    public InputStream getResourceAsStream(String resourceName)
    {
        return getClass().getResourceAsStream(resourceName);
    }
}
