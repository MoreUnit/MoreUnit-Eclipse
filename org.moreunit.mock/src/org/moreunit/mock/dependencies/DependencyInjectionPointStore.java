package org.moreunit.mock.dependencies;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.moreunit.core.log.Logger;

public class DependencyInjectionPointStore implements DependencyInjectionPointProvider
{
    private final Collection<IMethod> constructors = new HashSet<IMethod>();
    private final Collection<IMethod> setters = new HashSet<IMethod>();
    private final Collection<IField> fields = new HashSet<IField>();

    private final DependencyInjectionPointProvider authorizedPointProvider;
    private final Logger logger;

    public DependencyInjectionPointStore(DependencyInjectionPointProvider authorizedPointProvider, Logger logger)
    {
        this.authorizedPointProvider = authorizedPointProvider;
        this.logger = logger;
    }

    public void setInjectionPoints(Collection<IMember> members)
    {
        constructors.clear();
        setters.clear();
        fields.clear();

        try
        {
            for (IMember member : members)
            {
                if(member instanceof IField && authorizedPointProvider.getFields().contains(member))
                {
                    fields.add((IField) member);
                }
                else if(member instanceof IMethod)
                {
                    IMethod method = (IMethod) member;
                    if(authorizedPointProvider.getConstructors().contains(method))
                    {
                        constructors.add(method);
                    }
                    else if(authorizedPointProvider.getSetters().contains(method))
                    {
                        setters.add(method);
                    }
                }
            }
        }
        catch (JavaModelException e)
        {
            logger.error("Error while feeding injection points' store", e);
        }
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
