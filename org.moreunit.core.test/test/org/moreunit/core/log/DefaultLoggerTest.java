package org.moreunit.core.log;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class DefaultLoggerTest
{
    private static final String PLUGIN_ID = "org.moreunit.core.test";
    private static final String LOG_LEVEL_PROPERTY = "org.moreunit.log.level";

    private ILog mockLog;

    @BeforeEach
    public void setUp()
    {
        mockLog = mock(ILog.class);
    }

    @AfterEach
    public void tearDown()
    {
        System.clearProperty(LOG_LEVEL_PROPERTY);
    }

    @Test
    public void default_level_is_info_when_property_not_set()
    {
        DefaultLogger logger = new DefaultLogger(mockLog, PLUGIN_ID, LOG_LEVEL_PROPERTY);

        assertFalse(logger.traceEnabled());
        assertFalse(logger.debugEnabled());
        assertTrue(logger.infoEnabled());
        assertTrue(logger.warnEnabled());
        assertTrue(logger.errorEnabled());
    }

    @Test
    public void trace_level_enabled()
    {
        System.setProperty(LOG_LEVEL_PROPERTY, "trace");
        DefaultLogger logger = new DefaultLogger(mockLog, PLUGIN_ID, LOG_LEVEL_PROPERTY);

        assertTrue(logger.traceEnabled());
        assertTrue(logger.debugEnabled());

        logger.trace("trace message");
        logger.debug("debug message");

        ArgumentCaptor<IStatus> statusCaptor = ArgumentCaptor.forClass(IStatus.class);
        verify(mockLog, org.mockito.Mockito.times(2)).log(statusCaptor.capture());

        assertEquals(statusCaptor.getAllValues().get(0).getMessage(), "[TRACE] trace message");
        assertEquals(statusCaptor.getAllValues().get(0).getSeverity(), IStatus.INFO);
        assertEquals(statusCaptor.getAllValues().get(1).getMessage(), "[DEBUG] debug message");
        assertEquals(statusCaptor.getAllValues().get(1).getSeverity(), IStatus.INFO);
    }

    @Test
    public void warn_level_enabled()
    {
        System.setProperty(LOG_LEVEL_PROPERTY, "WARNING");
        DefaultLogger logger = new DefaultLogger(mockLog, PLUGIN_ID, LOG_LEVEL_PROPERTY);

        assertFalse(logger.infoEnabled());
        assertTrue(logger.warnEnabled());

        logger.info("info message");
        verify(mockLog, never()).log(any(IStatus.class));

        logger.warn("warn message");
        ArgumentCaptor<IStatus> statusCaptor = ArgumentCaptor.forClass(IStatus.class);
        verify(mockLog).log(statusCaptor.capture());

        assertEquals(statusCaptor.getValue().getMessage(), "warn message");
        assertEquals(statusCaptor.getValue().getSeverity(), IStatus.WARNING);
    }

    @Test
    public void error_level_enabled_with_throwable()
    {
        System.setProperty(LOG_LEVEL_PROPERTY, "ERROR");
        DefaultLogger logger = new DefaultLogger(mockLog, PLUGIN_ID, LOG_LEVEL_PROPERTY);

        assertFalse(logger.warnEnabled());
        assertTrue(logger.errorEnabled());

        Throwable exception = new RuntimeException("Test exception");
        logger.error("error message", exception);

        ArgumentCaptor<IStatus> statusCaptor = ArgumentCaptor.forClass(IStatus.class);
        verify(mockLog).log(statusCaptor.capture());

        assertEquals(statusCaptor.getValue().getMessage(), "error message");
        assertEquals(statusCaptor.getValue().getSeverity(), IStatus.ERROR);
        assertEquals(statusCaptor.getValue().getException(), exception);
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

        assertTrue((statusCaptor.getValue().getMessage()).contains("java.lang.RuntimeException: Test exception"));
        assertEquals(statusCaptor.getValue().getSeverity(), IStatus.ERROR);
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

        assertEquals(statusCaptor.getValue().getMessage(), "warn message");
        assertEquals(statusCaptor.getValue().getSeverity(), IStatus.WARNING);
        assertEquals(statusCaptor.getValue().getException(), exception);
    }
}
