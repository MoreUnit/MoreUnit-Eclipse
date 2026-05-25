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

    @Override
    public boolean traceEnabled()
    {
        return levelEnabled(Level.TRACE);
    }

    @Override
    public void trace(Object message)
    {
        if(traceEnabled())
        {
            log(IStatus.INFO, "[TRACE] " + message);
        }
    }

    @Override
    public boolean debugEnabled()
    {
        return levelEnabled(Level.DEBUG);
    }

    private boolean levelEnabled(Level level)
    {
        return ! level.isLowerThan(logLevel);
    }

    @Override
    public void debug(Object message)
    {
        if(debugEnabled())
        {
            log(IStatus.INFO, "[DEBUG] " + message);
        }
    }

    @Override
    public boolean infoEnabled()
    {
        return levelEnabled(Level.INFO);
    }

    @Override
    public void info(Object message)
    {
        if(infoEnabled())
        {
            log(IStatus.INFO, message);
        }
    }

    @Override
    public boolean warnEnabled()
    {
        return levelEnabled(Level.WARNING);
    }

    @Override
    public void warn(Object message)
    {
        if(warnEnabled())
        {
            log(IStatus.WARNING, message);
        }
    }

    @Override
    public void warn(Object message, Throwable throwable)
    {
        if(warnEnabled())
        {
            log(IStatus.WARNING, message, throwable);
        }
    }

    @Override
    public boolean errorEnabled()
    {
        return levelEnabled(Level.ERROR);
    }

    @Override
    public void error(Object message)
    {
        if(errorEnabled())
        {
            log(IStatus.ERROR, message);
        }
    }

    @Override
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

    @Override
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
