package org.moreunit.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class FeaturesTest {

    private static final String FEATURE_NAME = "org.moreunit.test.feature";

    @AfterEach
    void tearDown() {
        System.clearProperty(FEATURE_NAME);
    }

    @Test
    void testIsActiveTrue() {
        System.setProperty(FEATURE_NAME, "true");
        assertThat(Features.isActive(FEATURE_NAME)).isTrue();
    }

    @Test
    void testIsActiveFalse() {
        System.setProperty(FEATURE_NAME, "false");
        assertThat(Features.isActive(FEATURE_NAME)).isFalse();
    }

    @Test
    void testIsActiveDefault() {
        // Property is not set
        assertThat(Features.isActive(FEATURE_NAME)).isFalse();
    }
}
