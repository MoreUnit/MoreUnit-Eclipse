package org.moreunit.test.workspace;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.jdt.core.IMember;

public class TypeAssertions
{
    private final TypeHandler typeHandler;

    public TypeAssertions(TypeHandler typeHandler)
    {
        this.typeHandler = typeHandler;
    }

    public TypeAssertions isEqualTo(IMember expectedType)
    {
        assertEquals(typeHandler.get(), expectedType);
        return this;
    }
}
