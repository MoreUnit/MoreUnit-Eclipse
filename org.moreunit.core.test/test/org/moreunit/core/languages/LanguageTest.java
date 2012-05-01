package org.moreunit.core.languages;

import org.junit.Test;

public class LanguageTest
{
    @Test(expected = IllegalArgumentException.class)
    public void should_reject_null_extension() throws Exception
    {
        new Language(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_reject_empty_extension() throws Exception
    {
        new Language("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_reject_blank_extension() throws Exception
    {
        new Language("   ");
    }
}
