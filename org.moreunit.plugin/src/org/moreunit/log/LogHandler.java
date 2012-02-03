package org.moreunit.log;

public class LogHandler
{

    private static LogHandler instance;

    public static LogHandler getInstance()
    {
        if(instance == null)
            instance = new LogHandler();

        return instance;
    }

    private LogHandler()
    {
    }

    public void handleExceptionLog(String message, Throwable throwable)
    {
        System.out.println("moreunit: (EXC) " + message);
        System.out.println("moreunit: (EXC) " + throwable.getMessage());
        throwable.printStackTrace();
    }

    public void handleExceptionLog(Throwable throwable)
    {
        System.out.println("moreunit: (EXC) " + throwable.getMessage());
        throwable.printStackTrace();
    }

    public void handleInfoLog(String infoMessage)
    {
        System.out.println("moreunit: (INFO) " + infoMessage);
    }

    public void handleWarnLog(String warning)
    {
        System.out.println("moreunit (WARN) :" + warning);
    }
}