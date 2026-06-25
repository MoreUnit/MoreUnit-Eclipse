package org.moreunit.launch;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jface.viewers.ISelection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.moreunit.extensionpoints.ITestLaunchSupport.Cardinality;
import org.moreunit.log.LogHandler;
import org.moreunit.preferences.PreferenceConstants;

public class TestLauncherTest
{
    private AdditionalTestLaunchShortcutProvider additionalShortcutProvider;
    private TestLauncher testLauncher;

    @BeforeEach
    public void setUp()
    {
        additionalShortcutProvider = mock(AdditionalTestLaunchShortcutProvider.class);
        testLauncher = new TestLauncher(additionalShortcutProvider);
    }

    @Test
    public void launch_should_use_shortcut_from_additional_provider_when_one_is_available()
    {
        IMember testMember = mock(IMember.class);
        Collection<IMember> members = Arrays.asList(testMember);

        ILaunchShortcut additionalShortcut = mock(ILaunchShortcut.class);
        when(additionalShortcutProvider.getShorcutFor(eq(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_5), any(Class.class), any(Cardinality.class)))
            .thenReturn(additionalShortcut);

        testLauncher.launch(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_5, members, "run");

        verify(additionalShortcut).launch(any(ISelection.class), eq("run"));
    }

    @Test
    public void launch_should_query_additional_provider_with_cardinality_ONE_for_single_member()
    {
        IMember testMember = mock(IMember.class);
        Collection<IMember> members = Arrays.asList(testMember);

        // Make the additional provider resolve to a shortcut so we don't
        // fall through to the Eclipse-runtime path.
        ILaunchShortcut additionalShortcut = mock(ILaunchShortcut.class);
        when(additionalShortcutProvider.getShorcutFor(any(String.class), any(Class.class), any(Cardinality.class))).thenReturn(additionalShortcut);

        testLauncher.launch(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4, members, "run");

        ArgumentCaptor<Cardinality> cardinalityCaptor = ArgumentCaptor.forClass(Cardinality.class);
        verify(additionalShortcutProvider).getShorcutFor(eq(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4), any(Class.class), cardinalityCaptor.capture());
        assertSame(Cardinality.ONE, cardinalityCaptor.getValue());
    }

    @Test
    public void launch_should_query_additional_provider_with_cardinality_SEVERAL_for_multiple_members()
    {
        IMember testMember1 = mock(IMember.class);
        IMember testMember2 = mock(IMember.class);
        Collection<IMember> members = Arrays.asList(testMember1, testMember2);

        ILaunchShortcut additionalShortcut = mock(ILaunchShortcut.class);
        when(additionalShortcutProvider.getShorcutFor(any(String.class), any(Class.class), any(Cardinality.class))).thenReturn(additionalShortcut);

        testLauncher.launch(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_5, members, "run");

        ArgumentCaptor<Cardinality> cardinalityCaptor = ArgumentCaptor.forClass(Cardinality.class);
        verify(additionalShortcutProvider).getShorcutFor(eq(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_5), any(Class.class), cardinalityCaptor.capture());
        assertSame(Cardinality.SEVERAL, cardinalityCaptor.getValue());
    }

    @Test
    public void launch_should_pass_member_class_to_additional_provider()
    {
        IMember testMember = mock(IMember.class);
        Collection<IMember> members = Arrays.asList(testMember);

        ILaunchShortcut additionalShortcut = mock(ILaunchShortcut.class);
        when(additionalShortcutProvider.getShorcutFor(any(String.class), any(Class.class), any(Cardinality.class))).thenReturn(additionalShortcut);

        testLauncher.launch(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_5, members, "run");

        ArgumentCaptor<Class<? extends IJavaElement>> classCaptor = ArgumentCaptor.forClass(Class.class);
        verify(additionalShortcutProvider).getShorcutFor(any(String.class), classCaptor.capture(), any(Cardinality.class));
        assertSame(testMember.getClass(), classCaptor.getValue());
    }

    @Test
    public void launch_should_not_throw_when_no_shortcut_is_available()
    {
        // Mock the Platform registry so the dedicated-test-extension lookup
        // returns null (no extensions registered in the test runtime).
        IExtensionRegistry registry = mock(IExtensionRegistry.class);
        IExtensionPoint extensionPoint = mock(IExtensionPoint.class);
        when(registry.getExtensionPoint(any(), any())).thenReturn(extensionPoint);
        when(extensionPoint.getExtensions()).thenReturn(new IExtension[0]);
        when(additionalShortcutProvider.getShorcutFor(any(String.class), any(Class.class), any(Cardinality.class))).thenReturn(null);

        try (MockedStatic<Platform> platformMock = mockStatic(Platform.class);
             MockedStatic<LogHandler> logHandlerMock = mockStatic(LogHandler.class))
        {
            platformMock.when(Platform::getExtensionRegistry).thenReturn(registry);
            logHandlerMock.when(LogHandler::getInstance).thenReturn(mock(LogHandler.class));

            IMember testMember = mock(IMember.class);
            testLauncher.launch(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_5, Arrays.asList(testMember), "run");

            verify(additionalShortcutProvider, atLeastOnce()).getShorcutFor(any(String.class), any(Class.class), any(Cardinality.class));
        }
    }

