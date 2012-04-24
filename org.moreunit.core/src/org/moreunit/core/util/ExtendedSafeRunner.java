package org.moreunit.core.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;

public class ExtendedSafeRunner
{
    public static abstract class GenericRunnable<E, R>
    {
        private R result;

        public abstract void handleException(Throwable throwable, E element);

        public abstract R run(E element) throws Exception;

        private void doRun(E element) throws Exception
        {
            result = run(element);
        }
    }

    public <E, R> R applyTo(E element, GenericRunnable<E, R> code)
    {
        run(new SafeRunnable<E, R>(code, element));
        return code.result;
    }

    public <E, R> Iterable<R> applyTo(Iterable<E> elements, GenericRunnable<E, R> code)
    {
        List<R> results = new ArrayList<R>();
        for (E element : elements)
        {
            results.add(applyTo(element, code));
        }
        return results;
    }

    private void run(ISafeRunnable code)
    {
        SafeRunner.run(code);
    }

    private static class SafeRunnable<E, R> implements ISafeRunnable
    {
        final GenericRunnable<E, R> internalRunnable;
        final E element;

        SafeRunnable(GenericRunnable<E, R> internalRunnable, E element)
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
            internalRunnable.doRun(element);
        }
    }
}
