package org.moreunit.core.matching;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        assertEquals(asList("name"), tokenizer.tokenize("name").getTokens());
        assertEquals(asList("Name"), tokenizer.tokenize("Name").getTokens());
    }

    @Test
    public void should_return_split_words() throws Exception
    {
        assertEquals(asList("name", "With", "Several", "Parts"), tokenizer.tokenize("nameWithSeveralParts").getTokens());
        assertEquals(asList("Name", "With", "Several", "Parts"), tokenizer.tokenize("NameWithSeveralParts").getTokens());
    }

    @Test
    public void should_handle_numbers() throws Exception
    {
        assertEquals(asList("Name", "123", "With", "9", "Numbers", "In", "It", "80"), tokenizer.tokenize("Name123With9NumbersInIt80").getTokens());
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
        assertEquals(asList("name", "nameWith", "nameWithSeveral"), result.getCombinationsFromStart());
        assertEquals(asList("WithSeveralParts", "SeveralParts", "Parts"), result.getCombinationsFromEnd());

        // when
        result = tokenizer.tokenize("Name123WithNumbers");

        // then
        assertEquals(asList("Name", "Name123", "Name123With"), result.getCombinationsFromStart());
        assertEquals(asList("123WithNumbers", "WithNumbers", "Numbers"), result.getCombinationsFromEnd());
    }
}