    @Test
    public void launch_should_not_throw_when_test_type_is_unknown()
    {
        IExtensionRegistry registry = mock(IExtensionRegistry.class);
        IExtensionPoint extensionPoint = mock(IExtensionPoint.class);
        when(registry.getExtensionPoint(any(), any())).thenReturn(extensionPoint);
        when(extensionPoint.getExtensions()).thenReturn(new IExtension[0]);
        when(additionalShortcutProvider.getShorcutFor(any(String.class), any(Class.class), any(Cardinality.class))).thenReturn(null);

        try (MockedStatic<Platform> platformMock = mockStatic(Platform.class);
             MockedStatic<LogHandler> logHandlerMock = mockStatic(LogHandler.class))
        {
            platformMock.when(Platform::getExtensionRegistry).thenReturn(registry);
            logHandlerMock.when(LogHandler::getInstance).thenReturn(mock(LogHandler.class));

            IMember testMember = mock(IMember.class);
            testLauncher.launch("some-unknown-type", Arrays.asList(testMember), "run");

            verify(additionalShortcutProvider).getShorcutFor(eq("some-unknown-type"), any(Class.class), any(Cardinality.class));
        }
    }

    @Test
    public void launch_should_not_fall_through_to_dedicated_extension_when_additional_shortcut_is_present()
    {
        IExtensionRegistry registry = mock(IExtensionRegistry.class);
        IExtensionPoint extensionPoint = mock(IExtensionPoint.class);
        when(registry.getExtensionPoint(any(), any())).thenReturn(extensionPoint);
        when(extensionPoint.getExtensions()).thenReturn(new IExtension[0]);

        ILaunchShortcut additionalShortcut = mock(ILaunchShortcut.class);
        when(additionalShortcutProvider.getShorcutFor(any(String.class), any(Class.class), any(Cardinality.class))).thenReturn(additionalShortcut);

        try (MockedStatic<Platform> platformMock = mockStatic(Platform.class))
        {
            platformMock.when(Platform::getExtensionRegistry).thenReturn(registry);

            IMember testMember = mock(IMember.class);
            testLauncher.launch(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_5, Arrays.asList(testMember), "run");

            // The dedicated-test-extension path should not have been hit,
            // because the additional provider returned a shortcut first.
            platformMock.verify(() -> Platform.getExtensionRegistry(), never());
        }
    }

    @Test
    public void launch_with_provider_constructor_keeps_constructor_injected_provider()
    {
        assertNotNull(testLauncher);

        // Just verify the launcher was created with the injected provider.
        ILaunchShortcut additionalShortcut = mock(ILaunchShortcut.class);
        when(additionalShortcutProvider.getShorcutFor(any(String.class), any(Class.class), any(Cardinality.class))).thenReturn(additionalShortcut);

        IMember testMember = mock(IMember.class);
        testLauncher.launch(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_5, Arrays.asList(testMember), "run");

        verify(additionalShortcut).launch(any(ISelection.class), eq("run"));
    }

    @Test
    public void launch_should_use_shortcut_from_dedicated_test_extension_when_no_additional_shortcut() throws Exception
    {
        IExtensionRegistry registry = mock(IExtensionRegistry.class);
        IExtensionPoint extensionPoint = mock(IExtensionPoint.class);
        IExtension jdtExtension = mock(IExtension.class);
        when(jdtExtension.getNamespaceIdentifier()).thenReturn("org.eclipse.jdt.junit");
        IConfigurationElement configElement = mock(IConfigurationElement.class);
        ILaunchShortcut dedicatedShortcut = mock(ILaunchShortcut.class);
        when(configElement.createExecutableExtension("class")).thenReturn(dedicatedShortcut);
        when(jdtExtension.getConfigurationElements()).thenReturn(new IConfigurationElement[] { configElement });
        when(extensionPoint.getExtensions()).thenReturn(new IExtension[] { jdtExtension });
        when(registry.getExtensionPoint(any(), any())).thenReturn(extensionPoint);
        when(additionalShortcutProvider.getShorcutFor(any(String.class), any(Class.class), any(Cardinality.class))).thenReturn(null);

        try (MockedStatic<Platform> platformMock = mockStatic(Platform.class);
             MockedStatic<LogHandler> logHandlerMock = mockStatic(LogHandler.class))
        {
            platformMock.when(Platform::getExtensionRegistry).thenReturn(registry);
            logHandlerMock.when(LogHandler::getInstance).thenReturn(mock(LogHandler.class));

            IMember testMember = mock(IMember.class);
            testLauncher.launch(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4, Arrays.asList(testMember), "run");

            verify(dedicatedShortcut).launch(any(ISelection.class), eq("run"));
        }
    }
}
