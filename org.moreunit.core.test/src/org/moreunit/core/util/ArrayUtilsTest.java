package org.moreunit.core.util;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class ArrayUtilsTest {
    @Test
    void testArray() {
        String[] result = ArrayUtils.array("a", "b", "c");
        assertThat(result).containsExactly("a", "b", "c");
    }

    @Test
    void testArrayEmpty() {
        String[] result = ArrayUtils.array();
        assertThat(result).isEmpty();
    }
}
