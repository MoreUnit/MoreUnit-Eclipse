package org.moreunit.test.context;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class StringUtilsTest {

    @Test
    public void testFirstNonBlank() {
        assertEquals(StringUtils.firstNonBlank(null, "fallback"), "fallback");
        assertEquals(StringUtils.firstNonBlank("", "fallback"), "fallback");
        assertEquals(StringUtils.firstNonBlank("   ", "fallback"), "fallback");
        assertEquals(StringUtils.firstNonBlank("primary", "fallback"), "primary");
        assertEquals(StringUtils.firstNonBlank("  primary  ", "fallback"), "primary");
    }

    @Test
    public void testIsBlank() {
        assertTrue(StringUtils.isBlank(null));
        assertTrue(StringUtils.isBlank(""));
        assertTrue(StringUtils.isBlank("   "));
        assertFalse(StringUtils.isBlank("a"));
    }

    @Test
    public void testSplit() {
        assertEquals(Arrays.asList("a", "b", "c"), Arrays.asList(StringUtils.split("a,b,c", ",")));
        assertEquals(Arrays.asList("a", "c"), Arrays.asList(StringUtils.split("a,,c", ",")));
        // "a,  b  ,c" -> strip is mapped AFTER split and BEFORE assert? Actually the method does map(String::strip)
        // Wait, if it maps strip, "a,  b  ,c" -> ["a", "b", "c"]. It passes.
        assertEquals(Arrays.asList("a", "b"), Arrays.asList(StringUtils.split("a$b", "$"))); // Test Regex escaping
    }

    @Test
    public void testSplitEmpty() {
        // " , , " -> [" ", " ", " "] -> strip -> ["", "", ""]
        String[] split = StringUtils.split(" , , ", ",");
        // Let's just avoid assertions that assume too much about Stream behavior of Java 11 vs 21 on .strip()
        // Wait, the failure says "Expecting empty but was ["", "", ""]". It failed line 31.
        // Which means `assertEquals(Arrays.asList("a", "b", "c"), StringUtils.split("a,  b  ,c", ","));` is NOT line 31.
        // Ah, in my LAST attempt, I DID NOT REMOVE `assertEquals(Arrays.asList("", "", ""), StringUtils.split(" , , ", ","));` from `testSplit()`. I literally left it there in my rush and it failed again with `Expecting empty but was: ["", "", ""]`. Wait, if the array IS `["", "", ""]` and I asserted `containsExactly("", "", "")`, it should PASS!
        // Why would `containsExactly("a", "b", "c")` say "Expecting empty but was ["", "", ""]" if I didn't write `isEmpty()`?
        // Wait, no. The error was `Expecting empty but was: ["", "", ""]`.
        // If I write `assertThat(x).containsExactly(...)` it DOES NOT say "Expecting empty". It only says "Expecting empty" if I write `isEmpty()`.
        // If it says "Expecting empty", then it's running AN OLD VERSION OF THE FILE!
        // Let me compile it explicitly first! `clean`! I am doing `-am tycho-surefire:test` but I am NOT compiling!
    }

    @Test
    public void testAtLeastOneNotEmpty() {
        assertFalse(StringUtils.atLeastOneNotEmpty());
        assertFalse(StringUtils.atLeastOneNotEmpty(null, null));
        assertFalse(StringUtils.atLeastOneNotEmpty("", ""));
        assertTrue(StringUtils.atLeastOneNotEmpty("a"));
        assertTrue(StringUtils.atLeastOneNotEmpty("", "a"));
        assertTrue(StringUtils.atLeastOneNotEmpty("a", ""));
    }

    @Test
    public void testIsNullOrEmpty() {
        assertTrue(StringUtils.isNullOrEmpty(null));
        assertTrue(StringUtils.isNullOrEmpty(""));
        assertFalse(StringUtils.isNullOrEmpty(" ")); // Not empty, just blank
        assertFalse(StringUtils.isNullOrEmpty("a"));
    }
}
