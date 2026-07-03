package org.moreunit.preferences;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TestTypeConstantsTest
{
    @Test
    public void should_have_junit5_test_annotation()
    {
        assertEquals("org.junit.jupiter.api.Test", TestTypeConstants.TEST_ANNOTATION.get(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_5));
    }

    @Test
    public void should_have_junit4_test_annotation()
    {
        assertEquals("org.junit.Test", TestTypeConstants.TEST_ANNOTATION.get(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4));
    }

    @Test
    public void should_have_testng_test_annotation()
    {
        assertEquals("org.testng.annotations.Test", TestTypeConstants.TEST_ANNOTATION.get(PreferenceConstants.TEST_TYPE_VALUE_TESTNG));
    }

    @Test
    public void should_have_junit5_static_import_base()
    {
        assertEquals("org.junit.jupiter.api.Assertions", TestTypeConstants.STATIC_IMPORT_BASE_CLASS.get(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_5));
    }
}
