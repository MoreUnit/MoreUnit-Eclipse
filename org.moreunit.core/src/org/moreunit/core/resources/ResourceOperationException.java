package org.moreunit.core.resources;

import java.io.Serial;

public class ResourceOperationException extends RuntimeException
{
    @Serial
    private static final long serialVersionUID = 1L;

    public ResourceOperationException(Throwable cause)
    {
        super(cause);
    }
}
