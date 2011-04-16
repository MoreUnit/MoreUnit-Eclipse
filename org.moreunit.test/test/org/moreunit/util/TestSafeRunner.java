package org.moreunit.util;

public class TestSafeRunner extends ExtendedSafeRunner
{
    @Override
    public <T> void each(Iterable<T> elements, IGenericRunnable<T> code)
    {
        for (T e : elements)
        {
            try
            {
                code.run(e);
            }
            catch (Throwable t)
            {
                code.handleException(t, e);
            }
        }
    }
}
