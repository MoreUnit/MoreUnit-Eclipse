package org.moreunit.core.extension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import org.moreunit.core.extension.jump.IJumper;
import org.moreunit.core.languages.Language;
import org.moreunit.core.log.Logger;

public class LanguageExtensionManagerTest
{
    private BundleContext bundleContext;
    private Logger logger;
    private LanguageExtensionManager manager;
    private IExtensionRegistry extensionRegistry;

    @BeforeEach
    public void setUp()
    {
        bundleContext = mock(BundleContext.class);
        logger = mock(Logger.class);
        extensionRegistry = mock(IExtensionRegistry.class);
    }

    @Test
    public void extensionExistsForLanguage_should_return_true_when_language_exists()
    {
        try (MockedStatic<Platform> platformMock = Mockito.mockStatic(Platform.class))
        {
            final IConfigurationElement configElement = mock(IConfigurationElement.class);
            when(configElement.getAttribute("fileExtension")).thenReturn("java");
            when(configElement.getAttribute("name")).thenReturn("Java");
            when(configElement.getChildren("condition")).thenReturn(new IConfigurationElement[0]);
            when(configElement.getContributor()).thenReturn(mock(org.eclipse.core.runtime.IContributor.class));

            final IConfigurationElement[] elements = new IConfigurationElement[] { configElement };
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
            final IConfigurationElement configElement = mock(IConfigurationElement.class);
            when(configElement.getAttribute("fileExtension")).thenReturn("java");
            when(configElement.getAttribute("name")).thenReturn("Java");

            final IConfigurationElement conditionElement = mock(IConfigurationElement.class);
            when(conditionElement.getAttribute("type")).thenReturn("dependency");
            when(conditionElement.getAttribute("value")).thenReturn("missing.bundle");

            when(configElement.getChildren("condition")).thenReturn(new IConfigurationElement[] { conditionElement });
            when(configElement.getContributor()).thenReturn(mock(org.eclipse.core.runtime.IContributor.class));

            final IConfigurationElement[] elements = new IConfigurationElement[] { configElement };
            when(extensionRegistry.getConfigurationElementsFor(ExtensionPoints.LANGUAGES)).thenReturn(elements);

            platformMock.when(Platform::getExtensionRegistry).thenReturn(extensionRegistry);

            final Bundle[] bundles = new Bundle[0];
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
            final IConfigurationElement configElement = mock(IConfigurationElement.class);
            when(configElement.getAttribute("fileExtension")).thenReturn("java");
            when(configElement.getAttribute("name")).thenReturn("Java");

            final IConfigurationElement conditionElement = mock(IConfigurationElement.class);
            when(conditionElement.getAttribute("type")).thenReturn("dependency");
            when(conditionElement.getAttribute("value")).thenReturn("org.eclipse.jdt.core");

            when(configElement.getChildren("condition")).thenReturn(new IConfigurationElement[] { conditionElement });
            when(configElement.getContributor()).thenReturn(mock(org.eclipse.core.runtime.IContributor.class));

            final IConfigurationElement[] elements = new IConfigurationElement[] { configElement };
            when(extensionRegistry.getConfigurationElementsFor(ExtensionPoints.LANGUAGES)).thenReturn(elements);

            platformMock.when(Platform::getExtensionRegistry).thenReturn(extensionRegistry);

            final Bundle bundle = mock(Bundle.class);
            when(bundle.getSymbolicName()).thenReturn("org.eclipse.jdt.core");
            final Bundle[] bundles = new Bundle[] { bundle };
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
            final IConfigurationElement configElement = mock(IConfigurationElement.class);
            when(configElement.getAttribute("fileExtension")).thenReturn("java");
            when(configElement.getAttribute("name")).thenReturn("Java");

            final IConfigurationElement conditionElement = mock(IConfigurationElement.class);
            when(conditionElement.getAttribute("type")).thenReturn("unknown");
            when(conditionElement.getAttribute("value")).thenReturn("some.value");

            when(configElement.getChildren("condition")).thenReturn(new IConfigurationElement[] { conditionElement });
            when(configElement.getContributor()).thenReturn(mock(org.eclipse.core.runtime.IContributor.class));

            final IConfigurationElement[] elements = new IConfigurationElement[] { configElement };
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
            final IConfigurationElement configElement = mock(IConfigurationElement.class);
            when(configElement.getAttribute("fileExtension")).thenReturn("java");
            when(configElement.getAttribute("name")).thenReturn("Java");
            when(configElement.getChildren("condition")).thenReturn(null);
            when(configElement.getContributor()).thenReturn(mock(org.eclipse.core.runtime.IContributor.class));

            final IConfigurationElement[] elements = new IConfigurationElement[] { configElement };
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
            final IConfigurationElement configElement = mock(IConfigurationElement.class);
            when(configElement.getAttribute("fileExtension")).thenThrow(new RuntimeException("test error"));
            when(configElement.getContributor()).thenReturn(mock(org.eclipse.core.runtime.IContributor.class));

            final IConfigurationElement[] elements = new IConfigurationElement[] { configElement };
            when(extensionRegistry.getConfigurationElementsFor(ExtensionPoints.LANGUAGES)).thenReturn(elements);

            platformMock.when(Platform::getExtensionRegistry).thenReturn(extensionRegistry);

            manager = new LanguageExtensionManager(bundleContext, logger);

            assertFalse(manager.extensionExistsForLanguage("java"));
            verify(logger).warn(anyString());
        }
    }

