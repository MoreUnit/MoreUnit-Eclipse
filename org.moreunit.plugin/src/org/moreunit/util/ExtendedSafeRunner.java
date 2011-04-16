package org.moreunit.util;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;

public class ExtendedSafeRunner
{
    public static interface IGenericRunnable<T>
    {
        void handleException(Throwable throwable, T element);

        void run(T element) throws Exception;
    }

    public <T> void each(Iterable<T> elements, IGenericRunnable<T> code)
    {
        for (T element : elements)
        {
            run(new SafeRunnable<T>(code, element));
        }
    }

    public void run(ISafeRunnable code)
    {
        SafeRunner.run(code);
    }

    private static class SafeRunnable<T> implements ISafeRunnable
    {
        final IGenericRunnable<T> internalRunnable;
        final T element;

        SafeRunnable(IGenericRunnable<T> internalRunnable, T element)
        {
            this.internalRunnable = internalRunnable;
            this.element = element;
        }

        public void handleException(Throwable throwable)
        {
            internalRunnable.handleException(throwable, element);
        }

        public void run() throws Exception
        {
            internalRunnable.run(element);
        }

    }
}
