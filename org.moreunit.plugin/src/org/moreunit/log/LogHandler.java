package org.moreunit.log;

import org.moreunit.MoreUnitPlugin;
import org.moreunit.core.log.Logger;

public class LogHandler
{
    private static class InstanceHolder
    {
        static LogHandler instance = new LogHandler(MoreUnitPlugin.getDefault().getLogger());
    }

    public static LogHandler getInstance()
    {
        return InstanceHolder.instance;
    }

    private Logger logger;

    private LogHandler(Logger logger)
    {
        this.logger = logger;
    }

    public void handleExceptionLog(String message, Throwable throwable)
    {
        logger.error(message, throwable);
    }

    public void handleExceptionLog(Throwable throwable)
    {
        logger.error(throwable);
    }

    public void handleInfoLog(String infoMessage)
    {
        logger.info(infoMessage);
    }

    public void handleWarnLog(String warning)
    {
        logger.warn(warning);
    }
}