    private IConfigurationElement languageExtension(String fileExtension, IConfigurationElement... jumperElements)
    {
        final IConfigurationElement configElement = mock(IConfigurationElement.class);
        when(configElement.getAttribute("fileExtension")).thenReturn(fileExtension);
        when(configElement.getAttribute("name")).thenReturn(fileExtension.toUpperCase());
        when(configElement.getChildren("condition")).thenReturn(new IConfigurationElement[0]);
        when(configElement.getChildren("jumper")).thenReturn(jumperElements);
        when(configElement.getContributor()).thenReturn(mock(org.eclipse.core.runtime.IContributor.class));
        return configElement;
    }

    private IConfigurationElement jumperElement(Object executable)
    {
        final IConfigurationElement jumperElement = mock(IConfigurationElement.class);
        when(jumperElement.getName()).thenReturn("jumper");
        try
        {
            when(jumperElement.createExecutableExtension("class")).thenReturn(executable);
        }
        catch (final CoreException e)
        {
            throw new IllegalStateException(e);
        }
        when(jumperElement.getContributor()).thenReturn(mock(org.eclipse.core.runtime.IContributor.class));
        return jumperElement;
    }

    private IConfigurationElement failingJumperElement()
    {
        final IConfigurationElement jumperElement = mock(IConfigurationElement.class);
        when(jumperElement.getName()).thenReturn("jumper");
        try
        {
            when(jumperElement.createExecutableExtension("class")).thenThrow(new CoreException(new Status(Status.ERROR, "some.bundle", "class not found")));
        }
        catch (final CoreException e)
        {
            throw new IllegalStateException(e);
        }
        when(jumperElement.getContributor()).thenReturn(mock(org.eclipse.core.runtime.IContributor.class));
        return jumperElement;
    }

    private List<IJumper> collectJumpers(LanguageExtensionManager mgr, String extension)
    {
        final List<IJumper> jumpers = new ArrayList<>();
        for (final IJumper jumper : mgr.getJumpersFor(extension))
        {
            jumpers.add(jumper);
        }
        return jumpers;
    }

    @Test
    public void getJumpersFor_should_return_jumper_defined_by_matching_extension()
    {
        try (MockedStatic<Platform> platformMock = Mockito.mockStatic(Platform.class))
        {
            final IJumper jumper = mock(IJumper.class);
            final IConfigurationElement extension = languageExtension("java", jumperElement(jumper));
            when(extensionRegistry.getConfigurationElementsFor(ExtensionPoints.LANGUAGES)).thenReturn(new IConfigurationElement[] { extension });
            platformMock.when(Platform::getExtensionRegistry).thenReturn(extensionRegistry);

            manager = new LanguageExtensionManager(bundleContext, logger);

            final List<IJumper> jumpers = collectJumpers(manager, "java");

            assertEquals(1, jumpers.size());
            assertEquals(jumper, jumpers.get(0));
        }
    }

    @Test
    public void getJumpersFor_should_return_all_jumpers_of_an_extension()
    {
        try (MockedStatic<Platform> platformMock = Mockito.mockStatic(Platform.class))
        {
            final IJumper jumper1 = mock(IJumper.class);
            final IJumper jumper2 = mock(IJumper.class);
            final IConfigurationElement extension = languageExtension("py", jumperElement(jumper1), jumperElement(jumper2));

            when(extensionRegistry.getConfigurationElementsFor(ExtensionPoints.LANGUAGES)).thenReturn(new IConfigurationElement[] { extension });
            platformMock.when(Platform::getExtensionRegistry).thenReturn(extensionRegistry);

            manager = new LanguageExtensionManager(bundleContext, logger);

            final List<IJumper> jumpers = collectJumpers(manager, "py");

            assertEquals(2, jumpers.size());
            assertEquals(jumper1, jumpers.get(0));
            assertEquals(jumper2, jumpers.get(1));
        }
    }

    @Test
    public void getJumpersFor_should_not_return_jumper_for_other_language()
    {
        try (MockedStatic<Platform> platformMock = Mockito.mockStatic(Platform.class))
        {
            final IJumper jumper = mock(IJumper.class);
            final IConfigurationElement extension = languageExtension("py", jumperElement(jumper));

            when(extensionRegistry.getConfigurationElementsFor(ExtensionPoints.LANGUAGES)).thenReturn(new IConfigurationElement[] { extension });
            platformMock.when(Platform::getExtensionRegistry).thenReturn(extensionRegistry);

            manager = new LanguageExtensionManager(bundleContext, logger);

            final List<IJumper> jumpers = collectJumpers(manager, "java");

            assertTrue(jumpers.isEmpty());
        }
    }

