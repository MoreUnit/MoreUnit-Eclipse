package org.moreunit.mock.log;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.moreunit.mock.MoreUnitMockPlugin;
import org.moreunit.mock.utils.IOUtils;
import org.moreunit.wizards.MoreUnitStatus;

public class DefaultLogger implements Logger
{
    private final ILog logger;

    public DefaultLogger()
    {
        this(MoreUnitMockPlugin.getDefault().getLog());
    }

    public DefaultLogger(ILog logger)
    {
        this.logger = logger;
    }

    public boolean debugEnabled()
    {
        return true; // not implemented yet
    }

    public void debug(String message)
    {
        if(debugEnabled())
        {
            logger.log(new MoreUnitStatus(IStatus.INFO, "[DEBUG] " + message));
        }
    }

    public boolean infoEnabled()
    {
        return true; // not implemented yet
    }

    public void info(String message)
    {
        if(infoEnabled())
        {
            logger.log(new MoreUnitStatus(IStatus.INFO, message));
        }
    }

    public boolean warnEnabled()
    {
        return true; // not implemented yet
    }

    public void warn(String message)
    {
        if(warnEnabled())
        {
            logger.log(new MoreUnitStatus(IStatus.WARNING, message));
        }
    }

    public boolean errorEnabled()
    {
        return true; // not implemented yet
    }

    public void error(String message)
    {
        if(errorEnabled())
        {
            logger.log(new MoreUnitStatus(IStatus.ERROR, message));
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

    public void error(String message, Throwable throwable)
    {
        if(errorEnabled())
        {
            error(message + ": " + throwable.getMessage());
        }
    }

}
