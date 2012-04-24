package org.moreunit.mock.log;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.moreunit.core.util.IOUtils;
import org.moreunit.mock.MoreUnitMockPlugin;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DefaultLogger implements Logger
{
    private static final Level LOG_LEVEL = Level.valueOf(System.getProperty("org.moreunit.mock.log.level", Level.INFO.name()).toUpperCase());

    private final ILog logger;

    @Inject
    public DefaultLogger(ILog logger)
    {
        this.logger = logger;
    }

    public boolean debugEnabled()
    {
        return levelEnabled(Level.DEBUG);
    }

    private boolean levelEnabled(Level level)
    {
        return ! level.isLowerThan(LOG_LEVEL);
    }

    public void debug(Object message)
    {
        if(debugEnabled())
        {
            logger.log(new Status(IStatus.INFO, MoreUnitMockPlugin.PLUGIN_ID, "[DEBUG] " + message));
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
            logger.log(new Status(IStatus.INFO, MoreUnitMockPlugin.PLUGIN_ID, String.valueOf(message)));
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
            logger.log(new Status(IStatus.WARNING, MoreUnitMockPlugin.PLUGIN_ID, String.valueOf(message)));
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
            logger.log(new Status(IStatus.ERROR, MoreUnitMockPlugin.PLUGIN_ID, String.valueOf(message)));
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
            logger.log(new Status(IStatus.ERROR, MoreUnitMockPlugin.PLUGIN_ID, String.valueOf(message), throwable));
        }
    }

}
