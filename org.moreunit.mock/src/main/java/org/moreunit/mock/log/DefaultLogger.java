package org.moreunit.mock.log;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.moreunit.mock.MoreUnitMockPlugin;
import org.moreunit.mock.utils.IOUtils;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DefaultLogger implements Logger
{
    private final ILog logger;

    @Inject
    public DefaultLogger(ILog logger)
    {
        this.logger = logger;
    }

    public boolean debugEnabled()
    {
        return true; // not implemented yet
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
        return true; // not implemented yet
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
        return true; // not implemented yet
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
        return true; // not implemented yet
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
