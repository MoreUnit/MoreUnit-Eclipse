package org.moreunit.mock.templates.resolvers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.moreunit.mock.templates.MockingContext;
import org.moreunit.mock.templates.PatternResolver;

public abstract class SimplePatternResolver implements PatternResolver
{
    private final Pattern pattern;
    protected final MockingContext context;

    protected SimplePatternResolver(MockingContext context, Pattern pattern)
    {
        this.context = context;
        this.pattern = pattern;
    }

    public String resolve(String codePattern)
    {
        Matcher matcher = pattern.matcher(codePattern);
        if(matcher.find())
        {
            return matched(matcher);
        }
        return codePattern;
    }

    protected abstract String matched(Matcher matcher);
}
