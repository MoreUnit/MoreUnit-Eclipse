package org.moreunit.core.matching;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public abstract class NameTokenizerTestCase
{
    protected abstract NameTokenizer getTokenizer();

    @Test
    public void should_reject_null_name() throws Exception
    {
        assertThrows(IllegalArgumentException.class, () -> getTokenizer().tokenize(null));
    }

    @Test
    public void should_reject_empty_name() throws Exception
    {
        assertThrows(IllegalArgumentException.class, () -> getTokenizer().tokenize(""));
    }

    @Test
    public void should_reject_blank_name() throws Exception
    {
        assertThrows(IllegalArgumentException.class, () -> getTokenizer().tokenize("  "));
    }

    @Test
    public void should_reject_name_starting_with_space() throws Exception
    {
        assertThrows(IllegalArgumentException.class, () -> getTokenizer().tokenize(" name"));
    }

    @Test
    public void should_reject_name_ending_with_space() throws Exception
    {
        assertThrows(IllegalArgumentException.class, () -> getTokenizer().tokenize("name "));
    }
}
