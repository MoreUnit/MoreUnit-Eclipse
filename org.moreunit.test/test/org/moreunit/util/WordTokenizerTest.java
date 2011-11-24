package org.moreunit.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class WordTokenizerTest
{

    @Test
    public void testWordTokenizer()
    {
        WordTokenizer wordTokenizer = new WordTokenizer("Oa");
        assertTrue(wordTokenizer.hasMoreElements());

        wordTokenizer = new WordTokenizer("OneTwoThree");
        assertTrue(wordTokenizer.hasMoreElements());
        assertEquals("One", wordTokenizer.nextElement());
        assertTrue(wordTokenizer.hasMoreElements());
        assertEquals("Two", wordTokenizer.nextElement());
        assertTrue(wordTokenizer.hasMoreElements());
        assertEquals("Three", wordTokenizer.nextElement());
        assertFalse(wordTokenizer.hasMoreElements());

        wordTokenizer = new WordTokenizer("abc");
        assertTrue(wordTokenizer.hasMoreElements());
        assertEquals("abc", wordTokenizer.nextElement());
        assertFalse(wordTokenizer.hasMoreElements());

        wordTokenizer = new WordTokenizer("firstSecond");
        assertTrue(wordTokenizer.hasMoreElements());
        assertEquals("first", wordTokenizer.nextElement());
        assertTrue(wordTokenizer.hasMoreElements());
        assertEquals("Second", wordTokenizer.nextElement());
        assertFalse(wordTokenizer.hasMoreElements());
    }

}
