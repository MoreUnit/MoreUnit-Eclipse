package org.moreunit.mock.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;

public class Dependencies extends ArrayList<Dependency>
{
    private static final long serialVersionUID = - 8786785084170298943L;

    private static final Pattern SETTER_PATTERN = Pattern.compile("^set[A-Z].*");

    private final IType classUnderTest;
    private final IType testCase;
    public final List<Dependency> constructorDependencies = new ArrayList<Dependency>();
    public final List<SetterDependency> setterDependencies = new ArrayList<SetterDependency>();
    public final List<Dependency> fieldDependencies = new ArrayList<Dependency>();

    public Dependencies(IType classUnderTest, IType testCase)
    {
        this.classUnderTest = classUnderTest;
        this.testCase = testCase;
    }

    public void compute() throws JavaModelException
    {
        initContructorDependencies();
        initSetterDependencies();
        initFieldDependencies();
    }

    private void initContructorDependencies() throws JavaModelException
    {
        int parameterCount = - 1;
        IMethod constructor = null;
        for (IMethod method : classUnderTest.getMethods())
        {
            if(method.isConstructor() && method.getNumberOfParameters() > parameterCount)
            {
                constructor = method;
                parameterCount = method.getNumberOfParameters();
            }
        }

        if(constructor != null)
        {
            String[] parameterTypes = constructor.getParameterTypes();
            String[] parameterNames = constructor.getParameterNames();

            for (int i = 0; i < parameterNames.length; i++)
            {
                String signature = Signature.toString(parameterTypes[i]);
                Dependency dependency = new Dependency(resolveTypeSignature(signature), parameterNames[i], resolveTypeParameters(signature));
                if(! contains(dependency))
                {
                    constructorDependencies.add(dependency);
                    add(dependency);
                }
            }
        }
    }

    private void initSetterDependencies() throws JavaModelException
    {
        for (IMethod method : classUnderTest.getMethods())
        {
            String methodName = method.getElementName();
            if(method.getNumberOfParameters() == 1 && SETTER_PATTERN.matcher(methodName).matches())
            {
                String signature = Signature.toString(method.getParameterTypes()[0]);
                SetterDependency dependency = new SetterDependency(resolveTypeSignature(signature), methodName, resolveTypeParameters(signature));
                if(! contains(dependency))
                {
                    setterDependencies.add(dependency);
                    add(dependency);
                }
            }
        }
    }

    private String resolveTypeSignature(String signature) throws JavaModelException
    {
        String[][] possibleFieldTypes = classUnderTest.resolveType(signature);

        if(possibleFieldTypes.length != 0)
        {
            String[] fieldType = possibleFieldTypes[0];
            return fieldType[0] + "." + fieldType[1];
        }
        else
        {
            return signature;
        }
    }

    private void initFieldDependencies() throws JavaModelException
    {
        for (IField field : classUnderTest.getFields())
        {
            if(isVisibleToTestCase(field) && isAssignable(field))
            {
                String signature = Signature.toString(field.getTypeSignature());
                Dependency dependency = new Dependency(resolveTypeSignature(signature), field.getElementName(), resolveTypeParameters(signature));
                if(! contains(dependency))
                {
                    fieldDependencies.add(dependency);
                    add(dependency);
                }
            }
        }
    }

    List<TypeParameter> resolveTypeParameters(String signature) throws JavaModelException
    {
        int indexOfAngleBracket = signature.indexOf('<');
        if(indexOfAngleBracket != - 1)
        {
            return resolveTypeParameters(signature.toCharArray(), new StringIterator(signature, indexOfAngleBracket + 1));
        }
        return new ArrayList<TypeParameter>();
    }

    private List<TypeParameter> resolveTypeParameters(char[] signatureBuffer, StringIterator iterator) throws JavaModelException
    {
        List<TypeParameter> parameters = new ArrayList<TypeParameter>();

        StringBuilder buffer = new StringBuilder();
        for (int i = iterator.current; iterator.hasNext(); i = iterator.next())
        {
            char c = signatureBuffer[i];
            if(c == '<')
            {
                if(buffer.length() != 0)
                {
                    TypeParameter parameter = new TypeParameter(resolveTypeSignature(buffer.toString()));
                    parameter.internalParameters.addAll(resolveTypeParameters(signatureBuffer, iterator.increment()));
                    parameters.add(parameter);
                    buffer = new StringBuilder();
                }
                continue;
            }
            if(c == ' ')
            {
                continue;
            }
            if(c == ',' || c == '>')
            {
                if(buffer.length() != 0)
                {
                    parameters.add(new TypeParameter(resolveTypeSignature(buffer.toString())));
                    buffer = new StringBuilder();
                }
                if(c == '>')
                {
                    break;
                }
                continue;
            }
            buffer.append(c);
        }

        return parameters;
    }

    private boolean isVisibleToTestCase(IMember member) throws JavaModelException
    {
        if((member.getFlags() & ClassFileConstants.AccPublic) != 0)
        {
            return true;
        }
        else if(classUnderTest.getPackageFragment().equals(testCase.getPackageFragment()))
        {
            return (member.getFlags() & ClassFileConstants.AccPrivate) == 0;
        }
        return false;
    }

    private boolean isAssignable(IField field) throws JavaModelException
    {
        return (field.getFlags() & ClassFileConstants.AccFinal) == 0;
    }

    private static class StringIterator implements Iterator<Integer>
    {
        final int limit;
        int current;

        StringIterator(String string, int startIndex)
        {
            limit = string.length();
            current = startIndex;
        }

        public StringIterator increment()
        {
            next();
            return this;
        }

        public boolean hasNext()
        {
            return current < limit;
        }

        public Integer next()
        {
            if(! hasNext())
            {
                throw new IndexOutOfBoundsException();
            }
            return ++current;
        }

        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }
}
