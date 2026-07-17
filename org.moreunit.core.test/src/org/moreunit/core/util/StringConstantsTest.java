package org.moreunit.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class StringConstantsTest {

    @Test
    void testConstantsMatchExpectedValues() {
        assertThat(StringConstants.DOT).isEqualTo(".");
        assertThat(StringConstants.EMPTY_STRING).isEqualTo("");
        assertThat(StringConstants.NEWLINE).isEqualTo(System.getProperty("line.separator"));
        assertThat(StringConstants.SLASH).isEqualTo("/");
        assertThat(StringConstants.WILDCARD).isEqualTo("*");
    }
}
