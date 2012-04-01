package org.moreunit.core.matching;

import org.junit.Test;

public abstract class NameTokenizerTestCase
{
    protected abstract NameTokenizer getTokenizer();

    @Test(expected = IllegalArgumentException.class)
    public void should_reject_null_name() throws Exception
    {
        getTokenizer().tokenize(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_reject_empty_name() throws Exception
    {
        getTokenizer().tokenize("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_reject_blank_name() throws Exception
    {
        getTokenizer().tokenize("  ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_reject_name_starting_with_space() throws Exception
    {
        getTokenizer().tokenize(" name");
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_reject_name_ending_with_space() throws Exception
    {
        getTokenizer().tokenize("name ");
    }
}
