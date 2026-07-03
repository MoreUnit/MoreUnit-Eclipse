package org.moreunit.core.extension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import org.moreunit.core.languages.Language;
import org.moreunit.core.log.Logger;

public class LanguageExtensionManagerTest
{
    private BundleContext bundleContext;
    private Logger logger;
    private LanguageExtensionManager manager;
    private IExtensionRegistry extensionRegistry;
    private IConfigurationElement[] configElements;

    @BeforeEach
    public void setUp()
    {
        bundleContext = mock(BundleContext.class);
        logger = mock(Logger.class);
        extensionRegistry = mock(IExtensionRegistry.class);
        configElements = new IConfigurationElement[0];
    }

    @Test
    public void extensionExistsForLanguage_should_return_true_when_language_exists()
    {
        try (MockedStatic<Platform> platformMock = Mockito.mockStatic(Platform.class))
        {
            IConfigurationElement configElement = mock(IConfigurationElement.class);
            when(configElement.getAttribute("fileExtension")).thenReturn("java");
            when(configElement.getAttribute("name")).thenReturn("Java");
            when(configElement.getChildren("condition")).thenReturn(new IConfigurationElement[0]);
            when(configElement.getContributor()).thenReturn(mock(org.eclipse.core.runtime.IContributor.class));

            IConfigurationElement[] elements = new IConfigurationElement[] { configElement };
            when(extensionRegistry.getConfigurationElementsFor(ExtensionPoints.LANGUAGES)).thenReturn(elements);

            platformMock.when(Platform::getExtensionRegistry).thenReturn(extensionRegistry);

            manager = new LanguageExtensionManager(bundleContext, logger);

            assertTrue(manager.extensionExistsForLanguage("java"));
        }
    }

    @Test
    public void extensionExistsForLanguage_should_return_false_when_language_not_exists()
    {
        try (MockedStatic<Platform> platformMock = Mockito.mockStatic(Platform.class))
        {
            when(extensionRegistry.getConfigurationElementsFor(ExtensionPoints.LANGUAGES)).thenReturn(new IConfigurationElement[0]);
            platformMock.when(Platform::getExtensionRegistry).thenReturn(extensionRegistry);

            manager = new LanguageExtensionManager(bundleContext, logger);

            assertFalse(manager.extensionExistsForLanguage("nonexistent"));
        }
    }

    @Test
    public void extensionExistsForLanguage_should_return_false_when_dependency_not_met()
    {
        try (MockedStatic<Platform> platformMock = Mockito.mockStatic(Platform.class))
        {
            IConfigurationElement configElement = mock(IConfigurationElement.class);
            when(configElement.getAttribute("fileExtension")).thenReturn("java");
            when(configElement.getAttribute("name")).thenReturn("Java");

            IConfigurationElement conditionElement = mock(IConfigurationElement.class);
            when(conditionElement.getAttribute("type")).thenReturn("dependency");
            when(conditionElement.getAttribute("value")).thenReturn("missing.bundle");

            when(configElement.getChildren("condition")).thenReturn(new IConfigurationElement[] { conditionElement });
            when(configElement.getContributor()).thenReturn(mock(org.eclipse.core.runtime.IContributor.class));

            IConfigurationElement[] elements = new IConfigurationElement[] { configElement };
            when(extensionRegistry.getConfigurationElementsFor(ExtensionPoints.LANGUAGES)).thenReturn(elements);

            platformMock.when(Platform::getExtensionRegistry).thenReturn(extensionRegistry);

            Bundle[] bundles = new Bundle[0];
            when(bundleContext.getBundles()).thenReturn(bundles);

            manager = new LanguageExtensionManager(bundleContext, logger);

            assertFalse(manager.extensionExistsForLanguage("java"));
        }
    }

