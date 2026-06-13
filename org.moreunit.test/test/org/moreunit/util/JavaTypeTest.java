package org.moreunit.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class JavaTypeTest
{
    @Test
    public void should_parse_fully_qualified_name()
    {
        JavaType javaType = new JavaType("org.example.SomeClass");

        assertThat(javaType.getQualifiedName()).isEqualTo("org.example.SomeClass");
        assertThat(javaType.getSimpleName()).isEqualTo("SomeClass");
        assertThat(javaType.getQualifier()).isEqualTo("org.example");
        assertThat(javaType.getQualifierWithFinalDot()).isEqualTo("org.example.");
    }

    @Test
    public void should_parse_default_package_name()
    {
        JavaType javaType = new JavaType("SomeClass");

        assertThat(javaType.getQualifiedName()).isEqualTo("SomeClass");
        assertThat(javaType.getSimpleName()).isEqualTo("SomeClass");
        assertThat(javaType.getQualifier()).isEqualTo("");
        assertThat(javaType.getQualifierWithFinalDot()).isEqualTo("");
    }

    @Test
    public void should_construct_from_simple_name_and_package()
    {
        JavaType javaType = new JavaType("SomeClass", "org.example");

        assertThat(javaType.getQualifiedName()).isEqualTo("org.example.SomeClass");
        assertThat(javaType.getSimpleName()).isEqualTo("SomeClass");
        assertThat(javaType.getQualifier()).isEqualTo("org.example");
        assertThat(javaType.getQualifierWithFinalDot()).isEqualTo("org.example.");
    }

    @Test
    public void should_construct_from_simple_name_and_empty_package()
    {
        JavaType javaType = new JavaType("SomeClass", "");

        assertThat(javaType.getQualifiedName()).isEqualTo(".SomeClass");
        assertThat(javaType.getSimpleName()).isEqualTo("SomeClass");
        assertThat(javaType.getQualifier()).isEqualTo("");
        assertThat(javaType.getQualifierWithFinalDot()).isEqualTo("");
    }
}
