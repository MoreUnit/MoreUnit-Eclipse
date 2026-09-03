package org.moreunit.mock.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

public class TypeUseTest
{
    @Test
    public void should_create_type_use_with_class_name()
    {
        final TypeUse<?> use = new TypeUse<>("com.example.MyClass");
        assertEquals("com.example.MyClass", use.fullyQualifiedClassName);
        assertTrue(use.annotations.isEmpty());
        assertTrue(use.typeParameters.isEmpty());
    }

    @Test
    public void should_add_single_annotation()
    {
        final TypeUse<?> use = new TypeUse<>("com.example.MyClass");
        final TypeUse<?> result = use.withAnnotations("org.mockito.Mock");

        assertSame(use, result);
        assertEquals(1, use.annotations.size());
        assertEquals("org.mockito.Mock", use.annotations.get(0).fullyQualifiedClassName);
    }

    @Test
    public void should_add_multiple_annotations()
    {
        final TypeUse<?> use = new TypeUse<>("com.example.MyClass");
        use.withAnnotations("org.mockito.Mock", "org.junit.jupiter.api.BeforeEach");

        assertEquals(2, use.annotations.size());
        assertEquals("org.mockito.Mock", use.annotations.get(0).fullyQualifiedClassName);
        assertEquals("org.junit.jupiter.api.BeforeEach", use.annotations.get(1).fullyQualifiedClassName);
    }

    @Test
    public void should_add_annotations_from_collection()
    {
        final TypeUse<?> use = new TypeUse<>("com.example.MyClass");
        use.withAnnotations(Arrays.asList("a.B", "c.D"));

        assertEquals(2, use.annotations.size());
    }

    @Test
    public void should_add_empty_annotations()
    {
        final TypeUse<?> use = new TypeUse<>("com.example.MyClass");
        use.withAnnotations(Collections.<String>emptyList());

        assertTrue(use.annotations.isEmpty());
    }

    @Test
    public void should_add_type_parameters()
    {
        final TypeUse<?> use = new TypeUse<>("java.util.List");
        final TypeParameter param = new TypeParameter("java.lang.String");
        use.withTypeParameters(param);

        assertEquals(1, use.typeParameters.size());
        assertSame(param, use.typeParameters.get(0));
    }

    @Test
    public void should_add_multiple_type_parameters()
    {
        final TypeUse<?> use = new TypeUse<>("java.util.Map");
        final TypeParameter p1 = new TypeParameter("java.lang.String");
        final TypeParameter p2 = new TypeParameter("java.lang.Integer");
        use.withTypeParameters(p1, p2);

        assertEquals(2, use.typeParameters.size());
    }

    @Test
    public void should_be_equal_when_class_and_type_parameters_match()
    {
        final TypeUse<?> use1 = new TypeUse<>("java.util.List").withTypeParameters(new TypeParameter("java.lang.String"));
        final TypeUse<?> use2 = new TypeUse<>("java.util.List").withTypeParameters(new TypeParameter("java.lang.String"));

        assertEquals(use1, use2);
        assertEquals(use1.hashCode(), use2.hashCode());
    }

    @Test
    public void should_not_be_equal_when_type_parameters_differ()
    {
        final TypeUse<?> use1 = new TypeUse<>("java.util.List").withTypeParameters(new TypeParameter("java.lang.String"));
        final TypeUse<?> use2 = new TypeUse<>("java.util.List").withTypeParameters(new TypeParameter("java.lang.Integer"));

        org.junit.jupiter.api.Assertions.assertNotEquals(use1, use2);
    }

    @Test
    public void should_not_be_equal_when_annotations_differ()
    {
        final TypeUse<?> use1 = new TypeUse<>("com.example.Foo").withAnnotations("a.B");
        final TypeUse<?> use2 = new TypeUse<>("com.example.Foo").withAnnotations("c.D");

        org.junit.jupiter.api.Assertions.assertNotEquals(use1, use2);
    }

    @Test
    public void should_include_details_in_toString()
    {
        final TypeUse<?> use = new TypeUse<>("com.example.Foo")
                .withAnnotations("a.B")
                .withTypeParameters(new TypeParameter("java.lang.String"));
        final String str = use.toString();

        assertNotNull(str);
        assert(str.contains("com.example.Foo"));
        assert(str.contains("a.B"));
    }

    @Test
    public void should_support_chaining_fluent_api()
    {
        final TypeUse<?> use = new TypeUse<>("com.example.Foo")
                .withAnnotations("a.B", "c.D")
                .withTypeParameters(new TypeParameter("java.lang.String"), new TypeParameter("java.lang.Integer"));

        assertEquals(2, use.annotations.size());
        assertEquals(2, use.typeParameters.size());
    }

    @Test
    public void should_compute_hash_code_even_when_annotations_and_type_parameters_are_null() throws Exception
    {
        // defensive branches: the fields are always initialized in production,
        // but the null-guarded hash code must still be computed correctly
        final TypeUse<?> use = new TypeUse<>("java.util.List");
        setField(use, "typeParameters", null);
        setField(use, "annotations", null);

        final int expected = 31 * (31 * (31 + "java.util.List".hashCode()));
        assertEquals(expected, use.hashCode());
    }

    @Test
    public void should_not_be_equal_when_null_type_parameters_are_compared_with_non_null_ones() throws Exception
    {
        final TypeUse<?> useWithNulls = new TypeUse<>("java.util.List");
        setField(useWithNulls, "typeParameters", null);
        setField(useWithNulls, "annotations", null);

        final TypeUse<?> use = new TypeUse<>("java.util.List").withTypeParameters(new TypeParameter("java.lang.String"));

        assertFalse(useWithNulls.equals(use));
        assertFalse(use.equals(useWithNulls));
    }

    @Test
    public void should_not_be_equal_when_null_annotations_are_compared_with_non_null_ones() throws Exception
    {
        final TypeUse<?> useWithNullAnnotations = new TypeUse<>("com.example.Foo");
        setField(useWithNullAnnotations, "annotations", null);

        final TypeUse<?> use = new TypeUse<>("com.example.Foo").withAnnotations("a.B");

        assertFalse(useWithNullAnnotations.equals(use));
        assertFalse(use.equals(useWithNullAnnotations));
    }

    @Test
    public void should_be_equal_when_both_annotations_and_type_parameters_are_null() throws Exception
    {
        final TypeUse<?> use1 = new TypeUse<>("java.util.List");
        setField(use1, "typeParameters", null);
        setField(use1, "annotations", null);

        final TypeUse<?> use2 = new TypeUse<>("java.util.List");
        setField(use2, "typeParameters", null);
        setField(use2, "annotations", null);

        assertTrue(use1.equals(use2));
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception
    {
        Class<?> type = target.getClass();
        while (type != null)
        {
            try
            {
                final java.lang.reflect.Field field = type.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(target, value);
                return;
            }
            catch (final NoSuchFieldException e)
            {
                type = type.getSuperclass();
            }
        }
        throw new NoSuchFieldException(fieldName);
    }
}
