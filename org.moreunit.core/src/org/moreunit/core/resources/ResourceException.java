package org.moreunit.core.resources;

import java.io.Serial;

public class ResourceException extends RuntimeException
{
    @Serial
    private static final long serialVersionUID = 582514235872662780L;

    public ResourceException(String message)
    {
        super(message);
    }

    public ResourceException(Throwable cause)
    {
        super(cause);
    }

    public ResourceException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
