package org.moreunit.util;

import org.moreunit.core.util.ExtendedSafeRunner;

public class TestSafeRunner extends ExtendedSafeRunner
{
    public <E, R> R applyTo(E element, ExtendedSafeRunner.GenericRunnable<E, R> code)
    {
        try
        {
            return code.run(element);
        }
        catch (Throwable t)
        {
            code.handleException(t, element);
        }
        return null;
    }
}
