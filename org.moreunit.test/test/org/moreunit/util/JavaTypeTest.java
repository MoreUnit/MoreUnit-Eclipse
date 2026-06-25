package org.moreunit.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class JavaTypeTest
{
    @Test
    public void should_parse_fully_qualified_name()
    {
        JavaType javaType = new JavaType("org.example.SomeClass");

        assertEquals(javaType.getQualifiedName(), "org.example.SomeClass");
        assertEquals(javaType.getSimpleName(), "SomeClass");
        assertEquals(javaType.getQualifier(), "org.example");
        assertEquals(javaType.getQualifierWithFinalDot(), "org.example.");
    }

    @Test
    public void should_parse_default_package_name()
    {
        JavaType javaType = new JavaType("SomeClass");

        assertEquals(javaType.getQualifiedName(), "SomeClass");
        assertEquals(javaType.getSimpleName(), "SomeClass");
        assertEquals(javaType.getQualifier(), "");
        assertEquals(javaType.getQualifierWithFinalDot(), "");
    }

    @Test
    public void should_construct_from_simple_name_and_package()
    {
        JavaType javaType = new JavaType("SomeClass", "org.example");

        assertEquals(javaType.getQualifiedName(), "org.example.SomeClass");
        assertEquals(javaType.getSimpleName(), "SomeClass");
        assertEquals(javaType.getQualifier(), "org.example");
        assertEquals(javaType.getQualifierWithFinalDot(), "org.example.");
    }

    @Test
    public void should_construct_from_simple_name_and_empty_package()
    {
        JavaType javaType = new JavaType("SomeClass", "");

        assertEquals(javaType.getQualifiedName(), ".SomeClass");
        assertEquals(javaType.getSimpleName(), "SomeClass");
        assertEquals(javaType.getQualifier(), "");
        assertEquals(javaType.getQualifierWithFinalDot(), "");
    }
}
