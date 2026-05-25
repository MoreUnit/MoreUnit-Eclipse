package org.moreunit.util;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.jdt.core.IMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MethodComparatorTest
{
    private MethodComparator methodComparator;

    @BeforeEach
    public void setUp()
    {
        methodComparator = new MethodComparator();
    }

    @Test
    public void should_order_methods_by_element_name()
    {
        IMethod methodA = mock(IMethod.class);
        when(methodA.getElementName()).thenReturn("methodA");

        IMethod methodB = mock(IMethod.class);
        when(methodB.getElementName()).thenReturn("methodB");

        assertTrue(methodComparator.compare(methodA, methodB) < 0);
        assertTrue(methodComparator.compare(methodB, methodA) > 0);
    }

    @Test
    public void should_return_zero_for_equal_methods()
    {
        IMethod methodA = mock(IMethod.class);
        when(methodA.getElementName()).thenReturn("methodA");

        IMethod methodB = mock(IMethod.class);
        when(methodB.getElementName()).thenReturn("methodA");

        assertEquals(0, methodComparator.compare(methodA, methodB));
    }
}
