package org.moreunit.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.moreunit.test.model.Types.type;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.viewers.StructuredSelection;
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

    @Test
    public void constructor_with_proposed_type_should_propose_that_type_for_selection()
    {
        IType type1 = type("type1");
        IType type2 = type("type2");

        MemberContentProvider contentProvider = new MemberContentProvider(Arrays.asList(type2, type1), type2);

        assertEquals(Arrays.asList("type1", "type2"), namesOf(contentProvider.getElements(null)));
        assertFalse(contentProvider.getDefaultSelection().isEmpty());
        assertEquals(type2, ((StructuredSelection) contentProvider.getDefaultSelection()).getFirstElement());
    }

    @Test
    public void getDefaultSelection_should_return_null_when_no_member_and_no_type_are_given()
    {
        assertNull(new MemberContentProvider(types, methods, null).getDefaultSelection());
        assertNull(new MemberContentProvider(types, (IType) null).getDefaultSelection());
    }

    @Test
    public void getDefaultSelection_should_return_first_element_when_no_member_is_proposed()
    {
        IType type1 = type("type1");
        IType type2 = type("type2");
        types.add(type2);
        types.add(type1);

        MemberContentProvider contentProvider = new MemberContentProvider(types, methods, null);

        assertEquals(type1, ((StructuredSelection) contentProvider.getDefaultSelection()).getFirstElement());
    }

    @Test
    public void getDefaultSelection_should_return_first_method_of_first_type_when_it_has_one()
    {
        IType type1 = type("type1");
        IType type2 = type("type2");
        types.add(type2);
        types.add(type1);
        methods.add(mockMethod(type2, "method2A"));
        IMethod method1A = mockMethod(type1, "method1A");
        methods.add(method1A);
        methods.add(mockMethod(type1, "method1B"));

        MemberContentProvider contentProvider = new MemberContentProvider(types, methods, null);

        assertEquals(method1A, ((StructuredSelection) contentProvider.getDefaultSelection()).getFirstElement());
    }

    @Test
    public void getParent_should_return_null_for_type_and_declaring_type_for_method()
    {
        IType type1 = type("type1");
        IMethod method = mockMethod(type1, "method1A");

        MemberContentProvider contentProvider = new MemberContentProvider(Arrays.asList(type1), Arrays.asList(method), method);

        assertNull(contentProvider.getParent(type1));
        assertEquals(type1, contentProvider.getParent(method));
    }

    @Test
    public void hasChildren_should_return_true_only_for_types_having_methods()
    {
        IType typeWithMethods = type("type1");
        IType typeWithoutMethods = type("type2");
        IMethod method = mockMethod(typeWithMethods, "method1A");

        MemberContentProvider contentProvider = new MemberContentProvider(Arrays.asList(typeWithMethods, typeWithoutMethods), Arrays.asList(method), null);

        assertTrue(contentProvider.hasChildren(typeWithMethods));
        assertFalse(contentProvider.hasChildren(typeWithoutMethods));
        assertFalse(contentProvider.hasChildren(method));
    }

    @Test
    public void dispose_and_inputChanged_should_do_nothing()
    {
        MemberContentProvider contentProvider = new MemberContentProvider(types, methods, null);

        contentProvider.dispose();
        contentProvider.inputChanged(null, null, null);
    }

    @Test
    public void withAction_should_append_separator_and_action_to_elements()
    {
        IType type1 = type("type1");
        types.add(type1);

        MemberContentProvider contentProvider = new MemberContentProvider(types, methods, null);
        TreeActionElement<?> action = mock(TreeActionElement.class);
        MemberContentProvider returned = contentProvider.withAction(action);

        assertSame(contentProvider, returned);

        Object[] elements = contentProvider.getElements(null);
        assertEquals(3, elements.length);
        assertEquals(type1, elements[0]);
        assertTrue(elements[1] instanceof SeparatorElement);
        assertSame(action, elements[2]);
    }

    @Test
    public void withAction_should_not_add_two_separators_when_called_twice()
    {
        MemberContentProvider contentProvider = new MemberContentProvider(types, methods, null);

        TreeActionElement<?> action1 = mock(TreeActionElement.class);
        TreeActionElement<?> action2 = mock(TreeActionElement.class);
        contentProvider.withAction(action1).withAction(action2);

        Object[] elements = contentProvider.getElements(null);
        assertEquals(3, elements.length);
        assertSame(action1, elements[0]);
        assertTrue(elements[1] instanceof SeparatorElement);
        assertSame(action2, elements[2]);
    }
}
