package org.moreunit.test.workspace;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.jdt.core.IMember;

public class MethodAssertions
{
    private final MethodHandler methodHandler;

    public MethodAssertions(MethodHandler methodHandler)
    {
        this.methodHandler = methodHandler;
    }

    public MethodAssertions isEqualTo(IMember expectedMethod)
    {
        assertEquals(methodHandler.get(), expectedMethod);
        return this;
    }
}
