package org.moreunit.mock.templates.resolvers;

import org.moreunit.mock.templates.MockingContext;
import org.moreunit.mock.templates.PatternResolver;

public abstract class SimplePatternResolver implements PatternResolver
{
    private final String prefix;
    protected final MockingContext context;

    protected SimplePatternResolver(MockingContext context, String prefix)
    {
        this.context = context;
        this.prefix = prefix;
    }

    @Override
    public String resolve(String codePattern)
    {
        int startIdx = codePattern.indexOf(prefix);
        if(startIdx != -1)
        {
            int endIdx = codePattern.indexOf(")}", startIdx + prefix.length());
            if(endIdx != -1)
            {
                String preMatch = codePattern.substring(0, startIdx);
                String postMatch = codePattern.substring(endIdx + 2);
                return matched(preMatch, postMatch);
            }
        }
        return codePattern;
    }

    protected abstract String matched(String preMatch, String postMatch);
}
