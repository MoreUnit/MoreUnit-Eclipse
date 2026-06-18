package org.moreunit.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class MoreUnitContantsTest
{
    @Test
    public void should_have_expected_constants()
    {
        assertThat(MoreUnitContants.TEST_CASE_MARKER).isEqualTo("org.moreunit.testCase");
        assertThat(MoreUnitContants.TEST_CASE_DECORATOR).isEqualTo("org.moreunit.testdecorator");
        assertThat(MoreUnitContants.TEST_JUNIT3_METHOD_PRAEFIX).isEqualTo("test");
        assertThat(MoreUnitContants.SUFFIX_NAME).isEqualTo("Suffix");
        assertThat(MoreUnitContants.GETTER_PREFIX).isEqualTo("get");
        assertThat(MoreUnitContants.SETTER_PREFIX).isEqualTo("set");
        assertThat(MoreUnitContants.TESTNG_ANNOTATION_NAME).isEqualTo("Test");

        assertThat(MoreUnitContants.SUPPORTED_EXTENSIONS).containsExactly("java", "groovy");
        assertThat(MoreUnitContants.TEST_ANNOTATION_NAMES).containsExactly(
            "Test", "RepeatedTest", "ParameterizedTest", "TestFactory",
            "RepeatedIfExceptionsTest", "ParameterizedRepeatedIfExceptionsTest"
        );
    }
}
