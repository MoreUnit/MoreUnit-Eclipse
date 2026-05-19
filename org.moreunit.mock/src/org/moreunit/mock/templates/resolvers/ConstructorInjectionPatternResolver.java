package org.moreunit.mock.templates.resolvers;

import java.util.Iterator;

import org.moreunit.mock.model.Dependency;
import org.moreunit.mock.templates.MockingContext;

public class ConstructorInjectionPatternResolver extends SimplePatternResolver
{
    public ConstructorInjectionPatternResolver(MockingContext context)
    {
        super(context, "${:constructWithDependencies(");
    }

    @Override
    protected String matched(String preMatch, String postMatch)
    {
        StringBuilder buffer = new StringBuilder("new ${objectUnderTestType}(");

        for (Iterator<Dependency> it = context.dependenciesToMock().injectableByConstructor().iterator(); it.hasNext();)
        {
            buffer.append(it.next().name);
            if(it.hasNext())
            {
                buffer.append(",");
            }
        }
        return preMatch + buffer.append(")").toString() + postMatch;
    }
}
