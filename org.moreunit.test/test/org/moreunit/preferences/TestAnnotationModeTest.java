package org.moreunit.preferences;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.moreunit.preferences.Preferences.MethodSearchMode;

public class TestAnnotationModeTest
{
    @Test
    public void by_name_mode_should_expose_by_name_method_search_mode()
    {
        assertSame(MethodSearchMode.BY_NAME, TestAnnotationMode.BY_NAME.getMethodSearchMode());
    }

    @Test
    public void by_call_and_by_name_mode_should_expose_combined_method_search_mode()
    {
        assertSame(MethodSearchMode.BY_CALL_AND_BY_NAME, TestAnnotationMode.BY_CALL_AND_BY_NAME.getMethodSearchMode());
    }

    @Test
    public void off_mode_should_not_expose_any_method_search_mode()
    {
        assertThrows(IllegalStateException.class, () -> TestAnnotationMode.OFF.getMethodSearchMode());
    }
}
