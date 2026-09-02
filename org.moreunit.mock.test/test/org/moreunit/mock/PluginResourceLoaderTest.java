package org.moreunit.mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;

import org.eclipse.core.runtime.IPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.moreunit.core.log.Logger;
import org.osgi.framework.Bundle;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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

    @Test
    public void ensureStateExists_should_return_true_when_state_directory_already_exists() throws Exception {
        // given
        when(plugin.getStateLocation()).thenReturn(mockStateLocation);
        when(mockStateLocation.append("templates")).thenReturn(mockStateLocation);

        File stateDir = tempDir("existingState");
        when(mockStateLocation.toFile()).thenReturn(stateDir);

        // when + then
        assertEquals(true, loader.ensureStateExists("templates"));
    }

    @Test
    public void findBundleResources_should_return_bundle_entries() throws Exception {
        // given
        URL url = new URL("file:/plugin/resources/templates/foo.xml");
        when(plugin.getBundle()).thenReturn(bundle);
        when(bundle.findEntries("templates", "*.xml", true)).thenReturn(Collections.enumeration(Collections.singletonList(url)));

        // when + then
        Collection<URL> resources = loader.findBundleResources("templates", "*.xml");
        assertEquals(1, resources.size());
        assertEquals(url, resources.iterator().next());
    }

    @Test
    public void findBundleResources_should_look_into_resources_folder_when_bundle_entries_are_not_found() throws Exception {
        // given
        URL url = new URL("file:/plugin/resources/resources/templates/foo.xml");
        when(plugin.getBundle()).thenReturn(bundle);
        when(bundle.findEntries("templates", "*.xml", true)).thenReturn(null);
        when(bundle.findEntries("/resources/templates", "*.xml", true)).thenReturn(Collections.enumeration(Collections.singletonList(url)));

        // when + then
        Collection<URL> resources = loader.findBundleResources("templates", "*.xml");
        assertEquals(1, resources.size());
        assertEquals(url, resources.iterator().next());
    }

    @Test
    public void findBundleResources_should_return_nothing_when_no_entry_is_found() throws Exception {
        // given
        when(plugin.getBundle()).thenReturn(bundle);
        when(bundle.findEntries(anyString(), anyString(), org.mockito.ArgumentMatchers.anyBoolean())).thenReturn(null);

        // when + then
        assertEquals(0, loader.findBundleResources("templates", "*.xml").size());
    }

    @Test
    public void findWorkspaceStateResources_should_return_files_of_state_directory_matching_pattern() throws Exception {
        // given
        when(plugin.getStateLocation()).thenReturn(mockStateLocation);
        when(mockStateLocation.append("templates")).thenReturn(mockStateLocation);

        File stateDir = tempDir("workspaceState");
        new File(stateDir, "foo.xml").createNewFile();
        new File(stateDir, "bar.txt").createNewFile();
        when(mockStateLocation.toFile()).thenReturn(stateDir);

        // when
        Collection<URL> resources = loader.findWorkspaceStateResources("templates", "*.xml");

        // then
        assertEquals(1, resources.size());
        String fileName = resources.iterator().next().getFile();
        assertEquals(true, fileName.endsWith("foo.xml"));
    }

    @Test
    public void getWorkspaceResourceLocation_should_return_os_string_of_state_location() throws Exception {
        // given
        when(plugin.getStateLocation()).thenReturn(mockStateLocation);
        when(mockStateLocation.append("templates")).thenReturn(mockStateLocation);
        when(mockStateLocation.toOSString()).thenReturn("/state/location/templates");

        // when + then
        assertEquals("/state/location/templates", loader.getWorkspaceResourceLocation("templates"));
    }

    private static File tempDir(String name) throws Exception {
        File dir = new File(System.getProperty("java.io.tmpdir"), "moreunit-mock-test-" + name + "-" + System.nanoTime());
        dir.mkdirs();
        dir.deleteOnExit();
        return dir;
    }
}
