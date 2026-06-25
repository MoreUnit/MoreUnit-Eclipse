package org.moreunit.launch;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jdt.core.IMember;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.moreunit.MoreUnitPlugin;
import org.moreunit.extensionpoints.ITestLaunchSupport;
import org.moreunit.extensionpoints.ITestLaunchSupport.Cardinality;
import org.moreunit.extensionpoints.TestType;
import org.moreunit.log.LogHandler;

public class AdditionalTestLaunchShortcutProviderTest
{
    @Test
    public void getInstance_should_return_non_null_singleton()
    {
        assertNotNull(AdditionalTestLaunchShortcutProvider.getInstance());
    }

    @Test
    public void getInstance_should_return_same_instance_on_repeated_calls()
    {
        assertSame(AdditionalTestLaunchShortcutProvider.getInstance(), AdditionalTestLaunchShortcutProvider.getInstance());
    }

    @Test
    public void isShortcutFor_should_return_false_when_no_extension_provides_one()
    {
        try (MockedStatic<Platform> platformMock = mockStatic(Platform.class))
        {
            IExtensionRegistry registry = mock(IExtensionRegistry.class);
            when(registry.getConfigurationElementsFor(any())).thenReturn(new IConfigurationElement[0]);
            platformMock.when(Platform::getExtensionRegistry).thenReturn(registry);

            try (MockedStatic<LogHandler> logHandlerMock = mockStatic(LogHandler.class))
            {
                logHandlerMock.when(LogHandler::getInstance).thenReturn(mock(LogHandler.class));

                AdditionalTestLaunchShortcutProvider provider = new AdditionalTestLaunchShortcutProvider();
                boolean result = provider.isShortcutFor("junit5", IMember.class, Cardinality.ONE);

                assertFalse(result);
            }
        }
    }

    @Test
    public void getShorcutFor_should_return_null_when_no_extension_provides_one()
    {
        try (MockedStatic<Platform> platformMock = mockStatic(Platform.class))
        {
            IExtensionRegistry registry = mock(IExtensionRegistry.class);
            when(registry.getConfigurationElementsFor(any())).thenReturn(new IConfigurationElement[0]);
            platformMock.when(Platform::getExtensionRegistry).thenReturn(registry);

            try (MockedStatic<LogHandler> logHandlerMock = mockStatic(LogHandler.class))
            {
                logHandlerMock.when(LogHandler::getInstance).thenReturn(mock(LogHandler.class));

                AdditionalTestLaunchShortcutProvider provider = new AdditionalTestLaunchShortcutProvider();
                ILaunchShortcut shortcut = provider.getShorcutFor("junit5", IMember.class, Cardinality.ONE);

                assertNull(shortcut);
            }
        }
    }

    @Test
    public void getShorcutFor_with_elementCount_should_translate_to_cardinality_ONE()
    {
        try (MockedStatic<Platform> platformMock = mockStatic(Platform.class))
        {
            IExtensionRegistry registry = mock(IExtensionRegistry.class);
            when(registry.getConfigurationElementsFor(any())).thenReturn(new IConfigurationElement[0]);
            platformMock.when(Platform::getExtensionRegistry).thenReturn(registry);

            try (MockedStatic<LogHandler> logHandlerMock = mockStatic(LogHandler.class))
            {
                logHandlerMock.when(LogHandler::getInstance).thenReturn(mock(LogHandler.class));

                AdditionalTestLaunchShortcutProvider provider = new AdditionalTestLaunchShortcutProvider();
                ILaunchShortcut shortcut = provider.getShorcutFor("junit5", IMember.class, 1);

                assertNull(shortcut);
            }
        }
    }

    @Test
    public void getShorcutFor_with_elementCount_should_translate_to_cardinality_SEVERAL()
    {
        try (MockedStatic<Platform> platformMock = mockStatic(Platform.class))
        {
            IExtensionRegistry registry = mock(IExtensionRegistry.class);
            when(registry.getConfigurationElementsFor(any())).thenReturn(new IConfigurationElement[0]);
            platformMock.when(Platform::getExtensionRegistry).thenReturn(registry);

            try (MockedStatic<LogHandler> logHandlerMock = mockStatic(LogHandler.class))
            {
                logHandlerMock.when(LogHandler::getInstance).thenReturn(mock(LogHandler.class));

                AdditionalTestLaunchShortcutProvider provider = new AdditionalTestLaunchShortcutProvider();
                ILaunchShortcut shortcut = provider.getShorcutFor("junit5", IMember.class, 3);

                assertNull(shortcut);
            }
        }
    }

