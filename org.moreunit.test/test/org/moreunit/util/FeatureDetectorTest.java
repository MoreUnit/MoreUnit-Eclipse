package org.moreunit.util;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.jdt.core.IJavaProject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.moreunit.launch.AdditionalTestLaunchShortcutProvider;
import org.moreunit.preferences.PreferenceConstants;
import org.moreunit.preferences.Preferences;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Version;

public class FeatureDetectorTest
{

    private FeatureDetector detectorWithBundles(Bundle... bundles)
    {
        final BundleContext bundleContext = mock(BundleContext.class);
        when(bundleContext.getBundles()).thenReturn(bundles);
        FeatureDetector.setBundleContext(bundleContext);
        return new FeatureDetector(mock(Preferences.class), mock(AdditionalTestLaunchShortcutProvider.class));
    }

    private Bundle bundle(String symbolicName, String version, int state)
    {
        final Bundle bundle = mock(Bundle.class);
        when(bundle.getSymbolicName()).thenReturn(symbolicName);
        when(bundle.getVersion()).thenReturn(new Version(version));
        when(bundle.getState()).thenReturn(state);
        return bundle;
    }

    @AfterEach
    public void resetBundleContext()
    {
        FeatureDetector.setBundleContext(null);
    }

    @Test
    public void getTestNgPluginVersion_should_handle_null_bundle_context()
    {
        // We do not know the previous bundleContext, but since the test plugin
        // doesn't expose a getter, and test environments usually initialize this,
        // setting it to null for testing should ideally be restored.
        // However, there is no getter to save it. But we can just set it to null.
        FeatureDetector.setBundleContext(null);
        try
        {
            final FeatureDetector featureDetector = new FeatureDetector(null, null);

            // This method ultimately calls getBundle, which should return null
            // safely when bundleContext is null
            final Version version = featureDetector.getTestNgPluginVersion();

            assertNull(version);
        }
        finally
        {
            // Note: Since there's no getBundleContext(), we can't easily restore the original.
            // But we must clean up to prevent flaky tests, though in this case
            // bundle context is already null in isolated tests.
        }
    }

    @Test
    public void isGreaterOrEqual()
    {
        final FeatureDetector featureDetector = new FeatureDetector(null, null);
        assertFalse(featureDetector.isGreaterOrEqual(null, new Version("5.14.2.8")));
        assertFalse(featureDetector.isGreaterOrEqual(new Version(0, 0, 0), new Version("5.14.2.8")));
        assertFalse(featureDetector.isGreaterOrEqual(new Version("5.14.1.3"), new Version("5.14.2.8")));
        assertFalse(featureDetector.isGreaterOrEqual(new Version("5.14.2"), new Version("5.14.2.8")));
        assertFalse(featureDetector.isGreaterOrEqual(new Version("5.14.2.7"), new Version("5.14.2.8")));
        assertTrue(featureDetector.isGreaterOrEqual(new Version("5.14.2.8"), new Version("5.14.2.8")));
        assertTrue(featureDetector.isGreaterOrEqual(new Version("5.14.2.9"), new Version("5.14.2.8")));
        assertTrue(featureDetector.isGreaterOrEqual(new Version("5.14.3"), new Version("5.14.2.8")));
    }

    @Test
    public void getTestNgPluginVersion_should_return_null_when_bundle_is_not_present()
    {
        final FeatureDetector featureDetector = detectorWithBundles(bundle("org.eclipse.ui", "1.0.0", Bundle.ACTIVE));

        assertNull(featureDetector.getTestNgPluginVersion());
    }

    @Test
    public void getTestNgPluginVersion_should_return_version_when_bundle_is_present()
    {
        final Bundle testNgBundle = bundle("org.testng.eclipse", "5.14.2.8", Bundle.ACTIVE);
        final FeatureDetector featureDetector = detectorWithBundles(bundle("org.eclipse.ui", "1.0.0", Bundle.ACTIVE), testNgBundle);

        assertEquals(new Version("5.14.2.8"), featureDetector.getTestNgPluginVersion());
    }

    @Test
    public void isTestSelectionRunSupported_should_return_true_when_project_does_not_use_testng()
    {
        FeatureDetector featureDetector = detectorWithBundles();
        final Preferences preferences = mock(Preferences.class);
        when(preferences.getTestType(any(IJavaProject.class))).thenReturn(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4);
        featureDetector = new FeatureDetector(preferences, mock(AdditionalTestLaunchShortcutProvider.class));

        assertTrue(featureDetector.isTestSelectionRunSupported(mock(IJavaProject.class)));
    }

