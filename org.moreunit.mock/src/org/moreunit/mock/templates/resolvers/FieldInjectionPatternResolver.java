package org.moreunit.mock.templates.resolvers;

import org.moreunit.mock.model.FieldDependency;
import org.moreunit.mock.templates.MockingContext;

public class FieldInjectionPatternResolver extends SimplePatternResolver
{
    public FieldInjectionPatternResolver(MockingContext context)
    {
        super(context, "${:assignDependency(");
    }

    @Override
    protected String matched(String preMatch, String postMatch)
    {
        StringBuilder buffer = new StringBuilder();
        for (FieldDependency d : context.dependenciesToMock().injectableByField())
        {
            String resolvedPattern = "${objectUnderTest}.%s = %s".formatted(d.fieldName, d.name);
            buffer.append(preMatch).append(resolvedPattern).append(postMatch);
        }
        return buffer.toString();
    }
}
