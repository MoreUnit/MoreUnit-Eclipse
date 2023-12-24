package org.moreunit.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class WordTokenizerTest
{

    @Test
    public void should_split_token_into_words_split_by_upper_case_chars()
    {
        WordTokenizer wordTokenizer = new WordTokenizer("Oa");
        assertThat(wordTokenizer.hasMoreElements()).isTrue();

        wordTokenizer = new WordTokenizer("OneTwoThree");
        assertThat(wordTokenizer.hasMoreElements()).isTrue();
        assertThat(wordTokenizer.nextElement()).isEqualTo("One");
        assertThat(wordTokenizer.hasMoreElements()).isTrue();
        assertThat(wordTokenizer.nextElement()).isEqualTo("Two");
        assertThat(wordTokenizer.hasMoreElements()).isTrue();
        assertThat(wordTokenizer.nextElement()).isEqualTo("Three");
        assertThat(wordTokenizer.hasMoreElements()).isFalse();

        wordTokenizer = new WordTokenizer("abc");
        assertThat(wordTokenizer.hasMoreElements()).isTrue();
        assertThat(wordTokenizer.nextElement()).isEqualTo("abc");
        assertThat(wordTokenizer.hasMoreElements()).isFalse();

        wordTokenizer = new WordTokenizer("firstSecond");
        assertThat(wordTokenizer.hasMoreElements()).isTrue();
        assertThat(wordTokenizer.nextElement()).isEqualTo("first");
        assertThat(wordTokenizer.hasMoreElements()).isTrue();
        assertThat(wordTokenizer.nextElement()).isEqualTo("Second");
        assertThat(wordTokenizer.hasMoreElements()).isFalse();
    }

}
