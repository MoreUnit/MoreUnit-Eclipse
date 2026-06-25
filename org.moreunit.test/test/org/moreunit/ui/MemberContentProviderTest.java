package org.moreunit.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.moreunit.test.model.Types.type;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MemberContentProviderTest
{
    private Set<IType> types;
    private Set<IMethod> methods;

    @BeforeEach
    public void setUp()
    {
        types = new LinkedHashSet<>();
        methods = new LinkedHashSet<>();
    }

    @Test
    public void getElements_should_return_empty_list_when_no_type_nor_method_is_provided()
    {
        Object[] elements = new MemberContentProvider(types, methods, null).getElements(null);
        assertTrue(elements.length == 0);
    }

    @Test
    public void getElements_should_return_types_when_called_with_types()
    {
        types.add(type("type2"));
        types.add(type("type3"));
        types.add(type("type1"));

        MemberContentProvider contentProvider = new MemberContentProvider(types, methods, null);

        Object[] elements = contentProvider.getElements(null);
        assertEquals(Arrays.asList("type1", "type2", "type3"), namesOf(elements));
    }

    @Test
    public void should_build_elements_with_children_when_called_with_types_and_methods()
    {
        IType type2 = type("type2");
        types.add(type2);

        IType type1 = type("type1");
        types.add(type1);

        methods.add(mockMethod(type1, "method1B"));
        methods.add(mockMethod(type2, "method2A"));
        methods.add(mockMethod(type1, "method1A"));

        MemberContentProvider contentProvider = new MemberContentProvider(types, methods, null);

        Object[] elements = contentProvider.getElements(null);
        assertEquals(Arrays.asList("type1", "type2"), namesOf(elements));

        Object[] children1 = contentProvider.getChildren(elements[0]);
        assertEquals(Arrays.asList("method1A", "method1B"), namesOf(children1));

        Object[] children2 = contentProvider.getChildren(elements[1]);
        assertEquals(Arrays.asList("method2A"), namesOf(children2));
    }

    @Test
    public void should_detect_types_when_only_method_is_given()
    {
        IType type1 = type("type1");
        types.add(type1);

        IType type2 = type("type2"); // not added to type set
        methods.add(mockMethod(type2, "method2A"));

        MemberContentProvider contentProvider = new MemberContentProvider(types, methods, null);

        Object[] elements = contentProvider.getElements(null);
        assertEquals(Arrays.asList("type1", "type2"), namesOf(elements));

        Object[] children2 = contentProvider.getChildren(elements[1]);
        assertEquals(Arrays.asList("method2A"), namesOf(children2));
    }

    @Test
    public void should_build_with_types_with_and_without_methods()
    {
        IType type3 = type("type3");
        IType type1 = type("type1");

        methods.add(mockMethod(type1, "method1B"));
        methods.add(mockMethod(type3, "method3A"));
        methods.add(mockMethod(type1, "method1A"));

        IType type2 = type("type2");
        types.add(type2);

        MemberContentProvider contentProvider = new MemberContentProvider(types, methods, null);

        Object[] elements = contentProvider.getElements(null);
        assertEquals(Arrays.asList("type2", "type1", "type3"), namesOf(elements));

        Object[] children1 = contentProvider.getChildren(elements[1]);
        assertEquals(Arrays.asList("method1A", "method1B"), namesOf(children1));

        Object[] children3 = contentProvider.getChildren(elements[2]);
        assertEquals(Arrays.asList("method3A"), namesOf(children3));
    }

    private static java.util.List<String> namesOf(Object[] elements)
    {
        java.util.List<String> names = new java.util.ArrayList<>(elements.length);
        for (Object element : elements) {
            names.add(((org.eclipse.jdt.core.IMember) element).getElementName());
        }
        return names;
    }

    private IMethod mockMethod(IType declaringType, String methodName)
    {
        IMethod mock = mock(IMethod.class);
        when(mock.getElementName()).thenReturn(methodName);
        when(mock.getDeclaringType()).thenReturn(declaringType);
        return mock;
    }
}