    @Test
    public void getJumpersFor_should_ignore_jumper_whose_class_is_not_an_IJumper()
    {
        try (MockedStatic<Platform> platformMock = Mockito.mockStatic(Platform.class))
        {
            final IJumper jumper = mock(IJumper.class);
            final IConfigurationElement notAJumper = jumperElement("not a jumper");
            final IConfigurationElement extension = languageExtension("rb", notAJumper, jumperElement(jumper));

            when(extensionRegistry.getConfigurationElementsFor(ExtensionPoints.LANGUAGES)).thenReturn(new IConfigurationElement[] { extension });
            platformMock.when(Platform::getExtensionRegistry).thenReturn(extensionRegistry);

            manager = new LanguageExtensionManager(bundleContext, logger);

            final List<IJumper> jumpers = collectJumpers(manager, "rb");

            assertEquals(1, jumpers.size());
            assertEquals(jumper, jumpers.get(0));
            verify(logger).warn(anyString());
        }
    }

    @Test
    public void getJumpersFor_should_ignore_jumper_whose_class_cannot_be_created()
    {
        try (MockedStatic<Platform> platformMock = Mockito.mockStatic(Platform.class))
        {
            final IJumper jumper = mock(IJumper.class);
            final IConfigurationElement extension = languageExtension("rb", failingJumperElement(), jumperElement(jumper));

            when(extensionRegistry.getConfigurationElementsFor(ExtensionPoints.LANGUAGES)).thenReturn(new IConfigurationElement[] { extension });
            platformMock.when(Platform::getExtensionRegistry).thenReturn(extensionRegistry);

            manager = new LanguageExtensionManager(bundleContext, logger);

            final List<IJumper> jumpers = collectJumpers(manager, "rb");

            assertEquals(1, jumpers.size());
            assertEquals(jumper, jumpers.get(0));
            verify(logger).warn(anyString());
        }
    }

    @Test
    public void getJumpersFor_should_return_no_jumper_when_extension_has_no_jumper_element()
    {
        try (MockedStatic<Platform> platformMock = Mockito.mockStatic(Platform.class))
        {
            final IConfigurationElement extension = languageExtension("go");

            when(extensionRegistry.getConfigurationElementsFor(ExtensionPoints.LANGUAGES)).thenReturn(new IConfigurationElement[] { extension });
            platformMock.when(Platform::getExtensionRegistry).thenReturn(extensionRegistry);

            manager = new LanguageExtensionManager(bundleContext, logger);

            final List<IJumper> jumpers = collectJumpers(manager, "go");

            assertTrue(jumpers.isEmpty());
        }
    }

    @Test
    public void getJumpersFor_should_return_no_jumper_when_registry_has_no_language_extension()
    {
        try (MockedStatic<Platform> platformMock = Mockito.mockStatic(Platform.class))
        {
            when(extensionRegistry.getConfigurationElementsFor(ExtensionPoints.LANGUAGES)).thenReturn(new IConfigurationElement[0]);
            platformMock.when(Platform::getExtensionRegistry).thenReturn(extensionRegistry);

            manager = new LanguageExtensionManager(bundleContext, logger);

            final List<IJumper> jumpers = collectJumpers(manager, "java");

            assertTrue(jumpers.isEmpty());
        }
    }

    @Test
    public void getJumpersFor_should_throw_exception_when_asking_next_after_last_jumper()
    {
        try (MockedStatic<Platform> platformMock = Mockito.mockStatic(Platform.class))
        {
            final IJumper jumper = mock(IJumper.class);
            final IConfigurationElement extension = languageExtension("java", jumperElement(jumper));

            when(extensionRegistry.getConfigurationElementsFor(ExtensionPoints.LANGUAGES)).thenReturn(new IConfigurationElement[] { extension });
            platformMock.when(Platform::getExtensionRegistry).thenReturn(extensionRegistry);

            manager = new LanguageExtensionManager(bundleContext, logger);

            final Iterator<IJumper> it = manager.getJumpersFor("java").iterator();

            assertEquals(jumper, it.next());
            assertFalse(it.hasNext());
            assertThrows(NoSuchElementException.class, () -> it.next());
        }
    }

    @Test
    public void getJumpersFor_iterator_should_not_support_remove()
    {
        try (MockedStatic<Platform> platformMock = Mockito.mockStatic(Platform.class))
        {
            final IJumper jumper = mock(IJumper.class);
            final IConfigurationElement extension = languageExtension("java", jumperElement(jumper));

            when(extensionRegistry.getConfigurationElementsFor(ExtensionPoints.LANGUAGES)).thenReturn(new IConfigurationElement[] { extension });
            platformMock.when(Platform::getExtensionRegistry).thenReturn(extensionRegistry);

            manager = new LanguageExtensionManager(bundleContext, logger);

            final Iterator<IJumper> it = manager.getJumpersFor("java").iterator();

            assertTrue(it.hasNext());
            assertThrows(UnsupportedOperationException.class, () -> it.remove());
        }
    }
}