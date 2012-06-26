package org.moreunit.core.matching;

import org.eclipse.core.runtime.IPath;

public class DoesNotMatchConfigurationException extends Exception
{
    private static final long serialVersionUID = - 8002049690736157605L;

    private final IPath path;

    public DoesNotMatchConfigurationException(IPath path)
    {
        this.path = path;
    }

    public IPath getPath()
    {
        return path;
    }
}
