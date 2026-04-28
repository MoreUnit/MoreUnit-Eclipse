package org.moreunit.mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.io.File;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import org.osgi.framework.Bundle;

import org.eclipse.core.runtime.IPath;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.moreunit.core.log.Logger;

@RunWith(MockitoJUnitRunner.class)
public class PluginResourceLoaderTest {

    @Mock
    MoreUnitMockPlugin plugin;

    @Mock
    Logger logger;

    @Mock
    Bundle bundle;

    @Mock
    IPath mockStateLocation;

    PluginResourceLoader loader;

    @Before
    public void setUp() throws Exception {
        loader = new PluginResourceLoader(plugin, logger);
        Field field = MoreUnitMockPlugin.class.getDeclaredField("plugin");
        field.setAccessible(true);
        field.set(null, plugin);
    }

    @After
    public void tearDown() throws Exception {
        Field field = MoreUnitMockPlugin.class.getDeclaredField("plugin");
        field.setAccessible(true);
        field.set(null, null);
    }

    @Test
    public void testEnsureStateExists() {
       when(plugin.getStateLocation()).thenReturn(mockStateLocation);
       when(mockStateLocation.append("test")).thenReturn(mockStateLocation);

       File mockFile = mock(File.class);
       when(mockStateLocation.toFile()).thenReturn(mockFile);
       when(mockFile.exists()).thenReturn(false);
       when(mockFile.mkdirs()).thenReturn(false);

       boolean result = loader.ensureStateExists("test");
       assertThat(result).isFalse();
       verify(logger).error(anyString());
    }
}
