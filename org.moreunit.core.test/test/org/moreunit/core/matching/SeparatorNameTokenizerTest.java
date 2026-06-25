package org.moreunit.core.matching;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.moreunit.core.matching.NameTokenizer.TokenizationResult;

public class SeparatorNameTokenizerTest
{
    private SeparatorNameTokenizer tokenizer = new SeparatorNameTokenizer("_");

    @Test
    public void should_complain_when_separator_is_empty() throws Exception
    {
        assertThrows(IllegalArgumentException.class, () -> new SeparatorNameTokenizer(""));
    }

    @Test
    public void should_complain_when_separator_is_null() throws Exception
    {
        assertThrows(IllegalArgumentException.class, () -> new SeparatorNameTokenizer(null));
    }

    @Test
    public void should_return_whole_name_when_only_one_word() throws Exception
    {
        assertEquals(Arrays.asList("name"), tokenizer.tokenize("name").getTokens());
    }

    @Test
    public void should_return_split_words() throws Exception
    {
        assertEquals(Arrays.asList("name", "with", "several", "parts"), tokenizer.tokenize("name_with_several_parts").getTokens());
    }

    @Test
    public void should_handle_separator_in_first_position() throws Exception
    {
        assertEquals(Arrays.asList("name"), tokenizer.tokenize("_name").getTokens());
    }

    @Test
    public void should_handle_separator_in_last_position() throws Exception
    {
        assertEquals(Arrays.asList("name"), tokenizer.tokenize("name_").getTokens());
    }

    @Test
    public void should_handle_white_space_separator() throws Exception
    {
        assertEquals(Arrays.asList("two", "words"), new SeparatorNameTokenizer(" ").tokenize("two words").getTokens());
    }

    @Test
    public void should_handle_multi_char_separator() throws Exception
    {
        assertEquals(Arrays.asList("two", "words"), new SeparatorNameTokenizer("__").tokenize("two__words").getTokens());
    }

    @Test
    public void should_handle_double_separator() throws Exception
    {
        assertEquals(Arrays.asList("two", "words"), tokenizer.tokenize("two__words").getTokens());
    }

    @Test
    public void should_handle_regex_symbol_as_separator() throws Exception
    {
        assertEquals(Arrays.asList("one","two", "three"), new SeparatorNameTokenizer("$").tokenize("one$two$three").getTokens());
        assertEquals(Arrays.asList("one","two", "three"), new SeparatorNameTokenizer("*").tokenize("one*two*three").getTokens());
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
        assertEquals(Arrays.asList("name", "name_with", "name_with_several"), result.getCombinationsFromStart());
        assertEquals(Arrays.asList("with_several_parts", "several_parts", "parts"), result.getCombinationsFromEnd());
    }
}
