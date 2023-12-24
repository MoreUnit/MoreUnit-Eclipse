package org.moreunit.mock.templates.resolvers;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.moreunit.mock.dependencies.Dependencies;
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
    public void should_add_wildcard_type_parameter() throws Exception
    {
        // given
        dependencies.add(new Dependency("java.util.Iterable", "iter", asList(TypeParameter.wildcard())));

        // then
        assertThat(resolver.resolve("pre ${dependencyType} post"))
                .isEqualTo("pre ${IterableType:newType(java.util.Iterable)}<?> post");
    }

    @Test
    public void should_add_wildcard_type_parameter_with_extends_bound() throws Exception
    {
        // given
        dependencies.add(new Dependency("java.util.Iterable", "iter", asList(TypeParameter.extending("java.lang.Float"))));

        // then
        assertThat(resolver.resolve("pre ${dependencyType} post"))
                .isEqualTo("pre ${IterableType:newType(java.util.Iterable)}<? extends ${FloatType:newType(java.lang.Float)}> post");
    }

    @Test
    public void should_add_wildcard_type_parameter_with_super_bound() throws Exception
    {
        // given
        dependencies.add(new Dependency("java.util.Iterable", "iter", asList(TypeParameter.superOf("java.lang.Float"))));

        // then
        assertThat(resolver.resolve("pre ${dependencyType} post"))
                .isEqualTo("pre ${IterableType:newType(java.util.Iterable)}<? super ${FloatType:newType(java.lang.Float)}> post");
    }

    @Test
    public void should_add_wildcard_type_parameters_with_nested_params() throws Exception
    {
        // given
        dependencies.add(new Dependency("java.util.Map", "aMap",
                                        asList(TypeParameter.extending("java.util.List").withTypeParameters(TypeParameter.superOf("java.lang.Double")),
                                               TypeParameter.superOf("java.util.Set").withTypeParameters(TypeParameter.wildcard()))));

        // then
        assertThat(resolver.resolve("pre ${dependencyType} post"))
                .isEqualTo("pre ${MapType:newType(java.util.Map)}<"
                           + "? extends ${ListType:newType(java.util.List)}<? super ${DoubleType:newType(java.lang.Double)}>"
                           + ",? super ${SetType:newType(java.util.Set)}<?>"
                           + "> post");
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
        dependencies.add(new Dependency("java.util.Map", "aMap", asList(new TypeParameter("java.util.List").withTypeParameters(new TypeParameter("java.lang.Double")),
                                                                        new TypeParameter("java.util.Set").withTypeParameters(new TypeParameter("java.lang.String")))));

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

    @Test
    public void should_add_type_parameter_with_type_annotation() throws Exception
    {
        // given
        dependencies.add(new Dependency("java.util.Iterable", "iter",
                                        asList(new TypeParameter("java.lang.String")
                                                .withAnnotations("checkers.interning.quals.NonNull"))));

        // then
        assertThat(resolver.resolve("pre ${dependencyType} post"))
                .isEqualTo("pre ${IterableType:newType(java.util.Iterable)}<@${NonNullType:newType(checkers.interning.quals.NonNull)} ${StringType:newType(java.lang.String)}> post");
    }

    @Test
    public void should_add_type_parameter_with_type_annotations() throws Exception
    {
        // given
        dependencies.add(new Dependency("java.util.Iterable", "iter",
                                        asList(new TypeParameter("java.lang.String")
                                                .withAnnotations("checkers.interning.quals.Interned", "checkers.interning.quals.NonNull"))));

        // then
        assertThat(resolver.resolve("pre ${dependencyType} post"))
                .isEqualTo("pre ${IterableType:newType(java.util.Iterable)}<@${InternedType:newType(checkers.interning.quals.Interned)} @${NonNullType:newType(checkers.interning.quals.NonNull)} ${StringType:newType(java.lang.String)}> post");
    }

    @Test
    public void should_add_type_parameter_with_wildcard_type_annotations() throws Exception
    {
        // given
        dependencies.add(new Dependency("java.util.Iterable", "iter",
                                        asList(TypeParameter.extending("java.lang.String")
                                                .withAnnotations("checkers.interning.quals.NonNull")
                                                .withBaseTypeAnnotations("checkers.interning.quals.ReadOnly"))));

        // then
        assertThat(resolver.resolve("pre ${dependencyType} post"))
                .isEqualTo("pre ${IterableType:newType(java.util.Iterable)}<@${NonNullType:newType(checkers.interning.quals.NonNull)} ? extends @${ReadOnlyType:newType(checkers.interning.quals.ReadOnly)} ${StringType:newType(java.lang.String)}> post");
    }
}
