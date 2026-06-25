package org.moreunit.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.jdt.core.IType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TypeComparatorTest
{
    private TypeComparator typeComparator;

    @BeforeEach
    public void setUp()
    {
        typeComparator = new TypeComparator();
    }

    @Test
    public void should_order_types_by_fully_qualified_name()
    {
        IType typeA = mock(IType.class);
        when(typeA.getFullyQualifiedName()).thenReturn("org.example.AClass");

        IType typeB = mock(IType.class);
        when(typeB.getFullyQualifiedName()).thenReturn("org.example.BClass");

        assertTrue(typeComparator.compare(typeA, typeB) < 0);
        assertTrue(typeComparator.compare(typeB, typeA) > 0);
    }

    @Test
    public void should_return_zero_for_equal_types()
    {
        IType typeA = mock(IType.class);
        when(typeA.getFullyQualifiedName()).thenReturn("org.example.AClass");

        IType typeB = mock(IType.class);
        when(typeB.getFullyQualifiedName()).thenReturn("org.example.AClass");

        assertEquals(0, typeComparator.compare(typeA, typeB));
    }
}
