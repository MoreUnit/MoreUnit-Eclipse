package org.moreunit.mock.dependencies;

import java.util.Collection;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

public interface DependencyInjectionPointProvider
{
    Collection<IMethod> getConstructors() throws JavaModelException;

    Collection<IMethod> getSetters() throws JavaModelException;

    Collection<IField> getFields() throws JavaModelException;
}
