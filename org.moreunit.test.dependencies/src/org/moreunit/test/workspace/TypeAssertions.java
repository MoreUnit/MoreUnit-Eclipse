package org.moreunit.test.workspace;

import static org.fest.assertions.Assertions.assertThat;

import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;

public class TypeAssertions
{
    private final TypeHandler typeHandler;

    public TypeAssertions(TypeHandler typeHandler)
    {
        this.typeHandler = typeHandler;
    }

    public TypeAssertions isEqualTo(IMember expectedType)
    {
        assertThat(typeHandler.get()).isEqualTo(expectedType);
        return this;
    }
}
