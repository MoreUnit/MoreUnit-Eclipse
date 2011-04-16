package org.moreunit.mock.templates.resolvers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.moreunit.mock.model.FieldDependency;
import org.moreunit.mock.templates.MockingContext;

public class FieldInjectionPatternResolver extends SimplePatternResolver
{
    // content between parentheses is ignored for now
    private static final Pattern FIELD_INJECTION = Pattern.compile("\\$\\{:assignDependency\\(.*\\)\\}");

    public FieldInjectionPatternResolver(MockingContext context)
    {
        super(context, FIELD_INJECTION);
    }

    @Override
    protected String matched(Matcher matcher)
    {
        StringBuilder buffer = new StringBuilder();
        for (FieldDependency d : context.dependenciesToMock().injectableByField())
        {
            String resolvedPattern = String.format("\\$\\{objectUnderTest\\}.%s = %s", d.fieldName, d.name);
            buffer.append(matcher.replaceAll(resolvedPattern));
        }
        return buffer.toString();
    }
}
