package org.moreunit.util;

import static java.util.Arrays.asList;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.wizards.NewClassWizardPage;
import org.moreunit.extensionpoints.ITestLaunchSupport.Cardinality;
import org.moreunit.launch.AdditionalTestLaunchShortcutProvider;
import org.moreunit.log.LogHandler;
import org.moreunit.preferences.PreferenceConstants;
import org.moreunit.preferences.Preferences;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Version;

public class FeatureDetector
{
    private static final String GROOVY_UI_PLUGIN_ID = "org.codehaus.groovy.eclipse.ui";
    private static final String NEW_GROOVY_CLASS_WIZARD_PAGE_CLASS = "org.codehaus.groovy.eclipse.wizards.NewClassWizardPage";
    private static final Version TESTNG_MIN_VERSION_FOR_SELECTION_LAUNCH = new Version("5.14.2.10");
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
        Version actual = getTestNgPluginVersion();
        return isGreaterOrEqual(actual, expected);
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
        Bundle bundle = getBundle(TESTNG_PLUGIN_ID);
        return bundle == null ? null : bundle.getVersion();
    }

    private Bundle getBundle(String bundleId)
    {
        if(bundleContext == null)
        {
            return null;
        }

        Bundle[] bundles = bundleContext.getBundles();
        for (int i = 0; i < bundles.length; i++)
        {
            Bundle bundle = bundles[i];
            if(bundleId.equals(bundle.getSymbolicName()))
            {
                return bundle;
            }
        }
        return null;
    }

    public NewClassWizardPage createNewGroovyClassWizardPageIfPossible()
    {
        Class<NewClassWizardPage> clazz = loadClassIfPossible(GROOVY_UI_PLUGIN_ID, NEW_GROOVY_CLASS_WIZARD_PAGE_CLASS);
        if(clazz == null)
        {
            return null;
        }
        try
        {
            return clazz.newInstance();
        }
        catch (Exception e)
        {
            LogHandler.getInstance().handleWarnLog("Could not instantiate class: " + NEW_GROOVY_CLASS_WIZARD_PAGE_CLASS);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private <T> Class<T> loadClassIfPossible(String bundleId, String className)
    {
        Bundle bundle = getBundle(bundleId);
        if(bundle == null)
        {
            return null;
        }
        if(asList(Bundle.INSTALLED, Bundle.RESOLVED).contains(bundle.getState()))
        {
            try
            {
                bundle.start();
            }
            catch (BundleException e)
            {
                LogHandler.getInstance().handleWarnLog("Could not start bundle: " + bundleId);
            }
        }
        if(bundle.getState() == Bundle.ACTIVE)
        {
            try
            {
                return (Class<T>) bundle.loadClass(NEW_GROOVY_CLASS_WIZARD_PAGE_CLASS);
            }
            catch (ClassNotFoundException e)
            {
                LogHandler.getInstance().handleWarnLog("Could not load class: " + className);
            }
        }
        return null;
    }
}
