package org.moreunit.mock.dependencies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.moreunit.mock.model.TypeParameter.Kind;

public class Dependencies extends ArrayList<Dependency>
{
    private static final long serialVersionUID = - 8786785084170298943L;

    private static final Pattern TYPE_ANNOTATION = Pattern.compile("^(\\S+).*");
    private static final Pattern WILDCARD_EXTENDS_PATTERN = Pattern.compile("^(\\s+extends\\s+).*");
    private static final Pattern WILDCARD_SUPER_PATTERN = Pattern.compile("^(\\s+super\\s+).*");

    private final NamingRules namingRules;
    private final IType classUnderTest;
    private final DependencyInjectionPointStore injectionPointProvider;
    private final List<Dependency> constructorDependencies = new ArrayList<Dependency>();
    private final List<SetterDependency> setterDependencies = new ArrayList<SetterDependency>();
    private final List<FieldDependency> fieldDependencies = new ArrayList<FieldDependency>();

    public Dependencies(IType classUnderTest, DependencyInjectionPointStore injectionPointProvider, NamingRules namingRules)
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

        // removes type use annotations:
        // @NonNull etc. should probably not be put on test case fields
        cleanSignature = cleanSignature.replaceAll("^\\s*(?:\\S+\\s+)*?(\\S+)\\s*$", "$1");

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
            return resolveTypeParameters(signature.toCharArray(), new CharIterator(signature, indexOfAngleBracket + 1));
        }
        return new ArrayList<TypeParameter>();
    }

    private List<TypeParameter> resolveTypeParameters(char[] signatureBuffer, CharIterator iterator) throws JavaModelException
    {
        List<TypeParameter> parameters = new ArrayList<TypeParameter>();
        List<String> annotations = new ArrayList<String>();
        List<String> wildcardAnnotations = new ArrayList<String>();

        TypeParameter.Kind parameterKind = Kind.REGULAR;

        StringBuilder buffer = new StringBuilder();
        while (iterator.hasNext())
        {
            char c = iterator.next();
            if(c == '<' || c == ',' || c == '>')
            {
                if(buffer.length() != 0 || parameterKind == Kind.WILDARD_UNBOUNDED)
                {
                    TypeParameter parameter = TypeParameter.create(parameterKind, resolveTypeSignature(buffer.toString()))
                            .withAnnotations(annotations)
                            .withBaseTypeAnnotations(wildcardAnnotations);
                    parameters.add(parameter);

                    // reset
                    parameterKind = Kind.REGULAR;
                    buffer = new StringBuilder();

                    if(c == '>')
                    {
                        break;
                    }
                    if(c == '<')
                    {
                        parameter.typeParameters.addAll(resolveTypeParameters(signatureBuffer, iterator));
                    }
                }
                continue;
            }
            if(c == '@')
            {
                String annotation = consume(TYPE_ANNOTATION, iterator);
                if(annotation != null)
                {
                    (parameterKind == Kind.REGULAR ? annotations : wildcardAnnotations).add(resolveTypeSignature(annotation));
                }
                continue;
            }
            if(c == '?')
            {
                parameterKind = findWildcardType(iterator);
                continue;
            }
            if(c == ' ' || c == '\t' || c == '\r' || c == '\n')
            {
                continue;
            }
            buffer.append(c);
        }

        return parameters;
    }

    private TypeParameter.Kind findWildcardType(CharIterator iterator)
    {
        if(consume(WILDCARD_EXTENDS_PATTERN, iterator) != null)
        {
            return Kind.WILDCARD_EXTENDS;
        }
        if(consume(WILDCARD_SUPER_PATTERN, iterator) != null)
        {
            return Kind.WILDCARD_SUPER;
        }
        return Kind.WILDARD_UNBOUNDED;
    }

    private String consume(Pattern pattern, CharIterator iterator)
    {
        Matcher matcher = pattern.matcher(iterator.stringFromNextIdx());
        if(matcher.matches())
        {
            String capture = matcher.group(1);
            iterator.increment(capture.length());
            return capture;
        }
        return null;
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

    private static class CharIterator
    {
        final char[] chars;
        final int limit;
        int current;

        CharIterator(String string, int startIndex)
        {
            chars = string.toCharArray();
            limit = string.length();
            current = startIndex - 1;
        }

        String stringFromNextIdx()
        {
            return new String(chars, current + 1, limit - current - 1);
        }

        CharIterator increment(int offset)
        {
            while (offset-- > 0)
            {
                next();
            }
            return this;
        }

        boolean hasNext()
        {
            return current + 1 < limit;
        }

        char next()
        {
            if(! hasNext())
            {
                throw new IndexOutOfBoundsException();
            }
            return chars[++current];
        }
    }
}
