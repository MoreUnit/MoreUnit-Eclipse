package org.moreunit.core.log;

public interface Logger
{
    boolean debugEnabled();

    void debug(Object message);

    boolean infoEnabled();

    void info(Object message);

    boolean warnEnabled();

    void warn(Object message);

    boolean errorEnabled();

    void error(Object message);

    void error(Throwable throwable);

    void error(Object message, Throwable throwable);
}
