package org.moreunit.test.mockito;

import static org.mockito.Matchers.argThat;

public class MoreUnitMatchers
{
    public static <T> T oneOf(T... args)
    {
        return argThat(new IsOneOf<T>(args));
    }
}
