package org.moreunit.core.log;

public interface Logger
{
    boolean traceEnabled();

    void trace(Object message);

    boolean debugEnabled();

    void debug(Object message);

    boolean infoEnabled();

    void info(Object message);

    boolean warnEnabled();

    void warn(Object message);

    void warn(Object message, Throwable throwable);

    boolean errorEnabled();

    void error(Object message);

    void error(Throwable throwable);

    void error(Object message, Throwable throwable);
}
