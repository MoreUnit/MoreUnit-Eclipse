package org.moreunit.mock.templates;

public class TemplateException extends Exception
{

    private static final long serialVersionUID = 5284818313493437214L;

    public TemplateException(String message)
    {
        super(message);
    }

    public TemplateException(Throwable cause)
    {
        super(cause);
    }

    public TemplateException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
