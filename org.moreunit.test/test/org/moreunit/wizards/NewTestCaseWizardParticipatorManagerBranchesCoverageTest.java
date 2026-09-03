package org.moreunit.wizards;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.moreunit.extensionpoints.INewTestCaseWizardContext;
import org.moreunit.extensionpoints.INewTestCaseWizardParticipator;
import org.moreunit.log.LogHandler;
import org.moreunit.util.TestSafeRunner;

public class NewTestCaseWizardParticipatorManagerBranchesCoverageTest
{
    @BeforeEach
    public void initMocks()
    {
        MockitoAnnotations.openMocks(this);
    }

    @Mock
    private LogHandler logger;

    private NewTestCaseWizardParticipatorManager manager;

    @BeforeEach
    public void createParticipatorManager()
    {
        manager = new NewTestCaseWizardParticipatorManager(logger, new TestSafeRunner());
    }

    private IExtensionRegistry registryReturning(IConfigurationElement... elements)
    {
        final IExtensionRegistry registry = mock(IExtensionRegistry.class);
        when(registry.getConfigurationElementsFor(anyString())).thenReturn(elements);
        return registry;
    }

    @Test
    public void should_skip_configuration_element_that_cannot_be_instantiated() throws Exception
    {
        // given an extension that fails to instantiate
        final IConfigurationElement brokenElement = mock(IConfigurationElement.class);
        when(brokenElement.createExecutableExtension("class")).thenThrow(new CoreException(new Status(IStatus.ERROR, "test.plugin", "boom")));

        final IExtensionRegistry emptyRegistry = registryReturning(brokenElement);
        try (var platform = mockStatic(Platform.class))
        {
            platform.when(Platform::getExtensionRegistry).thenReturn(emptyRegistry);

            // when
            final NewTestCaseWizardComposer composer = manager.createWizardComposer(mock(INewTestCaseWizardContext.class));

            // then no page was registered and a warning was logged
            assertTrue(composer.getExtensionPages().isEmpty());
            verify(logger).handleWarnLog(anyString());
        }
    }

    @Test
    public void should_skip_extension_of_unexpected_type() throws Exception
    {
        // given an extension that does not implement the participator interface
        final IConfigurationElement foreignElement = mock(IConfigurationElement.class);
        when(foreignElement.createExecutableExtension("class")).thenReturn(new Object());

        final IExtensionRegistry foreignRegistry = registryReturning(foreignElement);
        try (var platform = mockStatic(Platform.class))
        {
            platform.when(Platform::getExtensionRegistry).thenReturn(foreignRegistry);

            // when
            final NewTestCaseWizardComposer composer = manager.createWizardComposer(mock(INewTestCaseWizardContext.class));

            // then no page was registered and a warning was logged
            assertTrue(composer.getExtensionPages().isEmpty());
            verify(logger).handleWarnLog(anyString());
        }
    }

    @Test
    public void should_order_participators_by_namespace_identifier() throws Exception
    {
        // given two extensions declared in reverse namespace order
        final INewTestCaseWizardContext context = mock(INewTestCaseWizardContext.class);

        final INewTestCaseWizardParticipator participatorA = mock(INewTestCaseWizardParticipator.class);
        when(participatorA.getPages(context)).thenReturn(asList(new ExtensionPage("a1")));
        final IConfigurationElement elementA = mock(IConfigurationElement.class);
        when(elementA.createExecutableExtension("class")).thenReturn(participatorA);
        when(elementA.getNamespaceIdentifier()).thenReturn("a");

        final INewTestCaseWizardParticipator participatorB = mock(INewTestCaseWizardParticipator.class);
        when(participatorB.getPages(context)).thenReturn(asList(new ExtensionPage("b1")));
        final IConfigurationElement elementB = mock(IConfigurationElement.class);
        when(elementB.createExecutableExtension("class")).thenReturn(participatorB);
        when(elementB.getNamespaceIdentifier()).thenReturn("b");

        final IExtensionRegistry reversedRegistry = registryReturning(elementB, elementA);
        try (var platform = mockStatic(Platform.class))
        {
            platform.when(Platform::getExtensionRegistry).thenReturn(reversedRegistry);

            // when
            final NewTestCaseWizardComposer composer = manager.createWizardComposer(context);

            // then pages are ordered by namespace identifier
            assertEquals(asList(new ExtensionPage("a1"), new ExtensionPage("b1")), composer.getExtensionPages());
        }
    }

    @Test
    public void should_register_no_page_when_registry_has_no_participator() throws Exception
    {
        final IExtensionRegistry noParticipatorRegistry = registryReturning();
        try (var platform = mockStatic(Platform.class))
        {
            platform.when(Platform::getExtensionRegistry).thenReturn(noParticipatorRegistry);

            // when
            final NewTestCaseWizardComposer composer = manager.createWizardComposer(mock(INewTestCaseWizardContext.class));

            // then
            assertEquals(Collections.emptyList(), composer.getExtensionPages());
        }
    }

    @Test
    public void should_do_nothing_when_test_case_creation_is_aborted()
    {
        final NewTestCaseWizardContext context = null;

        assertDoesNotThrow(() -> manager.testCaseCreationAborted(context));
    }
}
