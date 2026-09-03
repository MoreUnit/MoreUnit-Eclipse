package org.moreunit.mock.templates.resolvers;

import org.moreunit.mock.model.SetterDependency;
import org.moreunit.mock.templates.MockingContext;

public class SetterInjectionPatternResolver extends SimplePatternResolver
{
    public SetterInjectionPatternResolver(MockingContext context)
    {
        super(context, "${:setDependency(");
    }

    @Override
    protected String matched(String preMatch, String postMatch)
    {
        final StringBuilder buffer = new StringBuilder();
        for (final SetterDependency d : context.dependenciesToMock().injectableBySetter())
        {
            final String resolvedPattern = "${objectUnderTest}.%s(%s)".formatted(d.setterMethodName, d.name);
            buffer.append(preMatch).append(resolvedPattern).append(postMatch);
        }
        return buffer.toString();
    }
}
