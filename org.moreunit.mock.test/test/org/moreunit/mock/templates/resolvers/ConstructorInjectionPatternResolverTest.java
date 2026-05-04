package org.moreunit.mock.templates.resolvers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.moreunit.mock.dependencies.Dependencies;
import org.moreunit.mock.model.Dependency;
import org.moreunit.mock.templates.MockingContext;

@ExtendWith(MockitoExtension.class)
public class ConstructorInjectionPatternResolverTest
{
    @Mock
    private MockingContext context;

    private Dependencies dependencies;
    private ConstructorInjectionPatternResolver resolver;

    @BeforeEach
    public void createResolver() throws Exception
    {
        dependencies = new Dependencies(null, null, null);
        when(context.dependenciesToMock()).thenReturn(dependencies);
        resolver = new ConstructorInjectionPatternResolver(context);
    }

    @Test
    public void should_return_unmodified_pattern_when_does_not_match() throws Exception
    {
        assertThat(resolver.resolve("does not match")).isEqualTo("does not match");
    }

    @Test
    public void should_call_constructor_when_no_dependency() throws Exception
    {
        // when
        String resolvedPattern = resolver.resolve("pre ${:constructWithDependencies(objectUnderTest, dependency)} post");

        // then
        assertThat(resolvedPattern).isEqualTo("pre new ${objectUnderTestType}() post");
    }

    @Test
    public void should_call_constructor_whith_one_dependency() throws Exception
    {
        // given
        dependencies.injectableByConstructor().add(new Dependency("pack.age.Foo", "foo"));

        // when
        String resolvedPattern = resolver.resolve("pre ${:constructWithDependencies(objectUnderTest, dependency)} post");

        // then
        assertThat(resolvedPattern).isEqualTo("pre new ${objectUnderTestType}(foo) post");
    }

    @Test
    public void should_call_constructor_whith_several_dependencies() throws Exception
    {
        // given
        dependencies.injectableByConstructor().add(new Dependency("pack.age.Foo", "foo"));
        dependencies.injectableByConstructor().add(new Dependency("some.where.Thing", "bar"));
        dependencies.injectableByConstructor().add(new Dependency("BlobClass", "aBlob"));

        // when
        String resolvedPattern = resolver.resolve("pre ${:constructWithDependencies(objectUnderTest, dependency)} post");

        // then
        assertThat(resolvedPattern).isEqualTo("pre new ${objectUnderTestType}(foo,bar,aBlob) post");
    }
}
