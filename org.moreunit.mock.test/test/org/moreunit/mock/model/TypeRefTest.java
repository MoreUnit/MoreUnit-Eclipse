package org.moreunit.mock.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TypeRefTest
{
    @Test
    public void should_extract_simple_class_name_from_fully_qualified_name()
    {
        final TypeRef<?> ref = new TypeRef<>("com.example.MyClass");
        assertEquals("com.example.MyClass", ref.fullyQualifiedClassName);
        assertEquals("MyClass", ref.simpleClassName);
    }

    @Test
    public void should_handle_class_in_default_package()
    {
        final TypeRef<?> ref = new TypeRef<>("MyClass");
        assertEquals("MyClass", ref.fullyQualifiedClassName);
        assertEquals("MyClass", ref.simpleClassName);
    }

    @Test
    public void should_handle_deeply_nested_package()
    {
        final TypeRef<?> ref = new TypeRef<>("a.b.c.d.e.F");
        assertEquals("F", ref.simpleClassName);
    }

    @Test
    public void should_reject_null_fully_qualified_name()
    {
        assertThrows(NullPointerException.class, () -> new TypeRef<>(null));
    }

    @Test
    public void should_be_equal_when_fully_qualified_names_are_equal()
    {
        final TypeRef<?> ref1 = new TypeRef<>("com.example.Foo");
        final TypeRef<?> ref2 = new TypeRef<>("com.example.Foo");
        assertEquals(ref1, ref2);
        assertEquals(ref1.hashCode(), ref2.hashCode());
    }

    @Test
    public void should_not_be_equal_when_fully_qualified_names_differ()
    {
        final TypeRef<?> ref1 = new TypeRef<>("com.example.Foo");
        final TypeRef<?> ref2 = new TypeRef<>("com.example.Bar");
        assertNotEquals(ref1, ref2);
    }

    @Test
    public void should_not_be_equal_to_null()
    {
        final TypeRef<?> ref = new TypeRef<>("com.example.Foo");
        assertNotEquals(null, ref);
    }

    @Test
    public void should_not_be_equal_to_object_of_different_class()
    {
        final TypeRef<?> ref = new TypeRef<>("com.example.Foo");
        assertNotEquals("com.example.Foo", ref);
    }

    @Test
    public void should_include_fully_qualified_name_in_toString()
    {
        final TypeRef<?> ref = new TypeRef<>("com.example.Foo");
        final String str = ref.toString();
        assertNotNull(str);
        assert(str.contains("com.example.Foo"));
    }

    @Test
    public void should_be_equal_to_itself()
    {
        final TypeRef<?> ref = new TypeRef<>("com.example.Foo");
        assertTrue(ref.equals(ref));
    }

    @Test
    public void should_not_be_equal_to_null_even_when_compared_directly()
    {
        final TypeRef<?> ref = new TypeRef<>("com.example.Foo");
        assertFalse(ref.equals(null));
    }

    @Test
    public void should_not_be_equal_to_object_of_different_class_even_when_compared_directly()
    {
        final TypeRef<?> ref = new TypeRef<>("com.example.Foo");
        assertFalse(ref.equals(new Object()));
    }
}
