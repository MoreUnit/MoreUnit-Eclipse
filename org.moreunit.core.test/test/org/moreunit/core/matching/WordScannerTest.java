package org.moreunit.core.matching;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class WordScannerTest
{
    @Test(expected = NullPointerException.class)
    public void should_reject_null_string() throws Exception
    {
        new WordScanner(null);
    }

    @Test
    public void should_accept_empty_string() throws Exception
    {
        // given
        WordScanner scanner = new WordScanner("");

        // then
        assertThat(scanner.hasNext()).isFalse();
        assertThat(scanner.hasNext(2)).isFalse();
        assertThat(scanner.hasPrevious()).isFalse();
        assertThat(scanner.hasPrevious(2)).isFalse();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void index_should_not_be_initialized_at_construction() throws Exception
    {
        // start index is -1
        new WordScanner("some text").current();
    }

    @Test
    public void should_navigate_into_string() throws Exception
    {
        // given
        WordScanner scanner = new WordScanner("some text");

        // then (start index is -1)
        assertThat(scanner.hasNext()).isTrue();
        assertThat(scanner.hasNext(2)).isTrue();
        assertThat(scanner.hasPrevious()).isFalse();
        assertThat(scanner.hasPrevious(2)).isFalse();
        assertThat(scanner.next()).isEqualTo('s');
        assertThat(scanner.next(2)).isEqualTo('o');

        // when
        scanner.forward();

        // then
        assertThat(scanner.hasNext()).isTrue();
        assertThat(scanner.hasNext(2)).isTrue();
        assertThat(scanner.hasPrevious()).isFalse();
        assertThat(scanner.hasPrevious(2)).isFalse();
        assertThat(scanner.current()).isEqualTo('s');
        assertThat(scanner.next()).isEqualTo('o');
        assertThat(scanner.next(2)).isEqualTo('m');

        // when
        scanner.forward(8);

        // then
        assertThat(scanner.hasNext()).isFalse();
        assertThat(scanner.hasNext(2)).isFalse();
        assertThat(scanner.hasPrevious()).isTrue();
        assertThat(scanner.hasPrevious(2)).isTrue();
        assertThat(scanner.current()).isEqualTo('t');
        assertThat(scanner.previous()).isEqualTo('x');
        assertThat(scanner.previous(2)).isEqualTo('e');

        // when
        scanner.backward(4);

        // then
        assertThat(scanner.hasNext()).isTrue();
        assertThat(scanner.hasNext(3)).isTrue();
        assertThat(scanner.hasPrevious()).isTrue();
        assertThat(scanner.hasPrevious(3)).isTrue();
        assertThat(scanner.current()).isEqualTo(' ');
        assertThat(scanner.next()).isEqualTo('t');
        assertThat(scanner.next(2)).isEqualTo('e');
        assertThat(scanner.previous()).isEqualTo('e');
        assertThat(scanner.previous(2)).isEqualTo('m');

        // when
        scanner.backward();

        // then
        assertThat(scanner.current()).isEqualTo('e');
        assertThat(scanner.next(4)).isEqualTo('x');
        assertThat(scanner.previous(3)).isEqualTo('s');
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void should_complain_when_requested_index_is_after_upper_bound__without_offset() throws Exception
    {
        new WordScanner("some text").forward(9).forward();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void should_complain_when_requested_index_is_after_upper_bound__with_offset() throws Exception
    {
        new WordScanner("some text").forward(10);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void should_complain_when_requested_index_is_before_lower_bound__without_offset() throws Exception
    {
        new WordScanner("some text").backward();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void should_complain_when_requested_index_is_before_lower_bound__with_offset() throws Exception
    {
        // start index is -1
        new WordScanner("some text").forward(3).backward(3);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void should_complain_when_requested_char_is_after_upper_bound__without_offset() throws Exception
    {
        new WordScanner("some text").forward(9).next();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void should_complain_when_requested_char_is_after_upper_bound__with_offset() throws Exception
    {
        new WordScanner("some text").next(10);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void should_complain_when_requested_char_is_before_lower_bound__without_offset() throws Exception
    {
        new WordScanner("some text").previous();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void should_complain_when_requested_char_is_before_lower_bound__with_offset() throws Exception
    {
        // start index is -1
        new WordScanner("some text").forward(3).previous(3);
    }

    @Test
    public void should_remember_word_cuts() throws Exception
    {
        // given
        WordScanner scanner = new WordScanner("some text");

        // when
        scanner.forward(3);
        // then
        assertThat(scanner.getCurrentWord()).isEqualTo("som");

        // when
        scanner.forward(4);
        // then
        assertThat(scanner.getCurrentWord()).isEqualTo("e te");

        // when
        scanner.forward(2);
        // then
        assertThat(scanner.getCurrentWord()).isEqualTo("xt");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void should_reject_invalid_word_bounds() throws Exception
    {
        // given
        WordScanner scanner = new WordScanner("some text");

        // when
        scanner.forward(7);
        // then
        assertThat(scanner.getCurrentWord()).isEqualTo("some te");

        // when
        scanner.backward(4);
        // then: exception expected
        scanner.getCurrentWord();
    }
}
