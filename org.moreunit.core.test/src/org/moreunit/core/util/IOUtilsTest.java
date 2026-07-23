package org.moreunit.core.util;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.Closeable;
import java.io.IOException;

import org.junit.jupiter.api.Test;

class IOUtilsTest {

    @Test
    void testCloseQuietly_withNullArray() {
        IOUtils.closeQuietly((Closeable[]) null);
    }

    @Test
    void testCloseQuietly_withNullElements() {
        IOUtils.closeQuietly(null, null);
    }

    @Test
    void testCloseQuietly_withValidElements() throws IOException {
        Closeable c1 = mock(Closeable.class);
        Closeable c2 = mock(Closeable.class);

        IOUtils.closeQuietly(c1, c2);

        verify(c1).close();
        verify(c2).close();
    }

    @Test
    void testCloseQuietly_withExceptionOnClose() throws IOException {
        Closeable c1 = mock(Closeable.class);
        Closeable c2 = mock(Closeable.class);
        Closeable c3 = mock(Closeable.class);

        doThrow(new IOException("test")).when(c2).close();

        IOUtils.closeQuietly(c1, c2, c3);

        verify(c1).close();
        verify(c2).close();
        verify(c3).close();
    }
}
