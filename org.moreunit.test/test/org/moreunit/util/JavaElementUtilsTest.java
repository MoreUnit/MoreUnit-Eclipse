package org.moreunit.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.junit.jupiter.api.Test;

public class JavaElementUtilsTest
{
    @Test
    public void toArray_should_return_empty_array_for_empty_collection()
    {
        IJavaElement[] result = JavaElementUtils.toArray(Collections.emptyList());
        assertEquals(0, result.length);
    }

    @Test
    public void toArray_should_return_array_with_elements_from_collection()
    {
        IJavaElement element1 = mock(IJavaElement.class);
        IJavaElement element2 = mock(IJavaElement.class);
        List<IJavaElement> elements = Arrays.asList(element1, element2);

        IJavaElement[] result = JavaElementUtils.toArray(elements);

        assertArrayEquals(new IJavaElement[] { element1, element2 }, result);
    }
}
