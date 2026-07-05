package org.moreunit.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class FeaturesTest {

    private static final String TEST_FEATURE = "org.moreunit.testFeature";

    @AfterEach
    void tearDown() {
        System.clearProperty(TEST_FEATURE);
    }

    @Test
    void testIsActiveDefault() {
        assertThat(Features.isActive("non.existing.feature")).isFalse();
    }

    @Test
    void testIsActiveTrue() {
        System.setProperty(TEST_FEATURE, "true");
        assertThat(Features.isActive(TEST_FEATURE)).isTrue();
    }

    @Test
    void testIsActiveFalse() {
        System.setProperty(TEST_FEATURE, "false");
        assertThat(Features.isActive(TEST_FEATURE)).isFalse();
    }

    @Test
    void testIsActiveInvalid() {
        System.setProperty(TEST_FEATURE, "invalid");
        assertThat(Features.isActive(TEST_FEATURE)).isFalse();
    }
}
