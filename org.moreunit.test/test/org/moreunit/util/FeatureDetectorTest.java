package org.moreunit.util;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.osgi.framework.Version;

public class FeatureDetectorTest
{

    @Test
    public void isGreaterOrEqual()
    {
        FeatureDetector featureDetector = new FeatureDetector(null, null);
        assertThat(featureDetector.isGreaterOrEqual(null, new Version("5.14.2.8"))).isFalse();
        assertThat(featureDetector.isGreaterOrEqual(new Version(0, 0, 0), new Version("5.14.2.8"))).isFalse();
        assertThat(featureDetector.isGreaterOrEqual(new Version("5.14.1.3"), new Version("5.14.2.8"))).isFalse();
        assertThat(featureDetector.isGreaterOrEqual(new Version("5.14.2"), new Version("5.14.2.8"))).isFalse();
        assertThat(featureDetector.isGreaterOrEqual(new Version("5.14.2.7"), new Version("5.14.2.8"))).isFalse();
        assertThat(featureDetector.isGreaterOrEqual(new Version("5.14.2.8"), new Version("5.14.2.8"))).isTrue();
        assertThat(featureDetector.isGreaterOrEqual(new Version("5.14.2.9"), new Version("5.14.2.8"))).isTrue();
        assertThat(featureDetector.isGreaterOrEqual(new Version("5.14.3"), new Version("5.14.2.8"))).isTrue();
    }

}
