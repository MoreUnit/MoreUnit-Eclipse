package org.moreunit.core.log;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.moreunit.core.util.IOUtils;

public class DefaultLogger implements Logger
{
    private final ILog logger;
    private final String pluginId;
    private final Level logLevel;

    public DefaultLogger(ILog log, String pluginId, String logLevelProperty)
    {
        this.logger = log;
        this.pluginId = pluginId;
        logLevel = Level.valueOf(System.getProperty(logLevelProperty, Level.INFO.name()).toUpperCase());
    }

    public boolean traceEnabled()
    {
        return levelEnabled(Level.TRACE);
    }

    public void trace(Object message)
    {
        if(traceEnabled())
        {
            log(IStatus.INFO, "[TRACE] " + message);
        }
    }

    public boolean debugEnabled()
    {
        return levelEnabled(Level.DEBUG);
    }

    private boolean levelEnabled(Level level)
    {
        return ! level.isLowerThan(logLevel);
    }

    public void debug(Object message)
    {
        if(debugEnabled())
        {
            log(IStatus.INFO, "[DEBUG] " + message);
        }
    }

    public boolean infoEnabled()
    {
        return levelEnabled(Level.INFO);
    }

    public void info(Object message)
    {
        if(infoEnabled())
        {
            log(IStatus.INFO, message);
        }
    }

    public boolean warnEnabled()
    {
        return levelEnabled(Level.WARNING);
    }

    public void warn(Object message)
    {
        if(warnEnabled())
        {
            log(IStatus.WARNING, message);
        }
    }

    public void warn(Object message, Throwable throwable)
    {
        if(warnEnabled())
        {
            log(IStatus.WARNING, message, throwable);
        }
    }

    public boolean errorEnabled()
    {
        return levelEnabled(Level.ERROR);
    }

    public void error(Object message)
    {
        if(errorEnabled())
        {
            log(IStatus.ERROR, message);
        }
    }

    public void error(Throwable throwable)
    {
        if(errorEnabled())
        {
            error(getStackTrace(throwable));
        }
    }

    private String getStackTrace(Throwable throwable)
    {
        ByteArrayOutputStream os = null;
        PrintStream printStream = null;
        try
        {
            os = new ByteArrayOutputStream();
            printStream = new PrintStream(os);
            throwable.printStackTrace(printStream);
            return os.toString();
        }
        finally
        {
            IOUtils.closeQuietly(printStream, os);
        }
    }

    public void error(Object message, Throwable throwable)
    {
        if(errorEnabled())
        {
            log(IStatus.ERROR, message, throwable);
        }
    }

    private void log(int severity, Object message)
    {
        logger.log(new Status(severity, pluginId, String.valueOf(message)));
    }

    private void log(int severity, Object message, Throwable throwable)
    {
        logger.log(new Status(severity, pluginId, String.valueOf(message), throwable));
    }
}
