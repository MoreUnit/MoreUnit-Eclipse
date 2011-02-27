package org.moreunit.mock.utils;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

public class StringsTest
{
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
}
