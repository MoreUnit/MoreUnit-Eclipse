package org.moreunit.mock.templates.resolvers;

import java.util.Iterator;
import java.util.List;

import org.moreunit.mock.model.Dependency;
import org.moreunit.mock.model.TypeParameter;
import org.moreunit.mock.model.TypeUse.TypeAnnotation;
import org.moreunit.mock.templates.MockingContext;
import org.moreunit.mock.templates.PatternResolver;

public class DependencyPatternsResolver implements PatternResolver
{
    private final MockingContext context;

    public DependencyPatternsResolver(MockingContext context)
    {
        this.context = context;
    }

    public String resolve(String codePattern)
    {
        if(codePattern.indexOf("${dependencyType}") == - 1 && codePattern.indexOf("${dependency}") == - 1)
        {
            return codePattern;
        }

        StringBuilder buffer = new StringBuilder();
        for (Dependency d : context.dependenciesToMock())
        {
            String resolvedType = newTypeTpl(d.simpleClassName, d.fullyQualifiedClassName);
            String typeParams = buildTypeParametersDeclaration(d.typeParameters, new StringBuilder()).toString();

            buffer.append(codePattern.
                    replaceAll("\\$\\{dependencyType\\}\\s*\\.\\s*class", resolvedType + ".class").
                    replaceAll("\\$\\{dependencyType\\}", resolvedType + typeParams).
                    replaceAll("\\$\\{dependency\\}", d.name));
        }
        return buffer.toString();
    }

    private String newTypeTpl(String simpleName, String qualifiedName)
    {
        return String.format("\\${%sType:newType(%s)}", simpleName, qualifiedName);
    }

    private StringBuilder buildTypeParametersDeclaration(List<TypeParameter> typeParameters, StringBuilder buffer)
    {
        if(typeParameters.isEmpty())
        {
            return buffer;
        }

        buffer.append('<');

        for (Iterator<TypeParameter> paramIt = typeParameters.iterator(); paramIt.hasNext();)
        {
            TypeParameter p = paramIt.next();

            for (TypeAnnotation a : p.annotations)
            {
                buffer.append('@').append(newTypeTpl(a.simpleClassName, a.fullyQualifiedClassName)).append(' ');
            }

            buffer.append(p.wildcardExpression());

            if(p.hasName())
            {
                for (TypeAnnotation a : p.baseTypeAnnotations)
                {
                    buffer.append('@').append(newTypeTpl(a.simpleClassName, a.fullyQualifiedClassName)).append(' ');
                }

                buffer.append(newTypeTpl(p.simpleClassName, p.fullyQualifiedClassName));
            }

            buildTypeParametersDeclaration(p.typeParameters, buffer);

            if(paramIt.hasNext())
            {
                buffer.append(',');
            }
        }

        return buffer.append('>');
    }
}
