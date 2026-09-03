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
        final StringBuilder buffer = new StringBuilder();
        for (final FieldDependency d : context.dependenciesToMock().injectableByField())
        {
            final String resolvedPattern = "${objectUnderTest}.%s = %s".formatted(d.fieldName, d.name);
            buffer.append(preMatch).append(resolvedPattern).append(postMatch);
        }
        return buffer.toString();
    }
}
