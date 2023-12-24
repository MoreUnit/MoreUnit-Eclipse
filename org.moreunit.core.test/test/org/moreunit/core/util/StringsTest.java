package org.moreunit.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class StringsTest
{
    @Test
    public void should_be_empty_when_null() throws Exception
    {
        assertThat(Strings.isEmpty(null)).isTrue();
    }

    @Test
    public void should_be_empty_when_empty() throws Exception
    {
        assertThat(Strings.isEmpty("")).isTrue();
    }

    @Test
    public void should_not_be_empty_when_containing_spaces_only() throws Exception
    {
        assertThat(Strings.isEmpty("  ")).isFalse();
    }

    @Test
    public void should_be_blank_when_null() throws Exception
    {
        assertThat(Strings.isBlank(null)).isTrue();
    }

    @Test
    public void should_be_blank_when_empty() throws Exception
    {
        assertThat(Strings.isBlank("")).isTrue();
    }

    @Test
    public void should_be_blank_when_containing_spaces_only() throws Exception
    {
        assertThat(Strings.isBlank("  ")).isTrue();
    }

    @Test
    public void should_not_be_blank_when_containing_characters() throws Exception
    {
        assertThat(Strings.isBlank(" blah ")).isFalse();
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
    public void should_return_null_when_blank() throws Exception
    {
        assertThat(Strings.nullIfBlank(null)).isEqualTo(null);
        assertThat(Strings.nullIfBlank("")).isEqualTo(null);
        assertThat(Strings.nullIfBlank("   ")).isEqualTo(null);
    }

    @Test
    public void should_return_given_string_when_not_blank() throws Exception
    {
        assertThat(Strings.nullIfBlank("blah")).isEqualTo("blah");
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

    @Test
    public void ucFirst_should_uppercase_first_char() throws Exception
    {
        assertThat(Strings.ucFirst("blah")).isEqualTo("Blah");
    }

    @Test
    public void ucFirst_should_conserve_case_of_other_chars() throws Exception
    {
        assertThat(Strings.ucFirst("bLaH")).isEqualTo("BLaH");
    }

    @Test
    public void ucFirst_should_ignore_empty_string() throws Exception
    {
        assertThat(Strings.ucFirst(null)).isEqualTo(null);
        assertThat(Strings.ucFirst("")).isEqualTo("");
    }

    @Test
    public void join_should_return_empty_string_for_empty_array() throws Exception
    {
        assertThat(Strings.join(",")).isEqualTo("");
        assertThat(Strings.join(",", new String[0])).isEqualTo("");
    }

    @Test
    public void join_should_join_given_strings_whith_given_separator() throws Exception
    {
        assertThat(Strings.join(",", "1", "2", "3")).isEqualTo("1,2,3");
        assertThat(Strings.join(" -> ", "aBc", "2", "DEf")).isEqualTo("aBc -> 2 -> DEf");
    }
}
