package org.moreunit.test.mockito;

import static java.util.Arrays.asList;

import java.util.List;

import org.hamcrest.Description;
import org.mockito.ArgumentMatcher;

public class IsOneOf<T> extends ArgumentMatcher<T>
{
    private final List<T> wanted;

    public IsOneOf(T... wanted)
    {
        this.wanted = asList(wanted);
    }

    @Override
    public boolean matches(Object arg)
    {
        return wanted.contains(arg);
    }

    @Override
    public void describeTo(Description description)
    {
        description.appendText("is one of ").appendValueList("(", ", ", ")", wanted);
    }
}
