package org.moreunit.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class StringLengthComparatorTest {

    private final StringLengthComparator comparator = new StringLengthComparator();

    @Test
    void testCompareShorterWithLonger() {
        assertThat(comparator.compare("a", "abc")).isNegative();
    }

    @Test
    void testCompareLongerWithShorter() {
        assertThat(comparator.compare("abc", "a")).isPositive();
    }

    @Test
    void testCompareEqualLengths() {
        assertThat(comparator.compare("abc", "def")).isZero();
    }
}
