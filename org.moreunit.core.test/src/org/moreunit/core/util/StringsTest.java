package org.moreunit.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

class StringsTest {

    @Test
    void testIsBlank() {
        assertThat(Strings.isBlank(null)).isTrue();
        assertThat(Strings.isBlank("")).isTrue();
        assertThat(Strings.isBlank("   ")).isTrue();
        assertThat(Strings.isBlank(" a ")).isFalse();
    }

    @Test
    void testEmptyIfNull() {
        assertThat(Strings.emptyIfNull(null)).isEqualTo("");
        assertThat(Strings.emptyIfNull("")).isEqualTo("");
        assertThat(Strings.emptyIfNull("a")).isEqualTo("a");
    }

    @Test
    void testNullIfBlank() {
        assertThat(Strings.nullIfBlank(null)).isNull();
        assertThat(Strings.nullIfBlank("")).isNull();
        assertThat(Strings.nullIfBlank("   ")).isNull();
        assertThat(Strings.nullIfBlank(" a ")).isEqualTo(" a ");
    }

    @Test
    void testIsEmpty() {
        assertThat(Strings.isEmpty(null)).isTrue();
        assertThat(Strings.isEmpty("")).isTrue();
        assertThat(Strings.isEmpty(" ")).isFalse();
        assertThat(Strings.isEmpty("a")).isFalse();
    }

    @Test
    void testCountOccurrences() {
        assertThat(Strings.countOccurrences(null, "a")).isEqualTo(0);
        assertThat(Strings.countOccurrences("a", null)).isEqualTo(0);
        assertThat(Strings.countOccurrences("", "a")).isEqualTo(0);
        assertThat(Strings.countOccurrences("a", "")).isEqualTo(0);

        assertThat(Strings.countOccurrences("banana", "a")).isEqualTo(3);
        assertThat(Strings.countOccurrences("banana", "na")).isEqualTo(2);
        assertThat(Strings.countOccurrences("banana", "xyz")).isEqualTo(0);

        assertThat(Strings.countOccurrences("aaaa", "aa")).isEqualTo(3);
    }

    @Test
    void testSplit() {
        assertThat(Strings.split("a,b,c", ",")).containsExactly("a", "b", "c");
        assertThat(Strings.split(" a , b , c ", ",")).containsExactly("a", "b", "c");
        assertThat(Strings.split("a,,c", ",")).containsExactly("a", "c");
    }

    @Test
    void testSplitAsList() {
        assertThat(Strings.splitAsList("a,b,c", ",")).containsExactly("a", "b", "c");
        assertThat(Strings.splitAsList(" a , b , c ", ",")).containsExactly("a", "b", "c");
        assertThat(Strings.splitAsList("a,,c", ",")).containsExactly("a", "c");
    }

    @Test
    void testUcFirst() {
        assertThat(Strings.ucFirst(null)).isNull();
        assertThat(Strings.ucFirst("")).isEqualTo("");
        assertThat(Strings.ucFirst("a")).isEqualTo("A");
        assertThat(Strings.ucFirst("abc")).isEqualTo("Abc");
        assertThat(Strings.ucFirst("Abc")).isEqualTo("Abc");
    }

    @Test
    void testJoinVarargs() {
        assertThat(Strings.join(",", "a", "b", "c")).isEqualTo("a,b,c");
        assertThat(Strings.join(",", "a")).isEqualTo("a");
        assertThat(Strings.join(",")).isEqualTo("");
    }

    @Test
    void testJoinCollection() {
        assertThat(Strings.join(",", Arrays.asList("a", "b", "c"))).isEqualTo("a,b,c");
        assertThat(Strings.join(",", Arrays.asList("a"))).isEqualTo("a");
        assertThat(Strings.join(",", Collections.emptyList())).isEqualTo("");
    }

    @Test
    void testJoinStringBuilderArray() {
        StringBuilder sb = new StringBuilder();
        Strings.join(sb, ",", new String[] {"a", "b", "c"});
        assertThat(sb.toString()).isEqualTo("a,b,c");
    }

    @Test
    void testJoinStringBuilderCollection() {
        StringBuilder sb = new StringBuilder("prefix-");
        Strings.join(sb, ",", Arrays.asList("a", "b", "c"));
        assertThat(sb.toString()).isEqualTo("prefix-a,b,c");
    }

    @Test
    void testEmptyArray() {
        assertThat(Strings.emptyArray()).isEmpty();
        assertThat(Strings.emptyArray()).isSameAs(Strings.emptyArray());
    }
}
