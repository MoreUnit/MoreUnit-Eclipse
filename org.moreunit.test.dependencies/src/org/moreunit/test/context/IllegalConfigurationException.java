package org.moreunit.test.context;

import java.io.Serial;

public class IllegalConfigurationException extends RuntimeException
{
    @Serial
    private static final long serialVersionUID = - 3529999478764028075L;

    public IllegalConfigurationException(String message)
    {
        super(message);
    }
}
