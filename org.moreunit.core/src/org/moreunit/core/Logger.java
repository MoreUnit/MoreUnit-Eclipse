package org.moreunit.core;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class Logger
{
    private final ILog log;

    public Logger(ILog log)
    {
        this.log = log;
    }

    public void info(String message)
    {
        log(IStatus.INFO, message);
    }

    public void warning(String message)
    {
        log(IStatus.WARNING, message);
    }

    public void error(String message)
    {
        log(IStatus.ERROR, message);
    }

    public void error(Throwable t)
    {
        log(IStatus.ERROR, t.toString());
    }

    public void error(String message, Throwable t)
    {
        log(IStatus.ERROR, message + " - " + t.toString());
    }

    private void log(int severity, String message)
    {
        log.log(new Status(severity, MoreUnitCore.PLUGIN_ID, message));
    }
}
