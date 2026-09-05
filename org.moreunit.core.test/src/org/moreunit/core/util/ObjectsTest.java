package org.moreunit.core.util;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class ObjectsTest {

    @Test
    void testEqual() {
        // both null
        assertThat(Objects.equal(null, null)).isTrue();

        // one null
        assertThat(Objects.equal("test", null)).isFalse();
        assertThat(Objects.equal(null, "test")).isFalse();

        // same instance
        String s1 = "test";
        assertThat(Objects.equal(s1, s1)).isTrue();

        // different instances with same value
        String s2 = new String("test");
        assertThat(Objects.equal(s1, s2)).isTrue();

        // different instances with different values
        assertThat(Objects.equal("test1", "test2")).isFalse();
    }

    @Test
    void testHash() {
        Object o1 = new Object();
        Object o2 = new Object();

        int expectedHash = java.util.Arrays.hashCode(new Object[] { o1, o2 });
        assertThat(Objects.hash(o1, o2)).isEqualTo(expectedHash);

        expectedHash = java.util.Arrays.hashCode(new Object[] { null, o2 });
        assertThat(Objects.hash(null, o2)).isEqualTo(expectedHash);

        expectedHash = java.util.Arrays.hashCode(new Object[] {});
        assertThat(Objects.hash()).isEqualTo(expectedHash);
    }
}
