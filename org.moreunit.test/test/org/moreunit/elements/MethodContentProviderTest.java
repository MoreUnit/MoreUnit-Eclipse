package org.moreunit.elements;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.eclipse.jdt.core.IMethod;
import org.junit.jupiter.api.Test;

public class MethodContentProviderTest
{
    @Test
    public void should_return_methods_as_elements()
    {
        IMethod method = mock(IMethod.class);
        MethodContentProvider provider = new MethodContentProvider(List.of(method));

        Object[] elements = provider.getElements(null);

        assertEquals(1, elements.length);
        assertNotNull(elements[0]);
    }

    @Test
    public void should_return_empty_array_for_empty_list()
    {
        MethodContentProvider provider = new MethodContentProvider(List.of());

        assertEquals(0, provider.getElements(null).length);
    }

    @Test
    public void dispose_and_input_changed_should_not_throw()
    {
        MethodContentProvider provider = new MethodContentProvider(List.of());
        provider.dispose();
        provider.inputChanged(null, null, null);
    }
}
