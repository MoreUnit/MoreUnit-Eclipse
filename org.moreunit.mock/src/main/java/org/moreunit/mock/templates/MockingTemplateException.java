package org.moreunit.mock.templates;

public class MockingTemplateException extends Exception
{
    private static final long serialVersionUID = 5284818313493437214L;

    private final boolean userMessage;

    public MockingTemplateException(String message)
    {
        this(message, false);
    }

    public MockingTemplateException(String message, boolean userMessage)
    {
        super(message);
        this.userMessage = userMessage;
    }

    public MockingTemplateException(Throwable cause)
    {
        super(cause);
        this.userMessage = false;
    }

    public MockingTemplateException(String message, Throwable cause)
    {
        this(message, cause, false);
    }

    public MockingTemplateException(String message, Throwable cause, boolean userMessage)
    {
        super(message, cause);
        this.userMessage = userMessage;
    }

    public boolean isUserMessage()
    {
        return userMessage;
    }
}
