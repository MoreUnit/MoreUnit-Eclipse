package org.moreunit.core.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class StringsTest
{
    @Test
    public void should_be_empty_when_null() throws Exception
    {
        assertTrue(Strings.isEmpty(null));
    }

    @Test
    public void should_be_empty_when_empty() throws Exception
    {
        assertTrue(Strings.isEmpty(""));
    }

    @Test
    public void should_not_be_empty_when_containing_spaces_only() throws Exception
    {
        assertFalse(Strings.isEmpty("  "));
    }

    @Test
    public void should_be_blank_when_null() throws Exception
    {
        assertTrue(Strings.isBlank(null));
    }

    @Test
    public void should_be_blank_when_empty() throws Exception
    {
        assertTrue(Strings.isBlank(""));
    }

    @Test
    public void should_be_blank_when_containing_spaces_only() throws Exception
    {
        assertTrue(Strings.isBlank("  "));
    }

    @Test
    public void should_not_be_blank_when_containing_characters() throws Exception
    {
        assertFalse(Strings.isBlank(" blah "));
    }

    @Test
    public void should_return_empty_string_when_null()
    {
        assertEquals(Strings.emptyIfNull(null), "");
    }

    @Test
    public void should_return_given_string_when_not_null()
    {
        assertEquals(Strings.emptyIfNull("something"), "something");
    }

    @Test
    public void should_return_null_when_blank() throws Exception
    {
        assertEquals(Strings.nullIfBlank(null), null);
        assertEquals(Strings.nullIfBlank(""), null);
        assertEquals(Strings.nullIfBlank("   "), null);
    }

    @Test
    public void should_return_given_string_when_not_blank() throws Exception
    {
        assertEquals(Strings.nullIfBlank("blah"), "blah");
    }

    @Test
    public void should_count_occurrences() throws Exception
    {
        assertEquals(Strings.countOccurrences("blah", "*"), 0);
        assertEquals(Strings.countOccurrences("bl_ah", "_"), 1);
        assertEquals(Strings.countOccurrences("--bla-h-", "-"), 4);
        assertEquals(Strings.countOccurrences("bla<>h<>", "<>"), 2);
    }

    @Test
    public void should_return_zero_when_count_occurrences_is_called_with_empty_strings() throws Exception
    {
        assertEquals(Strings.countOccurrences("", "test"), 0);
        assertEquals(Strings.countOccurrences(null, "test"), 0);
        assertEquals(Strings.countOccurrences("test", ""), 0);
        assertEquals(Strings.countOccurrences("test", null), 0);
        assertEquals(Strings.countOccurrences(null, null), 0);
    }

    @Test
    public void split_should_ignore_blank_parts() throws Exception
    {
        assertEquals(Strings.split(",a4,,b,   ,cD, ", ","), new String[] { "a4", "b", "cD" });
    }

    @Test
    public void split_should_trim_parts() throws Exception
    {
        assertEquals(Strings.split("  aa ; b;c  ", ";"), new String[] { "aa", "b", "c" });
    }

    @Test
    public void ucFirst_should_uppercase_first_char() throws Exception
    {
        assertEquals(Strings.ucFirst("blah"), "Blah");
    }

    @Test
    public void ucFirst_should_conserve_case_of_other_chars() throws Exception
    {
        assertEquals(Strings.ucFirst("bLaH"), "BLaH");
    }

    @Test
    public void ucFirst_should_ignore_empty_string() throws Exception
    {
        assertEquals(Strings.ucFirst(null), null);
        assertEquals(Strings.ucFirst(""), "");
    }

    @Test
    public void join_should_return_empty_string_for_empty_array() throws Exception
    {
        assertEquals(Strings.join(","), "");
        assertEquals(Strings.join(",", new String[0]), "");
    }

    @Test
    public void join_should_join_given_strings_whith_given_separator() throws Exception
    {
        assertEquals(Strings.join(",", "1", "2", "3"), "1,2,3");
        assertEquals(Strings.join(" -> ", "aBc", "2", "DEf"), "aBc -> 2 -> DEf");
    }

    @Test
    public void join_should_append_to_string_builder_with_given_separator_for_collection() throws Exception
    {
        StringBuilder sb = new StringBuilder("prefix: ");
        Strings.join(sb, ",", java.util.Arrays.asList("1", "2", "3"));
        assertEquals(sb.toString(), "prefix: 1,2,3");
    }

    @Test
    public void join_should_append_to_string_builder_with_given_separator_for_array() throws Exception
    {
        StringBuilder sb = new StringBuilder("prefix: ");
        Strings.join(sb, " -> ", new String[]{"aBc", "2", "DEf"});
        assertEquals(sb.toString(), "prefix: aBc -> 2 -> DEf");
    }

    @Test
    public void emptyArray_should_return_empty_array()
    {
        assertTrue(Strings.emptyArray().length == 0);
    }

    @Test
    public void join_should_handle_collection_with_null_elements() throws Exception
    {
        assertEquals(Strings.join(",", java.util.Arrays.asList("1", null, "3")), "1,null,3");
    }

    @Test
    public void join_should_handle_array_with_null_elements() throws Exception
    {
        assertEquals(Strings.join(",", new String[]{"1", null, "3"}), "1,null,3");
    }

    @Test
    public void testCountOccurrences_withOverlappingPattern() {
        assertEquals(Strings.countOccurrences("aaaa", "aa"), 2);
    }

    @Test
    public void testCountOccurrences_withPatternLongerThanString() {
        assertEquals(Strings.countOccurrences("a", "aa"), 0);
    }

    @Test
    public void testSplitAsList() {
        assertEquals(Arrays.asList("a", "b", "c"), Strings.splitAsList("a,b,,c,", ","));
    }
}
