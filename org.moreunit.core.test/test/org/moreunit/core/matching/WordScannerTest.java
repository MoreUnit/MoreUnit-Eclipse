package org.moreunit.core.matching;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class WordScannerTest
{
    @Test
    public void should_reject_null_string() throws Exception
    {
        assertThrows(NullPointerException.class, () -> new WordScanner(null));
    }

    @Test
    public void should_accept_empty_string() throws Exception
    {
        // given
        WordScanner scanner = new WordScanner("");

        // then
        assertFalse(scanner.hasNext());
        assertFalse(scanner.hasNext(2));
        assertFalse(scanner.hasPrevious());
        assertFalse(scanner.hasPrevious(2));
    }

    @Test
    public void index_should_not_be_initialized_at_construction() throws Exception
    {
        // start index is -1
        assertThrows(IndexOutOfBoundsException.class, () -> new WordScanner("some text").current());
    }

    @Test
    public void should_navigate_into_string() throws Exception
    {
        // given
        WordScanner scanner = new WordScanner("some text");

        // then (start index is -1)
        assertTrue(scanner.hasNext());
        assertTrue(scanner.hasNext(2));
        assertFalse(scanner.hasPrevious());
        assertFalse(scanner.hasPrevious(2));
        assertEquals(scanner.next(), 's');
        assertEquals(scanner.next(2), 'o');

        // when
        scanner.forward();

        // then
        assertTrue(scanner.hasNext());
        assertTrue(scanner.hasNext(2));
        assertFalse(scanner.hasPrevious());
        assertFalse(scanner.hasPrevious(2));
        assertEquals(scanner.current(), 's');
        assertEquals(scanner.next(), 'o');
        assertEquals(scanner.next(2), 'm');

        // when
        scanner.forward(8);

        // then
        assertFalse(scanner.hasNext());
        assertFalse(scanner.hasNext(2));
        assertTrue(scanner.hasPrevious());
        assertTrue(scanner.hasPrevious(2));
        assertEquals(scanner.current(), 't');
        assertEquals(scanner.previous(), 'x');
        assertEquals(scanner.previous(2), 'e');

        // when
        scanner.backward(4);

        // then
        assertTrue(scanner.hasNext());
        assertTrue(scanner.hasNext(3));
        assertTrue(scanner.hasPrevious());
        assertTrue(scanner.hasPrevious(3));
        assertEquals(scanner.current(), ' ');
        assertEquals(scanner.next(), 't');
        assertEquals(scanner.next(2), 'e');
        assertEquals(scanner.previous(), 'e');
        assertEquals(scanner.previous(2), 'm');

        // when
        scanner.backward();

        // then
        assertEquals(scanner.current(), 'e');
        assertEquals(scanner.next(4), 'x');
        assertEquals(scanner.previous(3), 's');
    }

    @Test
    public void should_complain_when_requested_index_is_after_upper_bound__without_offset() throws Exception
    {
        assertThrows(IndexOutOfBoundsException.class, () -> new WordScanner("some text").forward(9).forward());
    }

    @Test
    public void should_complain_when_requested_index_is_after_upper_bound__with_offset() throws Exception
    {
        assertThrows(IndexOutOfBoundsException.class, () -> new WordScanner("some text").forward(10));
    }

    @Test
    public void should_complain_when_requested_index_is_before_lower_bound__without_offset() throws Exception
    {
        assertThrows(IndexOutOfBoundsException.class, () -> new WordScanner("some text").backward());
    }

    @Test
    public void should_complain_when_requested_index_is_before_lower_bound__with_offset() throws Exception
    {
        // start index is -1
        assertThrows(IndexOutOfBoundsException.class, () -> new WordScanner("some text").forward(3).backward(3));
    }

    @Test
    public void should_complain_when_requested_char_is_after_upper_bound__without_offset() throws Exception
    {
        assertThrows(IndexOutOfBoundsException.class, () -> new WordScanner("some text").forward(9).next());
    }

    @Test
    public void should_complain_when_requested_char_is_after_upper_bound__with_offset() throws Exception
    {
        assertThrows(IndexOutOfBoundsException.class, () -> new WordScanner("some text").next(10));
    }

    @Test
    public void should_complain_when_requested_char_is_before_lower_bound__without_offset() throws Exception
    {
        assertThrows(IndexOutOfBoundsException.class, () -> new WordScanner("some text").previous());
    }

    @Test
    public void should_complain_when_requested_char_is_before_lower_bound__with_offset() throws Exception
    {
        // start index is -1
        assertThrows(IndexOutOfBoundsException.class, () -> new WordScanner("some text").forward(3).previous(3));
    }

    @Test
    public void should_remember_word_cuts() throws Exception
    {
        // given
        WordScanner scanner = new WordScanner("some text");

        // when
        scanner.forward(3);
        // then
        assertEquals(scanner.getCurrentWord(), "som");

        // when
        scanner.forward(4);
        // then
        assertEquals(scanner.getCurrentWord(), "e te");

        // when
        scanner.forward(2);
        // then
        assertEquals(scanner.getCurrentWord(), "xt");
    }

    @Test
    public void should_reject_invalid_word_bounds() throws Exception
    {
        // given
        WordScanner scanner = new WordScanner("some text");

        // when
        scanner.forward(7);
        // then
        assertEquals(scanner.getCurrentWord(), "some te");

        // when
        scanner.backward(4);
        // then: exception expected
        assertThrows(IndexOutOfBoundsException.class, () -> scanner.getCurrentWord());
    }
}
