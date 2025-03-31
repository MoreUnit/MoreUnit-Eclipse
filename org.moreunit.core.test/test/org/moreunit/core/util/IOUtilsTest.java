package org.moreunit.core.util;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.moreunit.core.util.IOUtils.closeQuietly;

import java.io.Closeable;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

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
    @Ignore
    public void closeQuietly_should_swallow_IOExceptions() throws Exception
    {
        // given
        Closeable closeable = mock(Closeable.class);

        doThrow(new IOException()).when(closeable).close();

        // when
        closeQuietly(closeable);

        // then: no exception = success
    }
}