    @Test
    public void extensionExistsForLanguage_should_return_true_when_dependency_met()
    {
        try (MockedStatic<Platform> platformMock = Mockito.mockStatic(Platform.class))
        {
            IConfigurationElement configElement = mock(IConfigurationElement.class);
            when(configElement.getAttribute("fileExtension")).thenReturn("java");
            when(configElement.getAttribute("name")).thenReturn("Java");

            IConfigurationElement conditionElement = mock(IConfigurationElement.class);
            when(conditionElement.getAttribute("type")).thenReturn("dependency");
            when(conditionElement.getAttribute("value")).thenReturn("org.eclipse.jdt.core");

            when(configElement.getChildren("condition")).thenReturn(new IConfigurationElement[] { conditionElement });
            when(configElement.getContributor()).thenReturn(mock(org.eclipse.core.runtime.IContributor.class));

            IConfigurationElement[] elements = new IConfigurationElement[] { configElement };
            when(extensionRegistry.getConfigurationElementsFor(ExtensionPoints.LANGUAGES)).thenReturn(elements);

            platformMock.when(Platform::getExtensionRegistry).thenReturn(extensionRegistry);

            Bundle bundle = mock(Bundle.class);
            when(bundle.getSymbolicName()).thenReturn("org.eclipse.jdt.core");
            Bundle[] bundles = new Bundle[] { bundle };
            when(bundleContext.getBundles()).thenReturn(bundles);

            manager = new LanguageExtensionManager(bundleContext, logger);

            assertTrue(manager.extensionExistsForLanguage("java"));
        }
    }

    @Test
    public void extensionExistsForLanguage_should_ignore_unknown_condition_types()
    {
        try (MockedStatic<Platform> platformMock = Mockito.mockStatic(Platform.class))
        {
            IConfigurationElement configElement = mock(IConfigurationElement.class);
            when(configElement.getAttribute("fileExtension")).thenReturn("java");
            when(configElement.getAttribute("name")).thenReturn("Java");

            IConfigurationElement conditionElement = mock(IConfigurationElement.class);
            when(conditionElement.getAttribute("type")).thenReturn("unknown");
            when(conditionElement.getAttribute("value")).thenReturn("some.value");

            when(configElement.getChildren("condition")).thenReturn(new IConfigurationElement[] { conditionElement });
            when(configElement.getContributor()).thenReturn(mock(org.eclipse.core.runtime.IContributor.class));

            IConfigurationElement[] elements = new IConfigurationElement[] { configElement };
            when(extensionRegistry.getConfigurationElementsFor(ExtensionPoints.LANGUAGES)).thenReturn(elements);

            platformMock.when(Platform::getExtensionRegistry).thenReturn(extensionRegistry);

            manager = new LanguageExtensionManager(bundleContext, logger);

            assertTrue(manager.extensionExistsForLanguage("java"));
        }
    }

    @Test
    public void extensionExistsForLanguage_should_handle_null_conditions()
    {
        try (MockedStatic<Platform> platformMock = Mockito.mockStatic(Platform.class))
        {
            IConfigurationElement configElement = mock(IConfigurationElement.class);
            when(configElement.getAttribute("fileExtension")).thenReturn("java");
            when(configElement.getAttribute("name")).thenReturn("Java");
            when(configElement.getChildren("condition")).thenReturn(null);
            when(configElement.getContributor()).thenReturn(mock(org.eclipse.core.runtime.IContributor.class));

            IConfigurationElement[] elements = new IConfigurationElement[] { configElement };
            when(extensionRegistry.getConfigurationElementsFor(ExtensionPoints.LANGUAGES)).thenReturn(elements);

            platformMock.when(Platform::getExtensionRegistry).thenReturn(extensionRegistry);

            manager = new LanguageExtensionManager(bundleContext, logger);

            assertTrue(manager.extensionExistsForLanguage("java"));
        }
    }

    @Test
    public void extensionExistsForLanguage_should_log_warn_on_exception()
    {
        try (MockedStatic<Platform> platformMock = Mockito.mockStatic(Platform.class))
        {
            IConfigurationElement configElement = mock(IConfigurationElement.class);
            when(configElement.getAttribute("fileExtension")).thenThrow(new RuntimeException("test error"));
            when(configElement.getContributor()).thenReturn(mock(org.eclipse.core.runtime.IContributor.class));

            IConfigurationElement[] elements = new IConfigurationElement[] { configElement };
            when(extensionRegistry.getConfigurationElementsFor(ExtensionPoints.LANGUAGES)).thenReturn(elements);

            platformMock.when(Platform::getExtensionRegistry).thenReturn(extensionRegistry);

            manager = new LanguageExtensionManager(bundleContext, logger);

            assertFalse(manager.extensionExistsForLanguage("java"));
            verify(logger).warn(anyString());
        }
    }
}