package org.moreunit.mock.templates.resolvers;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.moreunit.mock.model.Dependencies;
import org.moreunit.mock.model.Dependency;
import org.moreunit.mock.templates.MockingContext;

@RunWith(MockitoJUnitRunner.class)
public class FieldInjectionPatternResolverTest
{
    @Mock
    private MockingContext context;

    private Dependencies dependencies;
    private FieldInjectionPatternResolver resolver;

    @Before
    public void createResolver() throws Exception
    {
        dependencies = new Dependencies(null, null);
        when(context.dependenciesToMock()).thenReturn(dependencies);
        resolver = new FieldInjectionPatternResolver(context);
    }

    @Test
    public void should_return_unmodified_pattern_when_does_not_match() throws Exception
    {
        assertThat(resolver.resolve("does not match")).isEqualTo("does not match");
    }

    @Test
    public void should_return_empty_string_when_there_are_no_dependencies_to_inject_by_field() throws Exception
    {
        assertThat(resolver.resolve("a ${:assignDependency(objectUnderTest, dependency)} c")).isEqualTo("");
    }

    @Test
    public void should_assign_one_field_dependency() throws Exception
    {
        // given
        dependencies.fieldDependencies.add(new Dependency("pack.age.Foo", "foo"));

        // when
        String resolvedPattern = resolver.resolve("pre ${:assignDependency(objectUnderTest, dependency)} post");

        // then
        assertThat(resolvedPattern).isEqualTo("pre ${objectUnderTest}.foo = foo post");
    }

    @Test
    public void should_assign_several_field_dependencies() throws Exception
    {
        // given
        dependencies.fieldDependencies.add(new Dependency("pack.age.Foo", "foo"));
        dependencies.fieldDependencies.add(new Dependency("some.where.Thing", "bar"));
        dependencies.fieldDependencies.add(new Dependency("BlobClass", "aBlob"));

        // when
        String resolvedPattern = resolver.resolve("pre ${:assignDependency(objectUnderTest, dependency)} post");

        // then
        assertThat(resolvedPattern).isEqualTo("pre ${objectUnderTest}.foo = foo post" +
                                              "pre ${objectUnderTest}.bar = bar post" +
                                              "pre ${objectUnderTest}.aBlob = aBlob post");
    }
}
