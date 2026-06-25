package org.moreunit.mock.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        TypeUse<?> use = new TypeUse<>("com.example.MyClass");
        assertEquals("com.example.MyClass", use.fullyQualifiedClassName);
        assertTrue(use.annotations.isEmpty());
        assertTrue(use.typeParameters.isEmpty());
    }

    @Test
    public void should_add_single_annotation()
    {
        TypeUse<?> use = new TypeUse<>("com.example.MyClass");
        TypeUse<?> result = use.withAnnotations("org.mockito.Mock");

        assertSame(use, result);
        assertEquals(1, use.annotations.size());
        assertEquals("org.mockito.Mock", use.annotations.get(0).fullyQualifiedClassName);
    }

    @Test
    public void should_add_multiple_annotations()
    {
        TypeUse<?> use = new TypeUse<>("com.example.MyClass");
        use.withAnnotations("org.mockito.Mock", "org.junit.jupiter.api.BeforeEach");

        assertEquals(2, use.annotations.size());
        assertEquals("org.mockito.Mock", use.annotations.get(0).fullyQualifiedClassName);
        assertEquals("org.junit.jupiter.api.BeforeEach", use.annotations.get(1).fullyQualifiedClassName);
    }

    @Test
    public void should_add_annotations_from_collection()
    {
        TypeUse<?> use = new TypeUse<>("com.example.MyClass");
        use.withAnnotations(Arrays.asList("a.B", "c.D"));

        assertEquals(2, use.annotations.size());
    }

    @Test
    public void should_add_empty_annotations()
    {
        TypeUse<?> use = new TypeUse<>("com.example.MyClass");
        use.withAnnotations(Collections.<String>emptyList());

        assertTrue(use.annotations.isEmpty());
    }

    @Test
    public void should_add_type_parameters()
    {
        TypeUse<?> use = new TypeUse<>("java.util.List");
        TypeParameter param = new TypeParameter("java.lang.String");
        use.withTypeParameters(param);

        assertEquals(1, use.typeParameters.size());
        assertSame(param, use.typeParameters.get(0));
    }

    @Test
    public void should_add_multiple_type_parameters()
    {
        TypeUse<?> use = new TypeUse<>("java.util.Map");
        TypeParameter p1 = new TypeParameter("java.lang.String");
        TypeParameter p2 = new TypeParameter("java.lang.Integer");
        use.withTypeParameters(p1, p2);

        assertEquals(2, use.typeParameters.size());
    }

    @Test
    public void should_be_equal_when_class_and_type_parameters_match()
    {
        TypeUse<?> use1 = new TypeUse<>("java.util.List").withTypeParameters(new TypeParameter("java.lang.String"));
        TypeUse<?> use2 = new TypeUse<>("java.util.List").withTypeParameters(new TypeParameter("java.lang.String"));

        assertEquals(use1, use2);
        assertEquals(use1.hashCode(), use2.hashCode());
    }

    @Test
    public void should_not_be_equal_when_type_parameters_differ()
    {
        TypeUse<?> use1 = new TypeUse<>("java.util.List").withTypeParameters(new TypeParameter("java.lang.String"));
        TypeUse<?> use2 = new TypeUse<>("java.util.List").withTypeParameters(new TypeParameter("java.lang.Integer"));

        org.junit.jupiter.api.Assertions.assertNotEquals(use1, use2);
    }

    @Test
    public void should_not_be_equal_when_annotations_differ()
    {
        TypeUse<?> use1 = new TypeUse<>("com.example.Foo").withAnnotations("a.B");
        TypeUse<?> use2 = new TypeUse<>("com.example.Foo").withAnnotations("c.D");

        org.junit.jupiter.api.Assertions.assertNotEquals(use1, use2);
    }

    @Test
    public void should_include_details_in_toString()
    {
        TypeUse<?> use = new TypeUse<>("com.example.Foo")
                .withAnnotations("a.B")
                .withTypeParameters(new TypeParameter("java.lang.String"));
        String str = use.toString();

        assertNotNull(str);
        assert(str.contains("com.example.Foo"));
        assert(str.contains("a.B"));
    }

    @Test
    public void should_support_chaining_fluent_api()
    {
        TypeUse<?> use = new TypeUse<>("com.example.Foo")
                .withAnnotations("a.B", "c.D")
                .withTypeParameters(new TypeParameter("java.lang.String"), new TypeParameter("java.lang.Integer"));

        assertEquals(2, use.annotations.size());
        assertEquals(2, use.typeParameters.size());
    }
}
