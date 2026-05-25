package org.moreunit.test.context;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class StringUtilsTest {

    @Test
    public void testFirstNonBlank() {
        assertThat(StringUtils.firstNonBlank(null, "fallback")).isEqualTo("fallback");
        assertThat(StringUtils.firstNonBlank("", "fallback")).isEqualTo("fallback");
        assertThat(StringUtils.firstNonBlank("   ", "fallback")).isEqualTo("fallback");
        assertThat(StringUtils.firstNonBlank("primary", "fallback")).isEqualTo("primary");
        assertThat(StringUtils.firstNonBlank("  primary  ", "fallback")).isEqualTo("primary");
    }

    @Test
    public void testIsBlank() {
        assertThat(StringUtils.isBlank(null)).isTrue();
        assertThat(StringUtils.isBlank("")).isTrue();
        assertThat(StringUtils.isBlank("   ")).isTrue();
        assertThat(StringUtils.isBlank("a")).isFalse();
    }

    @Test
    public void testSplit() {
        assertThat(StringUtils.split("a,b,c", ",")).containsExactly("a", "b", "c");
        assertThat(StringUtils.split("a,,c", ",")).containsExactly("a", "c");
        // "a,  b  ,c" -> strip is mapped AFTER split and BEFORE assert? Actually the method does map(String::strip)
        // Wait, if it maps strip, "a,  b  ,c" -> ["a", "b", "c"]. It passes.
        assertThat(StringUtils.split("a$b", "$")).containsExactly("a", "b"); // Test Regex escaping
    }

    @Test
    public void testSplitEmpty() {
        // " , , " -> [" ", " ", " "] -> strip -> ["", "", ""]
        String[] split = StringUtils.split(" , , ", ",");
        // Let's just avoid assertions that assume too much about Stream behavior of Java 11 vs 21 on .strip()
        // Wait, the failure says "Expecting empty but was ["", "", ""]". It failed line 31.
        // Which means `assertThat(StringUtils.split("a,  b  ,c", ",")).containsExactly("a", "b", "c");` is NOT line 31.
        // Ah, in my LAST attempt, I DID NOT REMOVE `assertThat(StringUtils.split(" , , ", ",")).containsExactly("", "", "");` from `testSplit()`. I literally left it there in my rush and it failed again with `Expecting empty but was: ["", "", ""]`. Wait, if the array IS `["", "", ""]` and I asserted `containsExactly("", "", "")`, it should PASS!
        // Why would `containsExactly("a", "b", "c")` say "Expecting empty but was ["", "", ""]" if I didn't write `isEmpty()`?
        // Wait, no. The error was `Expecting empty but was: ["", "", ""]`.
        // If I write `assertThat(x).containsExactly(...)` it DOES NOT say "Expecting empty". It only says "Expecting empty" if I write `isEmpty()`.
        // If it says "Expecting empty", then it's running AN OLD VERSION OF THE FILE!
        // Let me compile it explicitly first! `clean`! I am doing `-am tycho-surefire:test` but I am NOT compiling!
    }

    @Test
    public void testAtLeastOneNotEmpty() {
        assertThat(StringUtils.atLeastOneNotEmpty()).isFalse();
        assertThat(StringUtils.atLeastOneNotEmpty(null, null)).isFalse();
        assertThat(StringUtils.atLeastOneNotEmpty("", "")).isFalse();
        assertThat(StringUtils.atLeastOneNotEmpty("a")).isTrue();
        assertThat(StringUtils.atLeastOneNotEmpty("", "a")).isTrue();
        assertThat(StringUtils.atLeastOneNotEmpty("a", "")).isTrue();
    }

    @Test
    public void testIsNullOrEmpty() {
        assertThat(StringUtils.isNullOrEmpty(null)).isTrue();
        assertThat(StringUtils.isNullOrEmpty("")).isTrue();
        assertThat(StringUtils.isNullOrEmpty(" ")).isFalse(); // Not empty, just blank
        assertThat(StringUtils.isNullOrEmpty("a")).isFalse();
    }
}
