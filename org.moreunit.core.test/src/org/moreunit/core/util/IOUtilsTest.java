package org.moreunit.core.util;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.Closeable;
import java.io.IOException;

import org.junit.jupiter.api.Test;

class IOUtilsTest {

    @Test
    void testCloseQuietlyWithNullArray() {
        // Should not throw NPE
        IOUtils.closeQuietly((Closeable[]) null);
    }

    @Test
    void testCloseQuietlyWithEmptyArray() {
        IOUtils.closeQuietly();
    }

    @Test
    void testCloseQuietlyWithNullElements() {
        // Should not throw NPE
        IOUtils.closeQuietly(null, null);
    }

    @Test
    void testCloseQuietlySuccessfully() throws IOException {
        Closeable closeable1 = mock(Closeable.class);
        Closeable closeable2 = mock(Closeable.class);

        IOUtils.closeQuietly(closeable1, closeable2);

        verify(closeable1).close();
        verify(closeable2).close();
    }

    @Test
    void testCloseQuietlySuppressesIOException() throws IOException {
        Closeable closeable1 = mock(Closeable.class);
        Closeable closeable2 = mock(Closeable.class);

        doThrow(new IOException("test exception")).when(closeable1).close();

        // Should suppress the exception and still close the second one
        IOUtils.closeQuietly(closeable1, closeable2);

        verify(closeable1).close();
        verify(closeable2).close();
    }
}
