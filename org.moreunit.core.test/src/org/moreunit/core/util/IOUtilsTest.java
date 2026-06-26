package org.moreunit.core.util;

import static org.mockito.Mockito.*;

import java.io.Closeable;
import java.io.IOException;

import org.junit.jupiter.api.Test;

class IOUtilsTest {

    @Test
    void testCloseQuietlyWithNullArray() {
        IOUtils.closeQuietly((Closeable[]) null);
        // Should not throw an exception
    }

    @Test
    void testCloseQuietlyWithNullElement() {
        IOUtils.closeQuietly(new Closeable[] { null });
        // Should not throw an exception
    }

    @Test
    void testCloseQuietlySuccess() throws IOException {
        Closeable mockCloseable1 = mock(Closeable.class);
        Closeable mockCloseable2 = mock(Closeable.class);

        IOUtils.closeQuietly(mockCloseable1, mockCloseable2);

        verify(mockCloseable1).close();
        verify(mockCloseable2).close();
    }

    @Test
    void testCloseQuietlyWithException() throws IOException {
        Closeable mockCloseable1 = mock(Closeable.class);
        Closeable mockCloseable2 = mock(Closeable.class);

        doThrow(new IOException("Test exception")).when(mockCloseable1).close();

        IOUtils.closeQuietly(mockCloseable1, mockCloseable2);

        verify(mockCloseable1).close();
        verify(mockCloseable2).close(); // Should still be called even if the first one throws
    }
}
