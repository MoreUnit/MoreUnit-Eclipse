package org.moreunit.core.matching;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

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
    public void should_handle_regex_symbol_as_separator() throws Exception
    {
        assertThat(new SeparatorNameTokenizer("$").tokenize("one$two$three").getTokens()).containsExactly("one","two", "three");
        assertThat(new SeparatorNameTokenizer("*").tokenize("one*two*three").getTokens()).containsExactly("one","two", "three");
    }

    @Test
    public void should_return_token_combinations() throws Exception
    {
        // when
        TokenizationResult result = tokenizer.tokenize("name");

        // then
        assertThat(result.getCombinationsFromStart()).isEmpty();
        assertThat(result.getCombinationsFromEnd()).isEmpty();

        // when
        result = tokenizer.tokenize("name_with_several_parts");

        // then
        assertThat(result.getCombinationsFromStart()).containsExactly("name", "name_with", "name_with_several");
        assertThat(result.getCombinationsFromEnd()).containsExactly("with_several_parts", "several_parts", "parts");
    }
}
