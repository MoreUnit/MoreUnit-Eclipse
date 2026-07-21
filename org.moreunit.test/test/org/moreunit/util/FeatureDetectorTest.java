package org.moreunit.util;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.osgi.framework.Version;

public class FeatureDetectorTest
{

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
            FeatureDetector featureDetector = new FeatureDetector(null, null);

            // This method ultimately calls getBundle, which should return null
            // safely when bundleContext is null
            Version version = featureDetector.getTestNgPluginVersion();

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
        FeatureDetector featureDetector = new FeatureDetector(null, null);
        assertFalse(featureDetector.isGreaterOrEqual(null, new Version("5.14.2.8")));
        assertFalse(featureDetector.isGreaterOrEqual(new Version(0, 0, 0), new Version("5.14.2.8")));
        assertFalse(featureDetector.isGreaterOrEqual(new Version("5.14.1.3"), new Version("5.14.2.8")));
        assertFalse(featureDetector.isGreaterOrEqual(new Version("5.14.2"), new Version("5.14.2.8")));
        assertFalse(featureDetector.isGreaterOrEqual(new Version("5.14.2.7"), new Version("5.14.2.8")));
        assertTrue(featureDetector.isGreaterOrEqual(new Version("5.14.2.8"), new Version("5.14.2.8")));
        assertTrue(featureDetector.isGreaterOrEqual(new Version("5.14.2.9"), new Version("5.14.2.8")));
        assertTrue(featureDetector.isGreaterOrEqual(new Version("5.14.3"), new Version("5.14.2.8")));
    }

}
