package org.moreunit.mock.dependencies;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;

public class DependencyInjectionPointCollector implements DependencyInjectionPointProvider
{
    private static final Pattern SETTER_PATTERN = Pattern.compile("^set[A-Z].*");

    private final IType classUnderTest;
    private final IPackageFragment testCasePackage;

    private ITypeHierarchy typeHierarchy;

    public DependencyInjectionPointCollector(IType classUnderTest, IPackageFragment testCasePackage)
    {
        this.classUnderTest = classUnderTest;
        this.testCasePackage = testCasePackage;
    }

    public Collection<IMethod> getConstructors() throws JavaModelException
    {
        Collection<IMethod> constructors = new HashSet<IMethod>();

        for (IMethod method : classUnderTest.getMethods())
        {
            if(isVisibleToTestCase(method) && method.isConstructor() && method.getNumberOfParameters() > 0)
            {
                constructors.add(method);
            }
        }

        return constructors;
    }

    public boolean isVisibleToTestCase(IMember member) throws JavaModelException
    {
        int flags = member.getFlags();
        return ! Flags.isSynthetic(flags) && (Flags.isPublic(flags) || (canAccessPackage(classUnderTest, testCasePackage) && ! Flags.isPrivate(flags)));
    }

    protected boolean canAccessPackage(IType type, IPackageFragment packageFragment)
    {
        return packageFragment.getElementName().equals(type.getPackageFragment().getElementName());
    }

    public Collection<IMethod> getSetters() throws JavaModelException
    {
        Collection<IMethod> setters = new HashSet<IMethod>();

        for (IMethod method : getAllMethods())
        {
            String methodName = method.getElementName();
            if(isVisibleToTestCase(method) && method.getNumberOfParameters() == 1 && SETTER_PATTERN.matcher(methodName).matches())
            {
                setters.add(method);
            }
        }

        return setters;
    }

    private Set<IMethod> getAllMethods() throws JavaModelException
    {
        Set<IMethod> methods = new HashSet<IMethod>();
        for (IType type : getTypeHierarchy().getAllClasses())
        {
            Collections.addAll(methods, type.getMethods());
        }
        return methods;
    }

    private ITypeHierarchy getTypeHierarchy() throws JavaModelException
    {
        if(typeHierarchy == null)
        {
            typeHierarchy = classUnderTest.newSupertypeHierarchy(new NullProgressMonitor());
        }
        return typeHierarchy;
    }

    public Collection<Field> getFields() throws JavaModelException
    {
        HashSet<Field> fields = new HashSet<Field>();

        for (IField field : getAllFields())
        {
            fields.add(new Field(field, isVisibleToTestCase(field)));
        }

        return fields;
    }

    private Set<IField> getAllFields() throws JavaModelException
    {
        Set<IField> fields = new HashSet<IField>();
        for (IType type : getTypeHierarchy().getAllClasses())
        {
            Collections.addAll(fields, type.getFields());
        }
        return fields;
    }
}
