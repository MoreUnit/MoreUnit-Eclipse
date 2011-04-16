package org.moreunit.mock.dependencies;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

/**
 * Wraps a {@link DependencyInjectionPointProvider} so that it is queried only
 * once.
 */
public class DependencyInjectionPointProviderWrapper implements DependencyInjectionPointProvider
{
    private final Collection<IMethod> constructors = new HashSet<IMethod>();
    private final Collection<IMethod> setters = new HashSet<IMethod>();
    private final Collection<IField> fields = new HashSet<IField>();

    public DependencyInjectionPointProviderWrapper(DependencyInjectionPointProvider provider) throws JavaModelException
    {
        constructors.addAll(provider.getConstructors());
        setters.addAll(provider.getSetters());
        fields.addAll(provider.getFields());
    }

    public Collection<IMethod> getConstructors() throws JavaModelException
    {
        return constructors;
    }

    public Collection<IMethod> getSetters() throws JavaModelException
    {
        return setters;
    }

    public Collection<IField> getFields() throws JavaModelException
    {
        return fields;
    }
}
