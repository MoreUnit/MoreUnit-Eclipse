package org.moreunit.core.log;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class DefaultLoggerTest {

    private static final String PLUGIN_ID = "org.moreunit.test";
    private static final String LOG_LEVEL_PROPERTY = "org.moreunit.log.level";

    private ILog mockLog;
    private String originalProperty;

    @BeforeEach
    void setUp() {
        mockLog = mock(ILog.class);
        originalProperty = System.getProperty(LOG_LEVEL_PROPERTY);
    }

    @AfterEach
    void tearDown() {
        if (originalProperty == null) {
            System.clearProperty(LOG_LEVEL_PROPERTY);
        } else {
            System.setProperty(LOG_LEVEL_PROPERTY, originalProperty);
        }
    }

    @Test
    void testTraceLevelEnabled() {
        System.setProperty(LOG_LEVEL_PROPERTY, "TRACE");
        DefaultLogger logger = new DefaultLogger(mockLog, PLUGIN_ID, LOG_LEVEL_PROPERTY);

        assertThat(logger.traceEnabled()).isTrue();
        assertThat(logger.debugEnabled()).isTrue();
        assertThat(logger.infoEnabled()).isTrue();
        assertThat(logger.warnEnabled()).isTrue();
        assertThat(logger.errorEnabled()).isTrue();
    }

    @Test
    void testInfoLevelEnabled() {
        System.setProperty(LOG_LEVEL_PROPERTY, "INFO");
        DefaultLogger logger = new DefaultLogger(mockLog, PLUGIN_ID, LOG_LEVEL_PROPERTY);

        assertThat(logger.traceEnabled()).isFalse();
        assertThat(logger.debugEnabled()).isFalse();
        assertThat(logger.infoEnabled()).isTrue();
        assertThat(logger.warnEnabled()).isTrue();
        assertThat(logger.errorEnabled()).isTrue();
    }

    @Test
    void testErrorLevelEnabled() {
        System.setProperty(LOG_LEVEL_PROPERTY, "ERROR");
        DefaultLogger logger = new DefaultLogger(mockLog, PLUGIN_ID, LOG_LEVEL_PROPERTY);

        assertThat(logger.traceEnabled()).isFalse();
        assertThat(logger.debugEnabled()).isFalse();
        assertThat(logger.infoEnabled()).isFalse();
        assertThat(logger.warnEnabled()).isFalse();
        assertThat(logger.errorEnabled()).isTrue();
    }

    @Test
    void testLoggingBelowLevelIsIgnored() {
        System.setProperty(LOG_LEVEL_PROPERTY, "INFO");
        DefaultLogger logger = new DefaultLogger(mockLog, PLUGIN_ID, LOG_LEVEL_PROPERTY);

        logger.trace("trace message");
        logger.debug("debug message");

        verifyNoInteractions(mockLog);
    }

    @Test
    void testInfoLogging() {
        System.setProperty(LOG_LEVEL_PROPERTY, "INFO");
        DefaultLogger logger = new DefaultLogger(mockLog, PLUGIN_ID, LOG_LEVEL_PROPERTY);

        logger.info("info message");

        ArgumentCaptor<IStatus> statusCaptor = ArgumentCaptor.forClass(IStatus.class);
        verify(mockLog).log(statusCaptor.capture());

        IStatus status = statusCaptor.getValue();
        assertThat(status.getSeverity()).isEqualTo(IStatus.INFO);
        assertThat(status.getMessage()).isEqualTo("info message");
        assertThat(status.getPlugin()).isEqualTo(PLUGIN_ID);
        assertThat(status.getException()).isNull();
    }

    @Test
    void testWarnLogging() {
        System.setProperty(LOG_LEVEL_PROPERTY, "WARNING");
        DefaultLogger logger = new DefaultLogger(mockLog, PLUGIN_ID, LOG_LEVEL_PROPERTY);

        logger.warn("warn message");

        ArgumentCaptor<IStatus> statusCaptor = ArgumentCaptor.forClass(IStatus.class);
        verify(mockLog).log(statusCaptor.capture());

        IStatus status = statusCaptor.getValue();
        assertThat(status.getSeverity()).isEqualTo(IStatus.WARNING);
        assertThat(status.getMessage()).isEqualTo("warn message");
    }

    @Test
    void testWarnLoggingWithException() {
        System.setProperty(LOG_LEVEL_PROPERTY, "WARNING");
        DefaultLogger logger = new DefaultLogger(mockLog, PLUGIN_ID, LOG_LEVEL_PROPERTY);

        Exception ex = new RuntimeException("test ex");
        logger.warn("warn message", ex);

        ArgumentCaptor<IStatus> statusCaptor = ArgumentCaptor.forClass(IStatus.class);
        verify(mockLog).log(statusCaptor.capture());

        IStatus status = statusCaptor.getValue();
        assertThat(status.getSeverity()).isEqualTo(IStatus.WARNING);
        assertThat(status.getMessage()).isEqualTo("warn message");
        assertThat(status.getException()).isEqualTo(ex);
    }

    @Test
    void testErrorLoggingWithException() {
        System.setProperty(LOG_LEVEL_PROPERTY, "ERROR");
        DefaultLogger logger = new DefaultLogger(mockLog, PLUGIN_ID, LOG_LEVEL_PROPERTY);

        Exception ex = new RuntimeException("test error");
        logger.error(ex);

        ArgumentCaptor<IStatus> statusCaptor = ArgumentCaptor.forClass(IStatus.class);
        verify(mockLog).log(statusCaptor.capture());

        IStatus status = statusCaptor.getValue();
        assertThat(status.getSeverity()).isEqualTo(IStatus.ERROR);
        assertThat(status.getMessage()).contains("java.lang.RuntimeException: test error");
    }

    @Test
    void testTraceAndDebugPrefixes() {
        System.setProperty(LOG_LEVEL_PROPERTY, "TRACE");
        DefaultLogger logger = new DefaultLogger(mockLog, PLUGIN_ID, LOG_LEVEL_PROPERTY);

        logger.trace("trace message");
        logger.debug("debug message");

        ArgumentCaptor<IStatus> statusCaptor = ArgumentCaptor.forClass(IStatus.class);
        verify(mockLog, org.mockito.Mockito.times(2)).log(statusCaptor.capture());

        assertThat(statusCaptor.getAllValues().get(0).getMessage()).isEqualTo("[TRACE] trace message");
        assertThat(statusCaptor.getAllValues().get(1).getMessage()).isEqualTo("[DEBUG] debug message");
    }
}
