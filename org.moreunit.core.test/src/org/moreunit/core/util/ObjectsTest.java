package org.moreunit.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ObjectsTest {

    @Test
    void testEqual() {
        assertThat(Objects.equal(null, null)).isTrue();
        assertThat(Objects.equal("a", "a")).isTrue();
        assertThat(Objects.equal(new String("a"), new String("a"))).isTrue();

        assertThat(Objects.equal("a", null)).isFalse();
        assertThat(Objects.equal(null, "a")).isFalse();
        assertThat(Objects.equal("a", "b")).isFalse();
    }

    @Test
    void testHash() {
        assertThat(Objects.hash("a", "b")).isEqualTo(java.util.Arrays.hashCode(new Object[]{"a", "b"}));
        assertThat(Objects.hash(null, "b")).isEqualTo(java.util.Arrays.hashCode(new Object[]{null, "b"}));
    }
}
