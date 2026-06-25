package org.moreunit.core.matching;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
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
        assertEquals(Arrays.asList("name"), tokenizer.tokenize("name").getTokens());
        assertEquals(Arrays.asList("Name"), tokenizer.tokenize("Name").getTokens());
    }

    @Test
    public void should_return_split_words() throws Exception
    {
        assertEquals(Arrays.asList("name", "With", "Several", "Parts"), tokenizer.tokenize("nameWithSeveralParts").getTokens());
        assertEquals(Arrays.asList("Name", "With", "Several", "Parts"), tokenizer.tokenize("NameWithSeveralParts").getTokens());
    }

    @Test
    public void should_handle_numbers() throws Exception
    {
        assertEquals(Arrays.asList("Name", "123", "With", "9", "Numbers", "In", "It", "80"), tokenizer.tokenize("Name123With9NumbersInIt80").getTokens());
    }

    @Test
    public void should_return_token_combinations() throws Exception
    {
        // when
        TokenizationResult result = tokenizer.tokenize("Name");

        // then
        assertTrue(result.getCombinationsFromStart().isEmpty());
        assertTrue(result.getCombinationsFromEnd().isEmpty());

        // when
        result = tokenizer.tokenize("nameWithSeveralParts");

        // then
        assertEquals(Arrays.asList("name", "nameWith", "nameWithSeveral"), result.getCombinationsFromStart());
        assertEquals(Arrays.asList("WithSeveralParts", "SeveralParts", "Parts"), result.getCombinationsFromEnd());

        // when
        result = tokenizer.tokenize("Name123WithNumbers");

        // then
        assertEquals(Arrays.asList("Name", "Name123", "Name123With"), result.getCombinationsFromStart());
        assertEquals(Arrays.asList("123WithNumbers", "WithNumbers", "Numbers"), result.getCombinationsFromEnd());
    }
}
