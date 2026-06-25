package org.moreunit.mock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.lang.reflect.Field;

import org.eclipse.core.runtime.IPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.moreunit.core.log.Logger;
import org.osgi.framework.Bundle;

@Disabled
@ExtendWith(MockitoExtension.class)
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

    @BeforeEach
    public void setUp() throws Exception {
        loader = new PluginResourceLoader(plugin, logger);
        Field field = MoreUnitMockPlugin.class.getDeclaredField("plugin");
        field.setAccessible(true);
        field.set(null, plugin);
    }

    @AfterEach
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
       assertFalse(result);
       verify(logger).error(anyString());
    }
}
