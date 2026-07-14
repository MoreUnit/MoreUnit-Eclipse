package org.moreunit.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class ObjectsTest {

    @Test
    void testEqual() {
        // Same reference
        String str = "test";
        assertThat(Objects.equal(str, str)).isTrue();

        // Equal values
        assertThat(Objects.equal(new String("test"), new String("test"))).isTrue();

        // Both null
        assertThat(Objects.equal(null, null)).isTrue();
    }

    @Test
    void testNotEqual() {
        // One null
        assertThat(Objects.equal("test", null)).isFalse();
        assertThat(Objects.equal(null, "test")).isFalse();

        // Different values
        assertThat(Objects.equal("test", "test2")).isFalse();
        assertThat(Objects.equal(1, 2)).isFalse();

        // Different types
        assertThat(Objects.equal("1", 1)).isFalse();
    }

    @Test
    void testHash() {
        Object obj1 = "test";
        Object obj2 = 123;
        Object obj3 = null;

        assertThat(Objects.hash(obj1, obj2, obj3)).isEqualTo(Arrays.hashCode(new Object[] { obj1, obj2, obj3 }));

        assertThat(Objects.hash(obj1)).isEqualTo(Arrays.hashCode(new Object[] { obj1 }));

        assertThat(Objects.hash()).isEqualTo(Arrays.hashCode(new Object[0]));
    }
}
