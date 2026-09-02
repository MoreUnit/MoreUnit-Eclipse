package org.moreunit.mock.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

public class DependencyTest
{
    @Test
    public void should_create_dependency_with_fully_qualified_name_and_name()
    {
        Dependency dep = new Dependency("com.example.MyService", "myService");
        assertEquals("com.example.MyService", dep.fullyQualifiedClassName);
        assertEquals("myService", dep.name);
    }

    @Test
    public void should_create_dependency_with_type_parameters()
    {
        Dependency dep = new Dependency("java.util.List", "list",
                Arrays.asList(new TypeParameter("java.lang.String")));
        assertEquals(1, dep.typeParameters.size());
    }

    @Test
    public void should_reject_null_fully_qualified_name()
    {
        assertThrows(NullPointerException.class, () -> new Dependency(null, "name"));
    }

    @Test
    public void should_reject_empty_fully_qualified_name()
    {
        assertThrows(IllegalArgumentException.class, () -> new Dependency("", "name"));
    }

    @Test
    public void should_reject_null_name()
    {
        assertThrows(NullPointerException.class, () -> new Dependency("com.example.Foo", null));
    }

    @Test
    public void should_reject_empty_name()
    {
        assertThrows(IllegalArgumentException.class, () -> new Dependency("com.example.Foo", ""));
    }

    @Test
    public void should_be_equal_when_fqn_and_name_match()
    {
        Dependency d1 = new Dependency("com.example.Foo", "foo");
        Dependency d2 = new Dependency("com.example.Foo", "foo");
        assertEquals(d1, d2);
        assertEquals(d1.hashCode(), d2.hashCode());
    }

    @Test
    public void should_not_be_equal_when_fqn_differs()
    {
        Dependency d1 = new Dependency("com.example.Foo", "foo");
        Dependency d2 = new Dependency("com.example.Bar", "foo");
        assertNotEquals(d1, d2);
    }

    @Test
    public void should_not_be_equal_when_name_differs()
    {
        Dependency d1 = new Dependency("com.example.Foo", "foo1");
        Dependency d2 = new Dependency("com.example.Foo", "foo2");
        assertNotEquals(d1, d2);
    }

    @Test
    public void should_not_be_equal_to_null()
    {
        Dependency d = new Dependency("com.example.Foo", "foo");
        assertNotEquals(null, d);
    }

    @Test
    public void should_not_be_equal_to_non_dependency()
    {
        Dependency d = new Dependency("com.example.Foo", "foo");
        assertNotEquals("com.example.Foo", d);
    }

    @Test
    public void should_compare_by_name_using_collator()
    {
        Dependency d1 = new Dependency("com.example.A", "alpha");
        Dependency d2 = new Dependency("com.example.B", "beta");
        assertEquals(-1, d1.compareTo(d2));
    }

    @Test
    public void should_include_name_in_toString()
    {
        Dependency d = new Dependency("com.example.Foo", "foo");
        String str = d.toString();
        assertNotNull(str);
        assert(str.contains("foo"));
    }

    @Test
    public void should_extend_type_use()
    {
        Dependency d = new Dependency("com.example.Foo", "foo");
        // Dependency extends TypeUse, so it should have annotations and typeParameters
        assertNotNull(d.annotations);
        assertNotNull(d.typeParameters);
    }

    @Test
    public void should_compute_hash_code_even_when_fully_qualified_name_and_name_are_null() throws Exception
    {
        // defensive branches: the constructor prevents null values, but the
        // null-guarded hash code must still be computed correctly
        Dependency dep = new Dependency("com.example.Foo", "foo");
        setField(dep, "fullyQualifiedClassName", null);
        setField(dep, "name", null);

        assertEquals(31 * 31, dep.hashCode());
    }

    @Test
    public void should_compute_hash_code_even_when_only_name_is_null() throws Exception
    {
        Dependency dep = new Dependency("com.example.Foo", "foo");
        setField(dep, "name", null);

        assertEquals(31 * (31 + "com.example.Foo".hashCode()), dep.hashCode());
    }

    @Test
    public void should_not_be_equal_when_null_fully_qualified_name_is_compared_with_non_null_one() throws Exception
    {
        Dependency depWithNulls = new Dependency("com.example.Foo", "foo");
        setField(depWithNulls, "fullyQualifiedClassName", null);
        setField(depWithNulls, "name", null);

        Dependency dep = new Dependency("com.example.Foo", "foo");

        assertFalse(depWithNulls.equals(dep));
        assertFalse(dep.equals(depWithNulls));
    }

    @Test
    public void should_be_equal_when_both_fully_qualified_name_and_name_are_null() throws Exception
    {
        Dependency dep1 = new Dependency("com.example.Foo", "foo");
        setField(dep1, "fullyQualifiedClassName", null);
        setField(dep1, "name", null);

        Dependency dep2 = new Dependency("com.example.Bar", "bar");
        setField(dep2, "fullyQualifiedClassName", null);
        setField(dep2, "name", null);

        assertTrue(dep1.equals(dep2));
    }

    @Test
    public void should_not_be_equal_when_null_name_is_compared_with_non_null_one() throws Exception
    {
        Dependency depWithNullName = new Dependency("com.example.Foo", "foo");
        setField(depWithNullName, "name", null);

        Dependency dep = new Dependency("com.example.Foo", "foo");

        assertFalse(depWithNullName.equals(dep));
        assertFalse(dep.equals(depWithNullName));
    }

    @Test
    public void should_be_equal_to_itself()
    {
        Dependency d = new Dependency("com.example.Foo", "foo");
        assertTrue(d.equals(d));
    }

    @Test
    public void should_not_be_equal_to_null_even_when_compared_directly()
    {
        Dependency d = new Dependency("com.example.Foo", "foo");
        assertFalse(d.equals(null));
    }

    @Test
    public void should_not_be_equal_to_object_of_different_class_even_when_compared_directly()
    {
        Dependency d = new Dependency("com.example.Foo", "foo");
        assertFalse(d.equals(new Object()));
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception
    {
        Class<?> type = target.getClass();
        while (type != null)
        {
            try
            {
                java.lang.reflect.Field field = type.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(target, value);
                return;
            }
            catch (NoSuchFieldException e)
            {
                type = type.getSuperclass();
            }
        }
        throw new NoSuchFieldException(fieldName);
    }
}
