package org.moreunit.mock.templates.resolvers;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.moreunit.mock.elements.Dependencies;
import org.moreunit.mock.model.Dependency;
import org.moreunit.mock.model.TypeParameter;
import org.moreunit.mock.templates.MockingContext;

@RunWith(MockitoJUnitRunner.class)
public class DependencyPatternsResolverTest
{
    @Mock
    private MockingContext context;

    private Dependencies dependencies;
    private DependencyPatternsResolver resolver;

    @Before
    public void createResolver() throws Exception
    {
        dependencies = new Dependencies(null, null, null);
        when(context.dependenciesToMock()).thenReturn(dependencies);
        resolver = new DependencyPatternsResolver(context);
    }

    @Test
    public void should_return_unmodified_pattern_when_does_not_match() throws Exception
    {
        assertThat(resolver.resolve("does not match")).isEqualTo("does not match");
    }

    @Test
    public void should_return_empty_string_when_there_are_no_dependencies() throws Exception
    {
        assertThat(resolver.resolve("a ${dependency} b ${dependencyType}${dependency} c")).isEqualTo("");
    }

    @Test
    public void should_resolve_dependency() throws Exception
    {
        // given
        dependencies.add(new Dependency("some.where.Foo", "bar"));

        // then
        assertThat(resolver.resolve("pre ${dependency} between ${dependency} post"))
                .isEqualTo("pre bar between bar post");
    }

    @Test
    public void should_resolve_dependencyType() throws Exception
    {
        // given
        dependencies.add(new Dependency("some.where.Foo", "bar"));

        // then
        assertThat(resolver.resolve("pre ${dependencyType} between ${dependencyType} post"))
                .isEqualTo("pre ${FooType:newType(some.where.Foo)} between ${FooType:newType(some.where.Foo)} post");
    }

    @Test
    public void should_resolve_dependency_and_dependencyType() throws Exception
    {
        // given
        dependencies.add(new Dependency("some.where.Foo", "bar"));

        // then
        assertThat(resolver.resolve("a ${dependencyType} b ${dependency} c ${dependencyType} d ${dependency} e"))
                .isEqualTo("a ${FooType:newType(some.where.Foo)} b bar c ${FooType:newType(some.where.Foo)} d bar e");
    }

    @Test
    public void should_reproduce_pattern_for_each_dependency() throws Exception
    {
        // given
        dependencies.add(new Dependency("pack.age.Foo", "foo"));
        dependencies.add(new Dependency("some.where.Thing", "bar"));
        dependencies.add(new Dependency("BlobClass", "aBlob"));

        // then
        assertThat(resolver.resolve("a ${dependencyType} b ${dependency} c"))
                .isEqualTo("a ${FooType:newType(pack.age.Foo)} b foo c" +
                           "a ${ThingType:newType(some.where.Thing)} b bar c" +
                           "a ${BlobClassType:newType(BlobClass)} b aBlob c");
    }

    @Test
    public void should_add_one_type_parameter() throws Exception
    {
        // given
        dependencies.add(new Dependency("java.util.Iterable", "iter", asList(new TypeParameter("java.lang.Float"))));

        // then
        assertThat(resolver.resolve("pre ${dependencyType} post"))
                .isEqualTo("pre ${IterableType:newType(java.util.Iterable)}<${FloatType:newType(java.lang.Float)}> post");
    }

    @Test
    public void should_add_several_type_parameters() throws Exception
    {
        // given
        dependencies.add(new Dependency("java.util.Map", "map", asList(new TypeParameter("java.lang.String"), new TypeParameter("java.lang.Float"))));

        // then
        assertThat(resolver.resolve("pre ${dependencyType} post"))
                .isEqualTo("pre ${MapType:newType(java.util.Map)}<${StringType:newType(java.lang.String)},${FloatType:newType(java.lang.Float)}> post");
    }

    @Test
    public void should_add_several_nested_type_parameters() throws Exception
    {
        // given
        dependencies.add(new Dependency("java.util.Map", "aMap", asList(new TypeParameter("java.util.List", asList(new TypeParameter("java.lang.Double"))),
                                                                        new TypeParameter("java.util.Set", asList(new TypeParameter("java.lang.String"))))));

        // then
        assertThat(resolver.resolve("pre ${dependencyType} post"))
                .isEqualTo("pre ${MapType:newType(java.util.Map)}<"
                           + "${ListType:newType(java.util.List)}<${DoubleType:newType(java.lang.Double)}>"
                           + ",${SetType:newType(java.util.Set)}<${StringType:newType(java.lang.String)}>"
                           + "> post");
    }

    @Test
    public void should_not_add_type_parameters_for_classes() throws Exception
    {
        // given
        dependencies.add(new Dependency("java.util.Iterable", "iter", asList(new TypeParameter("java.lang.Float"))));

        // then
        assertThat(resolver.resolve("pre ${dependencyType}.class post"))
                .isEqualTo("pre ${IterableType:newType(java.util.Iterable)}.class post");

        assertThat(resolver.resolve("pre ${dependencyType}  . class post"))
                .isEqualTo("pre ${IterableType:newType(java.util.Iterable)}.class post");
    }
}
