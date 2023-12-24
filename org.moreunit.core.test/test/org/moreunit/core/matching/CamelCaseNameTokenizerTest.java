package org.moreunit.core.matching;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.moreunit.core.matching.NameTokenizer.TokenizationResult;

public class CamelCaseNameTokenizerTest extends NameTokenizerTestCase
{
    private NameTokenizer tokenizer = new CamelCaseNameTokenizer();

    @Override
    protected NameTokenizer getTokenizer()
    {
        return tokenizer;
    }

    @Test
    public void should_return_whole_name_when_only_one_word() throws Exception
    {
        assertThat(tokenizer.tokenize("name").getTokens()).containsExactly("name");
        assertThat(tokenizer.tokenize("Name").getTokens()).containsExactly("Name");
    }

    @Test
    public void should_return_split_words() throws Exception
    {
        assertThat(tokenizer.tokenize("nameWithSeveralParts").getTokens()).containsExactly("name", "With", "Several", "Parts");
        assertThat(tokenizer.tokenize("NameWithSeveralParts").getTokens()).containsExactly("Name", "With", "Several", "Parts");
    }

    @Test
    public void should_handle_numbers() throws Exception
    {
        assertThat(tokenizer.tokenize("Name123With9NumbersInIt80").getTokens()).containsExactly("Name", "123", "With", "9", "Numbers", "In", "It", "80");
    }

    @Test
    public void should_return_token_combinations() throws Exception
    {
        // when
        TokenizationResult result = tokenizer.tokenize("Name");

        // then
        assertThat(result.getCombinationsFromStart()).isEmpty();
        assertThat(result.getCombinationsFromEnd()).isEmpty();

        // when
        result = tokenizer.tokenize("nameWithSeveralParts");

        // then
        assertThat(result.getCombinationsFromStart()).containsExactly("name", "nameWith", "nameWithSeveral");
        assertThat(result.getCombinationsFromEnd()).containsExactly("WithSeveralParts", "SeveralParts", "Parts");

        // when
        result = tokenizer.tokenize("Name123WithNumbers");

        // then
        assertThat(result.getCombinationsFromStart()).containsExactly("Name", "Name123", "Name123With");
        assertThat(result.getCombinationsFromEnd()).containsExactly("123WithNumbers", "WithNumbers", "Numbers");
    }
}