    @Test
    public void isTestSelectionRunSupported_should_return_true_when_testng_plugin_version_is_sufficient()
    {
        FeatureDetector featureDetector = detectorWithBundles(bundle("org.testng.eclipse", "5.14.2.10", Bundle.ACTIVE));
        final Preferences preferences = mock(Preferences.class);
        when(preferences.getTestType(any(IJavaProject.class))).thenReturn(PreferenceConstants.TEST_TYPE_VALUE_TESTNG);
        featureDetector = new FeatureDetector(preferences, mock(AdditionalTestLaunchShortcutProvider.class));

        assertTrue(featureDetector.isTestSelectionRunSupported(mock(IJavaProject.class)));
    }

    @Test
    public void isTestSelectionRunSupported_should_return_false_when_testng_plugin_version_is_too_old_and_no_additional_shortcut_exists()
    {
        // note: OSGi Version qualifiers are compared as strings, so use an
        // unambiguously lower version here
        FeatureDetector featureDetector = detectorWithBundles(bundle("org.testng.eclipse", "5.13.0.0", Bundle.ACTIVE));
        final Preferences preferences = mock(Preferences.class);
        when(preferences.getTestType(any(IJavaProject.class))).thenReturn(PreferenceConstants.TEST_TYPE_VALUE_TESTNG);
        final AdditionalTestLaunchShortcutProvider provider = mock(AdditionalTestLaunchShortcutProvider.class);
        when(provider.isShortcutFor(Mockito.anyString(), any(), any())).thenReturn(false);
        featureDetector = new FeatureDetector(preferences, provider);

        assertFalse(featureDetector.isTestSelectionRunSupported(mock(IJavaProject.class)));
    }

    @Test
    public void isTestSelectionRunSupported_should_return_true_when_additional_shortcut_supports_testng()
    {
        FeatureDetector featureDetector = detectorWithBundles(bundle("org.testng.eclipse", "5.14.2.8", Bundle.ACTIVE));
        final Preferences preferences = mock(Preferences.class);
        when(preferences.getTestType(any(IJavaProject.class))).thenReturn(PreferenceConstants.TEST_TYPE_VALUE_TESTNG);
        final AdditionalTestLaunchShortcutProvider provider = mock(AdditionalTestLaunchShortcutProvider.class);
        when(provider.isShortcutFor(Mockito.anyString(), any(), any())).thenReturn(true);
        featureDetector = new FeatureDetector(preferences, provider);

        assertTrue(featureDetector.isTestSelectionRunSupported(mock(IJavaProject.class)));
    }

    @Test
    public void createNewGroovyClassWizardPageIfPossible_should_return_null_when_groovy_bundle_is_not_installed()
    {
        final FeatureDetector featureDetector = detectorWithBundles(bundle("org.eclipse.ui", "1.0.0", Bundle.ACTIVE));

        assertNull(featureDetector.createNewGroovyClassWizardPageIfPossible());
    }

    @Test
    public void createNewGroovyClassWizardPageIfPossible_should_return_null_when_groovy_bundle_cannot_be_started() throws BundleException
    {
        final Bundle groovyBundle = bundle("org.codehaus.groovy.eclipse.ui", "1.0.0", Bundle.INSTALLED);
        Mockito.doThrow(new BundleException("no way")).when(groovyBundle).start();
        final FeatureDetector featureDetector = detectorWithBundles(groovyBundle);

        assertNull(featureDetector.createNewGroovyClassWizardPageIfPossible());
        Mockito.verify(groovyBundle).start();
    }

    @Test
    public void createNewGroovyClassWizardPageIfPossible_should_return_null_when_groovy_class_cannot_be_loaded() throws ClassNotFoundException
    {
        final Bundle groovyBundle = bundle("org.codehaus.groovy.eclipse.ui", "1.0.0", Bundle.ACTIVE);
        when(groovyBundle.loadClass("org.codehaus.groovy.eclipse.wizards.NewClassWizardPage")).thenThrow(new ClassNotFoundException("nope"));
        final FeatureDetector featureDetector = detectorWithBundles(groovyBundle);

        assertNull(featureDetector.createNewGroovyClassWizardPageIfPossible());
    }

    @Test
    public void createNewGroovyClassWizardPageIfPossible_should_start_resolved_bundle_and_return_null_when_class_not_found() throws Exception
    {
        final Bundle groovyBundle = bundle("org.codehaus.groovy.eclipse.ui", "1.0.0", Bundle.RESOLVED);
        when(groovyBundle.loadClass("org.codehaus.groovy.eclipse.wizards.NewClassWizardPage")).thenReturn(null);
        final FeatureDetector featureDetector = detectorWithBundles(groovyBundle);

        assertNull(featureDetector.createNewGroovyClassWizardPageIfPossible());
        Mockito.verify(groovyBundle).start();
    }
}
