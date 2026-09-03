package org.moreunit.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class WordTokenizerTest
{

    @Test
    public void should_handle_null_and_empty_string()
    {
        WordTokenizer wordTokenizer = new WordTokenizer(null);
        assertFalse(wordTokenizer.hasMoreElements());

        wordTokenizer = new WordTokenizer("");
        assertFalse(wordTokenizer.hasMoreElements());
    }

    @Test
    public void should_handle_all_lower_case_string()
    {
        final WordTokenizer wordTokenizer = new WordTokenizer("abcdef");
        assertTrue(wordTokenizer.hasMoreElements());
        assertEquals("abcdef", wordTokenizer.nextElement());
        assertFalse(wordTokenizer.hasMoreElements());
    }

    @Test
    public void should_handle_all_upper_case_string()
    {
        final WordTokenizer wordTokenizer = new WordTokenizer("ABC");
        assertTrue(wordTokenizer.hasMoreElements());
        assertEquals("A", wordTokenizer.nextElement());
        assertTrue(wordTokenizer.hasMoreElements());
        assertEquals("B", wordTokenizer.nextElement());
        assertTrue(wordTokenizer.hasMoreElements());
        assertEquals("C", wordTokenizer.nextElement());
        assertFalse(wordTokenizer.hasMoreElements());
    }

    @Test
    public void should_split_token_into_words_split_by_upper_case_chars()
    {
        WordTokenizer wordTokenizer = new WordTokenizer("Oa");
        assertTrue(wordTokenizer.hasMoreElements());

        wordTokenizer = new WordTokenizer("OneTwoThree");
        assertTrue(wordTokenizer.hasMoreElements());
        assertEquals(wordTokenizer.nextElement(), "One");
        assertTrue(wordTokenizer.hasMoreElements());
        assertEquals(wordTokenizer.nextElement(), "Two");
        assertTrue(wordTokenizer.hasMoreElements());
        assertEquals(wordTokenizer.nextElement(), "Three");
        assertFalse(wordTokenizer.hasMoreElements());

        wordTokenizer = new WordTokenizer("abc");
        assertTrue(wordTokenizer.hasMoreElements());
        assertEquals(wordTokenizer.nextElement(), "abc");
        assertFalse(wordTokenizer.hasMoreElements());

        wordTokenizer = new WordTokenizer("firstSecond");
        assertTrue(wordTokenizer.hasMoreElements());
        assertEquals(wordTokenizer.nextElement(), "first");
        assertTrue(wordTokenizer.hasMoreElements());
        assertEquals(wordTokenizer.nextElement(), "Second");
        assertFalse(wordTokenizer.hasMoreElements());
    }

}
