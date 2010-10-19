package org.moreunit.util;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.moreunit.extensionpoints.ITestLaunchSupport.Cardinality;
import org.moreunit.launch.AdditionalTestLaunchShortcutProvider;
import org.moreunit.preferences.PreferenceConstants;
import org.moreunit.preferences.Preferences;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;

public class FeatureDetector
{
    private static final Version TESTNG_MIN_VERSION_FOR_SELECTION_LAUNCH = new Version("5.14.2.7");
    private static final String TESTNG_PLUGIN_ID = "org.testng.eclipse";

    private static BundleContext bundleContext;

    private final Preferences preferences;
    private final AdditionalTestLaunchShortcutProvider additionalTestLaunchShortcutProvider;

    public static void setBundleContext(BundleContext bundleContext)
    {
        FeatureDetector.bundleContext = bundleContext;
    }

    public FeatureDetector()
    {
        this(Preferences.getInstance(), AdditionalTestLaunchShortcutProvider.getInstance());
    }

    public FeatureDetector(Preferences preferences, AdditionalTestLaunchShortcutProvider provider)
    {
        this.preferences = preferences;
        this.additionalTestLaunchShortcutProvider = provider;
    }

    public boolean isTestSelectionRunSupported(IJavaProject javaProject)
    {

        /* Gro: Method isShortcurFor should be refactored to use TestType-Class instead of String value
         * as below, I will do after 2.2.0 has been released!
        TestType testType = TestType.getTestType(javaProject);
        return ! testType.equals(TestType.TESTNG)
             || testNgPluginVersionGreaterThanOrEqualTo(TESTNG_MIN_VERSION_FOR_SELECTION_LAUNCH)
             || additionalTestLaunchShortcutProvider.isShortcutFor(testType, IType.class, Cardinality.SEVERAL);
        */

        final String testNg = PreferenceConstants.TEST_TYPE_VALUE_TESTNG;
        return ! testNg.equals(preferences.getTestType(javaProject))
            || testNgPluginVersionGreaterThanOrEqualTo(TESTNG_MIN_VERSION_FOR_SELECTION_LAUNCH)
            || additionalTestLaunchShortcutProvider.isShortcutFor(testNg, IType.class, Cardinality.SEVERAL);
    }

    private boolean testNgPluginVersionGreaterThanOrEqualTo(Version expected)
    {
        // disabled for now, there is still an issue with the TestNG plugin
        return false;
        // Version actual = getTestNgPluginVersion();
        // return isGreaterOrEqual(actual, expected);
    }

    public boolean isGreaterOrEqual(Version actual, Version expected)
    {
        if(actual == null || Version.emptyVersion.equals(actual))
        {
            return false;
        }
        return expected.compareTo(actual) <= 0;
    }

    public Version getTestNgPluginVersion()
    {
        if(bundleContext == null)
        {
            return null;
        }

        Bundle[] bundles = bundleContext.getBundles();
        for (int i = 0; i < bundles.length; i++)
        {
            Bundle bundle = bundles[i];
            if(TESTNG_PLUGIN_ID.equals(bundle.getSymbolicName()))
            {
                return bundle.getVersion();
            }
        }
        return null;
    }
}
