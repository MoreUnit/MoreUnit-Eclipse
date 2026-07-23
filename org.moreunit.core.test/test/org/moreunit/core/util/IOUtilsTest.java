package org.moreunit.core.util;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.moreunit.core.util.IOUtils.closeQuietly;

import java.io.Closeable;
import java.io.IOException;

import org.junit.jupiter.api.Test;

public class IOUtilsTest
{
    @Test
    public void closeQuietly_should_close_resources() throws Exception
    {
        // given
        Closeable closeable1 = mock(Closeable.class);
        Closeable closeable2 = mock(Closeable.class);

        // when
        closeQuietly(closeable1, closeable2);

        // then
        verify(closeable1, times(1)).close();
        verify(closeable2, times(1)).close();

        // no exception = success
    }

    @Test
    public void closeQuietly_should_ignore_null_entry() throws Exception
    {
        // when
        closeQuietly((Closeable) null);

        // then: no exception = success
    }

    @Test
    public void closeQuietly_should_swallow_IOExceptions() throws Exception
    {
        // given
        Closeable closeable = mock(Closeable.class);

        doThrow(new IOException()).when(closeable).close();

        // when
        closeQuietly(closeable);

        // then: no exception = success
    }

    @Test
    public void closeQuietly_should_ignore_null_array() throws Exception
    {
        // when
        closeQuietly((Closeable[]) null);

        // then: no exception = success
    }

    @Test
    public void closeQuietly_should_ignore_null_varargs_elements() throws Exception
    {
        // when
        closeQuietly(null, null);

        // then: no exception = success
    }

    @Test
    public void closeQuietly_should_continue_closing_remaining_resources_on_exception() throws Exception
    {
        // given
        Closeable c1 = mock(Closeable.class);
        Closeable c2 = mock(Closeable.class);
        Closeable c3 = mock(Closeable.class);

        doThrow(new IOException()).when(c2).close();

        // when
        closeQuietly(c1, c2, c3);

        // then
        verify(c1, times(1)).close();
        verify(c2, times(1)).close();
        verify(c3, times(1)).close();
    }
}
