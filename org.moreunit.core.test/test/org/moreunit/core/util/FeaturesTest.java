package org.moreunit.core.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.moreunit.core.util.Features.isActive;

import org.junit.jupiter.api.Test;

public class FeaturesTest
{
    @Test
    public void isActive_should_detect_active_feature() throws Exception
    {
        System.setProperty("test.active.feature", "true");
        assertTrue(isActive("test.active.feature"));
    }

    @Test
    public void isActive_should_detect_inactive_feature() throws Exception
    {
        assertFalse(isActive("test.inactive.feature"));

        System.setProperty("test.inactive.feature", "false");
        assertFalse(isActive("test.inactive.feature"));
    }

    @Test
    public void isActive_should_consider_invalid_activation_value_as_false() throws Exception
    {
        System.setProperty("test.active.feature", "INVALID VALUE");
        assertFalse(isActive("test.active.feature"));
    }
}