    @Test
    public void getShorcutFor_should_return_first_matching_support_shortcut() throws Exception
    {
        ILaunchShortcut shortcut1 = mock(ILaunchShortcut.class);
        ITestLaunchSupport support1 = mock(ITestLaunchSupport.class);
        when(support1.isLaunchSupported(TestType.JUNIT_5, IMember.class, Cardinality.ONE)).thenReturn(true);
        when(support1.getShortcut()).thenReturn(shortcut1);

        IConfigurationElement configElement1 = mock(IConfigurationElement.class);
        when(configElement1.createExecutableExtension("class")).thenReturn(support1);

        try (MockedStatic<Platform> platformMock = mockStatic(Platform.class))
        {
            IExtensionRegistry registry = mock(IExtensionRegistry.class);
            when(registry.getConfigurationElementsFor(any())).thenReturn(new IConfigurationElement[] { configElement1 });
            platformMock.when(Platform::getExtensionRegistry).thenReturn(registry);

            try (MockedStatic<LogHandler> logHandlerMock = mockStatic(LogHandler.class))
            {
                logHandlerMock.when(LogHandler::getInstance).thenReturn(mock(LogHandler.class));

                AdditionalTestLaunchShortcutProvider provider = new AdditionalTestLaunchShortcutProvider();
                ILaunchShortcut result = provider.getShorcutFor("junit5", IMember.class, Cardinality.ONE);

                assertSame(shortcut1, result);
            }
        }
    }

    @Test
    public void getShorcutFor_should_return_null_when_support_does_not_handle_the_cardinality() throws Exception
    {
        ITestLaunchSupport support = mock(ITestLaunchSupport.class);
        when(support.isLaunchSupported(any(TestType.class), any(Class.class), any(Cardinality.class))).thenReturn(false);

        IConfigurationElement configElement = mock(IConfigurationElement.class);
        when(configElement.createExecutableExtension("class")).thenReturn(support);

        try (MockedStatic<Platform> platformMock = mockStatic(Platform.class))
        {
            IExtensionRegistry registry = mock(IExtensionRegistry.class);
            when(registry.getConfigurationElementsFor(any())).thenReturn(new IConfigurationElement[] { configElement });
            platformMock.when(Platform::getExtensionRegistry).thenReturn(registry);

            try (MockedStatic<LogHandler> logHandlerMock = mockStatic(LogHandler.class))
            {
                logHandlerMock.when(LogHandler::getInstance).thenReturn(mock(LogHandler.class));

                AdditionalTestLaunchShortcutProvider provider = new AdditionalTestLaunchShortcutProvider();
                ILaunchShortcut result = provider.getShorcutFor("junit5", IMember.class, Cardinality.ONE);

                assertNull(result);
            }
        }
    }

    @Test
    public void getShorcutFor_should_skip_extensions_that_throw_CoreException() throws Exception
    {
        IConfigurationElement badElement = mock(IConfigurationElement.class);
        when(badElement.createExecutableExtension("class")).thenThrow(new CoreException(new Status(IStatus.ERROR, "test", "boom")));

        try (MockedStatic<Platform> platformMock = mockStatic(Platform.class))
        {
            IExtensionRegistry registry = mock(IExtensionRegistry.class);
            when(registry.getConfigurationElementsFor(any())).thenReturn(new IConfigurationElement[] { badElement });
            platformMock.when(Platform::getExtensionRegistry).thenReturn(registry);

            try (MockedStatic<LogHandler> logHandlerMock = mockStatic(LogHandler.class))
            {
                logHandlerMock.when(LogHandler::getInstance).thenReturn(mock(LogHandler.class));

                AdditionalTestLaunchShortcutProvider provider = new AdditionalTestLaunchShortcutProvider();
                ILaunchShortcut result = provider.getShorcutFor("junit5", IMember.class, Cardinality.ONE);

                assertNull(result);
            }
        }
    }

    @Test
    public void getShorcutFor_should_skip_extensions_returning_non_ITestLaunchSupport_objects() throws Exception
    {
        IConfigurationElement wrongClassElement = mock(IConfigurationElement.class);
        when(wrongClassElement.createExecutableExtension("class")).thenReturn("not a test launch support");

        try (MockedStatic<Platform> platformMock = mockStatic(Platform.class))
        {
            IExtensionRegistry registry = mock(IExtensionRegistry.class);
            when(registry.getConfigurationElementsFor(any())).thenReturn(new IConfigurationElement[] { wrongClassElement });
            platformMock.when(Platform::getExtensionRegistry).thenReturn(registry);

            try (MockedStatic<LogHandler> logHandlerMock = mockStatic(LogHandler.class))
            {
                logHandlerMock.when(LogHandler::getInstance).thenReturn(mock(LogHandler.class));

                AdditionalTestLaunchShortcutProvider provider = new AdditionalTestLaunchShortcutProvider();
                ILaunchShortcut result = provider.getShorcutFor("junit5", IMember.class, Cardinality.ONE);

                assertNull(result);
            }
        }
    }

