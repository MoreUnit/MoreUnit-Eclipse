package org.moreunit.mock.dependencies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.moreunit.mock.elements.NamingRules;
import org.moreunit.mock.model.Dependency;
import org.moreunit.mock.model.FieldDependency;
import org.moreunit.mock.model.SetterDependency;
import org.moreunit.mock.model.TypeParameter;

public class Dependencies extends ArrayList<Dependency>
{
    private static final long serialVersionUID = - 8786785084170298943L;

    private final NamingRules namingRules;
    private final IType classUnderTest;
    private final DependencyInjectionPointProvider injectionPointProvider;
    private final List<Dependency> constructorDependencies = new ArrayList<Dependency>();
    private final List<SetterDependency> setterDependencies = new ArrayList<SetterDependency>();
    private final List<FieldDependency> fieldDependencies = new ArrayList<FieldDependency>();

    public Dependencies(IType classUnderTest, DependencyInjectionPointProvider injectionPointProvider, NamingRules namingRules)
    {
        this.namingRules = namingRules;
        this.classUnderTest = classUnderTest;
        this.injectionPointProvider = injectionPointProvider;
    }

    public void init() throws JavaModelException
    {
        initContructorDependencies();
        initSetterDependencies();
        initFieldDependencies();

        Collections.sort(setterDependencies);
        Collections.sort(fieldDependencies);
        Collections.sort(this);
    }

    private void initContructorDependencies() throws JavaModelException
    {
        IMethod constructor = null;
        for (IMethod c : injectionPointProvider.getConstructors())
        {
            if(constructor == null || c.getNumberOfParameters() > constructor.getNumberOfParameters())
            {
                constructor = c;
            }
        }

        if(constructor != null)
        {
            String[] parameterTypes = constructor.getParameterTypes();
            String[] parameterNames = constructor.getParameterNames();

            for (int i = 0; i < parameterNames.length; i++)
            {
                Dependency dependency = createConstructorDependency(parameterTypes[i], parameterNames[i]);
                if(! contains(dependency))
                {
                    constructorDependencies.add(dependency);
                    add(dependency);
                }
            }
        }
    }

    private Dependency createConstructorDependency(String parameterType, String parameterName) throws JavaModelException
    {
        String signature = Signature.toString(parameterType);
        String dependencyName = namingRules.cleanParameterName(parameterName);
        return new Dependency(resolveTypeSignature(signature), dependencyName, resolveTypeParameters(signature));
    }

    private void initSetterDependencies() throws JavaModelException
    {
        for (IMethod method : injectionPointProvider.getSetters())
        {
            SetterDependency dependency = createSetterDependency(method);
            if(! contains(dependency))
            {
                setterDependencies.add(dependency);
                add(dependency);
            }
        }
    }

    private SetterDependency createSetterDependency(IMethod method) throws JavaModelException
    {
        String signature = Signature.toString(method.getParameterTypes()[0]);
        return new SetterDependency(resolveTypeSignature(signature), method.getElementName(), resolveTypeParameters(signature));
    }

    String resolveTypeSignature(String signature) throws JavaModelException
    {
        // removes type parameters
        String cleanSignature = signature.replaceFirst("<.+>$", "");

        String[][] possibleFieldTypes = classUnderTest.resolveType(cleanSignature);
        if(possibleFieldTypes == null || possibleFieldTypes.length == 0)
        {
            return cleanSignature;
        }

        String[] fieldType = possibleFieldTypes[0];
        return fieldType[0] + "." + fieldType[1];
    }

    private void initFieldDependencies() throws JavaModelException
    {
        for (IField field : injectionPointProvider.getFields())
        {
            FieldDependency dependency = createFieldDependency(field);
            if(! contains(dependency))
            {
                fieldDependencies.add(dependency);
                add(dependency);
            }
        }
    }

    private FieldDependency createFieldDependency(IField field) throws JavaModelException
    {
        String signature = Signature.toString(field.getTypeSignature());
        String fieldName = field.getElementName();
        String dependencyName = namingRules.cleanFieldName(fieldName);
        return new FieldDependency(resolveTypeSignature(signature), fieldName, dependencyName, resolveTypeParameters(signature));
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

    public List<Dependency> injectableByConstructor()
    {
        return constructorDependencies;
    }

    public List<SetterDependency> injectableBySetter()
    {
        return setterDependencies;
    }

    public List<FieldDependency> injectableByField()
    {
        return fieldDependencies;
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
