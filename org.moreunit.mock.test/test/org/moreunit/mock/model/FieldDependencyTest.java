package org.moreunit.mock.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class FieldDependencyTest
{
    @Test
    public void should_create_field_dependency_with_field_name()
    {
        FieldDependency dep = new FieldDependency("com.example.MyService", "myServiceField", "myService");
        assertEquals("com.example.MyService", dep.fullyQualifiedClassName);
        assertEquals("myService", dep.name);
        assertEquals("myServiceField", dep.fieldName);
    }

    @Test
    public void should_create_field_dependency_with_type_parameters()
    {
        FieldDependency dep = new FieldDependency("java.util.List", "listField", "list",
                Arrays.asList(new TypeParameter("java.lang.String")));
        assertEquals(1, dep.typeParameters.size());
        assertEquals("listField", dep.fieldName);
    }

    @Test
    public void should_inherit_dependency_equality()
    {
        FieldDependency d1 = new FieldDependency("com.example.Foo", "fooField", "foo");
        FieldDependency d2 = new FieldDependency("com.example.Foo", "fooField", "foo");

        assertEquals(d1, d2);
        assertEquals(d1.hashCode(), d2.hashCode());
    }

    @Test
    public void should_include_field_name_in_toString()
    {
        FieldDependency dep = new FieldDependency("com.example.Foo", "fooField", "foo");
        String str = dep.toString();
        assertNotNull(str);
        // FieldDependency extends Dependency which prints name
        assert(str.contains("foo"));
    }

    @Test
    public void should_extend_dependency()
    {
        FieldDependency dep = new FieldDependency("com.example.Foo", "fooField", "foo");
        assertNotNull(dep.annotations);
        assertNotNull(dep.typeParameters);
    }
}
