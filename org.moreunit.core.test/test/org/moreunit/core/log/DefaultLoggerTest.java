package org.moreunit.core.log;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class DefaultLoggerTest
{
    private static final String PLUGIN_ID = "org.moreunit.core.test";
    private static final String LOG_LEVEL_PROPERTY = "org.moreunit.log.level";

    private ILog mockLog;

    @Before
    public void setUp()
    {
        mockLog = mock(ILog.class);
    }

    @After
    public void tearDown()
    {
        System.clearProperty(LOG_LEVEL_PROPERTY);
    }

    @Test
    public void default_level_is_info_when_property_not_set()
    {
        DefaultLogger logger = new DefaultLogger(mockLog, PLUGIN_ID, LOG_LEVEL_PROPERTY);

        assertThat(logger.traceEnabled()).isFalse();
        assertThat(logger.debugEnabled()).isFalse();
        assertThat(logger.infoEnabled()).isTrue();
        assertThat(logger.warnEnabled()).isTrue();
        assertThat(logger.errorEnabled()).isTrue();
    }

    @Test
    public void trace_level_enabled()
    {
        System.setProperty(LOG_LEVEL_PROPERTY, "trace");
        DefaultLogger logger = new DefaultLogger(mockLog, PLUGIN_ID, LOG_LEVEL_PROPERTY);

        assertThat(logger.traceEnabled()).isTrue();
        assertThat(logger.debugEnabled()).isTrue();

        logger.trace("trace message");
        logger.debug("debug message");

        ArgumentCaptor<IStatus> statusCaptor = ArgumentCaptor.forClass(IStatus.class);
        verify(mockLog, org.mockito.Mockito.times(2)).log(statusCaptor.capture());

        assertThat(statusCaptor.getAllValues().get(0).getMessage()).isEqualTo("[TRACE] trace message");
        assertThat(statusCaptor.getAllValues().get(0).getSeverity()).isEqualTo(IStatus.INFO);
        assertThat(statusCaptor.getAllValues().get(1).getMessage()).isEqualTo("[DEBUG] debug message");
        assertThat(statusCaptor.getAllValues().get(1).getSeverity()).isEqualTo(IStatus.INFO);
    }

    @Test
    public void warn_level_enabled()
    {
        System.setProperty(LOG_LEVEL_PROPERTY, "WARNING");
        DefaultLogger logger = new DefaultLogger(mockLog, PLUGIN_ID, LOG_LEVEL_PROPERTY);

        assertThat(logger.infoEnabled()).isFalse();
        assertThat(logger.warnEnabled()).isTrue();

        logger.info("info message");
        verify(mockLog, never()).log(any(IStatus.class));

        logger.warn("warn message");
        ArgumentCaptor<IStatus> statusCaptor = ArgumentCaptor.forClass(IStatus.class);
        verify(mockLog).log(statusCaptor.capture());

        assertThat(statusCaptor.getValue().getMessage()).isEqualTo("warn message");
        assertThat(statusCaptor.getValue().getSeverity()).isEqualTo(IStatus.WARNING);
    }

    @Test
    public void error_level_enabled_with_throwable()
    {
        System.setProperty(LOG_LEVEL_PROPERTY, "ERROR");
        DefaultLogger logger = new DefaultLogger(mockLog, PLUGIN_ID, LOG_LEVEL_PROPERTY);

        assertThat(logger.warnEnabled()).isFalse();
        assertThat(logger.errorEnabled()).isTrue();

        Throwable exception = new RuntimeException("Test exception");
        logger.error("error message", exception);

        ArgumentCaptor<IStatus> statusCaptor = ArgumentCaptor.forClass(IStatus.class);
        verify(mockLog).log(statusCaptor.capture());

        assertThat(statusCaptor.getValue().getMessage()).isEqualTo("error message");
        assertThat(statusCaptor.getValue().getSeverity()).isEqualTo(IStatus.ERROR);
        assertThat(statusCaptor.getValue().getException()).isEqualTo(exception);
    }

    @Test
    public void error_level_only_throwable()
    {
        System.setProperty(LOG_LEVEL_PROPERTY, "ERROR");
        DefaultLogger logger = new DefaultLogger(mockLog, PLUGIN_ID, LOG_LEVEL_PROPERTY);

        Throwable exception = new RuntimeException("Test exception");
        logger.error(exception);

        ArgumentCaptor<IStatus> statusCaptor = ArgumentCaptor.forClass(IStatus.class);
        verify(mockLog).log(statusCaptor.capture());

        assertThat(statusCaptor.getValue().getMessage()).contains("java.lang.RuntimeException: Test exception");
        assertThat(statusCaptor.getValue().getSeverity()).isEqualTo(IStatus.ERROR);
    }

    @Test
    public void warn_level_with_throwable()
    {
        System.setProperty(LOG_LEVEL_PROPERTY, "WARNING");
        DefaultLogger logger = new DefaultLogger(mockLog, PLUGIN_ID, LOG_LEVEL_PROPERTY);

        Throwable exception = new RuntimeException("Test exception");
        logger.warn("warn message", exception);

        ArgumentCaptor<IStatus> statusCaptor = ArgumentCaptor.forClass(IStatus.class);
        verify(mockLog).log(statusCaptor.capture());

        assertThat(statusCaptor.getValue().getMessage()).isEqualTo("warn message");
        assertThat(statusCaptor.getValue().getSeverity()).isEqualTo(IStatus.WARNING);
        assertThat(statusCaptor.getValue().getException()).isEqualTo(exception);
    }
}
