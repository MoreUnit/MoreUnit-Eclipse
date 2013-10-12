package org.moreunit.mock.dependencies;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

/**
 * Wraps a {@link DependencyInjectionPointProvider} so that it is queried only
 * once.
 */
public class DependencyInjectionPointProviderCache implements DependencyInjectionPointProvider
{
    private final Collection<IMethod> constructors = new HashSet<IMethod>();
    private final Collection<IMethod> setters = new HashSet<IMethod>();
    private final Collection<Field> fields = new HashSet<Field>();
    private JavaModelException exception;

    public DependencyInjectionPointProviderCache(DependencyInjectionPointProvider provider)
    {
        try
        {
            constructors.addAll(provider.getConstructors());
            setters.addAll(provider.getSetters());
            fields.addAll(provider.getFields());
        }
        catch (JavaModelException e)
        {
            exception = e;
        }
    }

    public Collection<IMethod> getConstructors() throws JavaModelException
    {
        rethrowExceptionIfAny();
        return constructors;
    }

    private void rethrowExceptionIfAny() throws JavaModelException
    {
        if(exception != null)
        {
            throw exception;
        }
    }

    public Collection<IMethod> getSetters() throws JavaModelException
    {
        rethrowExceptionIfAny();
        return setters;
    }

    public Collection<Field> getFields() throws JavaModelException
    {
        rethrowExceptionIfAny();
        return fields;
    }
}
