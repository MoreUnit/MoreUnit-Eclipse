package org.moreunit.core.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.moreunit.core.util.Features.isActive;

import org.junit.Test;

public class FeaturesTest
{
    @Test
    public void isActive_should_detect_active_feature() throws Exception
    {
        System.setProperty("test.active.feature", "true");
        assertThat(isActive("test.active.feature")).isTrue();
    }

    @Test
    public void isActive_should_detect_inactive_feature() throws Exception
    {
        assertThat(isActive("test.inactive.feature")).isFalse();

        System.setProperty("test.inactive.feature", "false");
        assertThat(isActive("test.inactive.feature")).isFalse();
    }

    @Test
    public void isActive_should_consider_invalid_activation_value_as_false() throws Exception
    {
        System.setProperty("test.active.feature", "INVALID VALUE");
        assertThat(isActive("test.active.feature")).isFalse();
    }
}
