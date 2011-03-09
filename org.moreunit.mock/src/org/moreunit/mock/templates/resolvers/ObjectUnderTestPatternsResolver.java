package org.moreunit.mock.templates.resolvers;

import org.moreunit.mock.templates.MockingContext;
import org.moreunit.mock.templates.PatternResolver;

public class ObjectUnderTestPatternsResolver implements PatternResolver
{
    private final MockingContext context;

    public ObjectUnderTestPatternsResolver(MockingContext context)
    {
        this.context = context;
    }

    public String resolve(String codePattern)
    {
        if(codePattern.indexOf("${objectUnderTestType}") == - 1 && codePattern.indexOf("${objectUnderTest}") == - 1)
        {
            return codePattern;
        }

        return codePattern
                .replaceAll("\\$\\{objectUnderTestType\\}", "\\${classUnderTest:newType(" + context.classUnderTest.getFullyQualifiedName() + ")}")
                .replaceAll("\\$\\{objectUnderTest\\}", objectUnderTestName());
    }

    private String objectUnderTestName()
    {
        String name = context.classUnderTest.getElementName();
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }
}
