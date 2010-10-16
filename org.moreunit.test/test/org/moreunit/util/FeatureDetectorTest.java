package org.moreunit.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.osgi.framework.Version;

public class FeatureDetectorTest
{

    @Test
    public void testIsGreaterOrEqual()
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
