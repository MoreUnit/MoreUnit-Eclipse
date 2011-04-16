package org.moreunit.mock.templates.resolvers;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.moreunit.mock.model.Dependency;
import org.moreunit.mock.templates.MockingContext;

public class ConstructorInjectionPatternResolver extends SimplePatternResolver
{
    // content between parentheses is ignored for now
    private static final Pattern CONSTRUCTOR_INJECTION = Pattern.compile("\\$\\{:constructWithDependencies\\(.*\\)\\}");

    public ConstructorInjectionPatternResolver(MockingContext context)
    {
        super(context, CONSTRUCTOR_INJECTION);
    }

    @Override
    protected String matched(Matcher matcher)
    {
        StringBuilder buffer = new StringBuilder("new \\$\\{objectUnderTestType\\}(");

        for (Iterator<Dependency> it = context.dependenciesToMock().injectableByConstructor().iterator(); it.hasNext();)
        {
            buffer.append(it.next().name);
            if(it.hasNext())
            {
                buffer.append(",");
            }
        }
        return matcher.replaceAll(buffer.append(")").toString());
    }
}
