package org.moreunit.core.util;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

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
        assertThat(Strings.emptyIfNull(null)).isEqualTo("");
    }

    @Test
    public void should_return_given_string_when_not_null()
    {
        assertThat(Strings.emptyIfNull("something")).isEqualTo("something");
    }

    @Test
    public void should_count_occurrences() throws Exception
    {
        assertThat(Strings.countOccurrences("blah", "*")).isEqualTo(0);
        assertThat(Strings.countOccurrences("bl_ah", "_")).isEqualTo(1);
        assertThat(Strings.countOccurrences("--bla-h-", "-")).isEqualTo(4);
        assertThat(Strings.countOccurrences("bla<>h<>", "<>")).isEqualTo(2);
    }

    @Test
    public void split_should_ignore_blank_parts() throws Exception
    {
        assertThat(Strings.split(",a4,,b,   ,cD, ", ",")).isEqualTo(new String[] { "a4", "b", "cD" });
    }

    @Test
    public void split_should_trim_parts() throws Exception
    {
        assertThat(Strings.split("  aa ; b;c  ", ";")).isEqualTo(new String[] { "aa", "b", "c" });
    }
}
