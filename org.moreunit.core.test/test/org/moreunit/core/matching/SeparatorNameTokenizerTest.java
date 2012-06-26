package org.moreunit.core.matching;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.moreunit.core.matching.NameTokenizer.TokenizationResult;

public class SeparatorNameTokenizerTest
{
    private SeparatorNameTokenizer tokenizer = new SeparatorNameTokenizer("_");

    @Test(expected = IllegalArgumentException.class)
    public void should_complain_when_separator_is_empty() throws Exception
    {
        new SeparatorNameTokenizer("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_complain_when_separator_is_null() throws Exception
    {
        new SeparatorNameTokenizer(null);
    }

    @Test
    public void should_return_whole_name_when_only_one_word() throws Exception
    {
        assertThat(tokenizer.tokenize("name").getTokens()).containsExactly("name");
    }

    @Test
    public void should_return_split_words() throws Exception
    {
        assertThat(tokenizer.tokenize("name_with_several_parts").getTokens()).containsExactly("name", "with", "several", "parts");
    }

    @Test
    public void should_handle_separator_in_first_position() throws Exception
    {
        assertThat(tokenizer.tokenize("_name").getTokens()).containsExactly("name");
    }

    @Test
    public void should_handle_separator_in_last_position() throws Exception
    {
        assertThat(tokenizer.tokenize("name_").getTokens()).containsExactly("name");
    }

    @Test
    public void should_handle_white_space_separator() throws Exception
    {
        assertThat(new SeparatorNameTokenizer(" ").tokenize("two words").getTokens()).containsExactly("two", "words");
    }

    @Test
    public void should_handle_multi_char_separator() throws Exception
    {
        assertThat(new SeparatorNameTokenizer("__").tokenize("two__words").getTokens()).containsExactly("two", "words");
    }

    @Test
    public void should_handle_double_separator() throws Exception
    {
        assertThat(tokenizer.tokenize("two__words").getTokens()).containsExactly("two", "words");
    }

    @Test
    public void should_return_token_combinations() throws Exception
    {
        // when
        TokenizationResult result = tokenizer.tokenize("name");

        // then
        assertTrue(result.getCombinationsFromStart().isEmpty());
        assertTrue(result.getCombinationsFromEnd().isEmpty());

        // when
        result = tokenizer.tokenize("name_with_several_parts");

        // then
        assertEquals(asList("name", "name_with", "name_with_several"), result.getCombinationsFromStart());
        assertEquals(asList("with_several_parts", "several_parts", "parts"), result.getCombinationsFromEnd());
    }
}
