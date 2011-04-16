package org.moreunit.mock.templates.resolvers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.moreunit.mock.model.SetterDependency;
import org.moreunit.mock.templates.MockingContext;

public class SetterInjectionPatternResolver extends SimplePatternResolver
{
    // content between parentheses is ignored for now
    private static final Pattern SETTER_INJECTION = Pattern.compile("\\$\\{:setDependency\\(.*\\)\\}");

    public SetterInjectionPatternResolver(MockingContext context)
    {
        super(context, SETTER_INJECTION);
    }

    @Override
    protected String matched(Matcher matcher)
    {
        StringBuilder buffer = new StringBuilder();
        for (SetterDependency d : context.dependenciesToMock().injectableBySetter())
        {
            String resolvedPattern = String.format("\\$\\{objectUnderTest\\}.%s(%s)", d.setterMethodName, d.name);
            buffer.append(matcher.replaceAll(resolvedPattern));
        }
        return buffer.toString();
    }
}
