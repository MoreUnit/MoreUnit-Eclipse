package org.moreunit.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class MoreUnitContantsTest
{
    @Test
    public void should_have_expected_constants()
    {
        assertEquals("org.moreunit.testCase", MoreUnitContants.TEST_CASE_MARKER);
        assertEquals("org.moreunit.testdecorator", MoreUnitContants.TEST_CASE_DECORATOR);
        assertEquals("test", MoreUnitContants.TEST_JUNIT3_METHOD_PRAEFIX);
        assertEquals("Suffix", MoreUnitContants.SUFFIX_NAME);
        assertEquals("get", MoreUnitContants.GETTER_PREFIX);
        assertEquals("set", MoreUnitContants.SETTER_PREFIX);
        assertEquals("Test", MoreUnitContants.TESTNG_ANNOTATION_NAME);

        assertEquals(2, MoreUnitContants.SUPPORTED_EXTENSIONS.size());
        assertEquals("java", MoreUnitContants.SUPPORTED_EXTENSIONS.get(0));
        assertEquals("groovy", MoreUnitContants.SUPPORTED_EXTENSIONS.get(1));

        assertEquals(6, MoreUnitContants.TEST_ANNOTATION_NAMES.size());
        assertEquals("Test", MoreUnitContants.TEST_ANNOTATION_NAMES.get(0));
        assertEquals("RepeatedTest", MoreUnitContants.TEST_ANNOTATION_NAMES.get(1));
        assertEquals("ParameterizedTest", MoreUnitContants.TEST_ANNOTATION_NAMES.get(2));
        assertEquals("TestFactory", MoreUnitContants.TEST_ANNOTATION_NAMES.get(3));
        assertEquals("RepeatedIfExceptionsTest", MoreUnitContants.TEST_ANNOTATION_NAMES.get(4));
        assertEquals("ParameterizedRepeatedIfExceptionsTest", MoreUnitContants.TEST_ANNOTATION_NAMES.get(5));
    }
}
