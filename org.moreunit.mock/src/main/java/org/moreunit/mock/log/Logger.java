package org.moreunit.mock.log;

public interface Logger
{
    boolean debugEnabled();

    void debug(String message);
    
    boolean infoEnabled();

    void info(String message);

    boolean warnEnabled();

    void warn(String message);

    boolean errorEnabled();

    void error(String message);

    void error(Throwable throwable);

    void error(String message, Throwable throwable);
}