    @Test
    public void getShorcutFor_should_stop_after_finding_first_match() throws Exception
    {
        ILaunchShortcut shortcut1 = mock(ILaunchShortcut.class);
        ITestLaunchSupport support1 = mock(ITestLaunchSupport.class);
        when(support1.isLaunchSupported(TestType.JUNIT_5, IMember.class, Cardinality.ONE)).thenReturn(true);
        when(support1.getShortcut()).thenReturn(shortcut1);

        // A second support that explicitly does NOT handle the request.
        ITestLaunchSupport support2 = mock(ITestLaunchSupport.class);
        when(support2.isLaunchSupported(any(TestType.class), any(Class.class), any(Cardinality.class))).thenReturn(false);

        IConfigurationElement configElement1 = mock(IConfigurationElement.class);
        when(configElement1.createExecutableExtension("class")).thenReturn(support1);

        IConfigurationElement configElement2 = mock(IConfigurationElement.class);
        when(configElement2.createExecutableExtension("class")).thenReturn(support2);

        try (MockedStatic<Platform> platformMock = mockStatic(Platform.class))
        {
            IExtensionRegistry registry = mock(IExtensionRegistry.class);
            when(registry.getConfigurationElementsFor(any())).thenReturn(new IConfigurationElement[] { configElement1, configElement2 });
            platformMock.when(Platform::getExtensionRegistry).thenReturn(registry);

            try (MockedStatic<LogHandler> logHandlerMock = mockStatic(LogHandler.class))
            {
                logHandlerMock.when(LogHandler::getInstance).thenReturn(mock(LogHandler.class));

                AdditionalTestLaunchShortcutProvider provider = new AdditionalTestLaunchShortcutProvider();
                ILaunchShortcut result = provider.getShorcutFor("junit5", IMember.class, Cardinality.ONE);

                assertSame(shortcut1, result);
                // support2 must NOT have been consulted for a shortcut, since
                // support1 already matched.
                verify(support2, never()).getShortcut();
            }
        }
    }

    @Test
    public void getShorcutFor_should_query_extension_registry_under_moreunit_namespace()
    {
        try (MockedStatic<Platform> platformMock = mockStatic(Platform.class))
        {
            IExtensionRegistry registry = mock(IExtensionRegistry.class);
            when(registry.getConfigurationElementsFor(any())).thenReturn(new IConfigurationElement[0]);
            platformMock.when(Platform::getExtensionRegistry).thenReturn(registry);

            try (MockedStatic<LogHandler> logHandlerMock = mockStatic(LogHandler.class))
            {
                logHandlerMock.when(LogHandler::getInstance).thenReturn(mock(LogHandler.class));

                AdditionalTestLaunchShortcutProvider provider = new AdditionalTestLaunchShortcutProvider();
                provider.getShorcutFor("junit5", IMember.class, Cardinality.ONE);

                verify(registry).getConfigurationElementsFor(MoreUnitPlugin.PLUGIN_ID + ".testLaunchSupportAddition");
            }
        }
    }

    @Test
    public void isShortcutFor_should_return_true_when_a_support_handles_the_request() throws Exception
    {
        ILaunchShortcut shortcut = mock(ILaunchShortcut.class);
        ITestLaunchSupport support = mock(ITestLaunchSupport.class);
        when(support.isLaunchSupported(TestType.JUNIT_5, IMember.class, Cardinality.ONE)).thenReturn(true);
        when(support.getShortcut()).thenReturn(shortcut);

        IConfigurationElement configElement = mock(IConfigurationElement.class);
        when(configElement.createExecutableExtension("class")).thenReturn(support);

        try (MockedStatic<Platform> platformMock = mockStatic(Platform.class))
        {
            IExtensionRegistry registry = mock(IExtensionRegistry.class);
            when(registry.getConfigurationElementsFor(any())).thenReturn(new IConfigurationElement[] { configElement });
            platformMock.when(Platform::getExtensionRegistry).thenReturn(registry);

            try (MockedStatic<LogHandler> logHandlerMock = mockStatic(LogHandler.class))
            {
                logHandlerMock.when(LogHandler::getInstance).thenReturn(mock(LogHandler.class));

                AdditionalTestLaunchShortcutProvider provider = new AdditionalTestLaunchShortcutProvider();
                boolean result = provider.isShortcutFor("junit5", IMember.class, Cardinality.ONE);

                assertTrue(result);
            }
        }
    }
}
