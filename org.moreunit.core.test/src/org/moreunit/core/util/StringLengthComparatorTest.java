package org.moreunit.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

class StringLengthComparatorTest {

    @Test
    void testCompare() {
        StringLengthComparator comparator = new StringLengthComparator();

        assertThat(comparator.compare("a", "bb")).isNegative();
        assertThat(comparator.compare("aaa", "bb")).isPositive();
        assertThat(comparator.compare("aa", "bb")).isZero();
    }

    @Test
    void testSort() {
        List<String> list = Arrays.asList("apple", "pie", "banana", "kiwi");
        Collections.sort(list, new StringLengthComparator());

        assertThat(list).containsExactly("pie", "kiwi", "apple", "banana");
    }
}
