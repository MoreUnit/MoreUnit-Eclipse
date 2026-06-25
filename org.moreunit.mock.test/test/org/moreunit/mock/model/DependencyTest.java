package org.moreunit.mock.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
}
