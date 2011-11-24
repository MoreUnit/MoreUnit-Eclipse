package org.moreunit.test.workspace;

import static org.fest.assertions.Assertions.assertThat;

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
        assertThat(methodHandler.get()).isEqualTo(expectedMethod);
        return this;
    }
}
