package org.moreunit.core.matching;

import org.moreunit.core.resources.Path;

public class DoesNotMatchConfigurationException extends Exception
{
    private static final long serialVersionUID = - 8002049690736157605L;

    private final Path path;

    public DoesNotMatchConfigurationException(Path path)
    {
        this.path = path;
    }

    public Path getPath()
    {
        return path;
    }
}
