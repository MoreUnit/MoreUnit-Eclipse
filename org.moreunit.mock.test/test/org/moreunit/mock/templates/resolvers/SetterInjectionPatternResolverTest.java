package org.moreunit.mock.templates.resolvers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.moreunit.mock.dependencies.Dependencies;
import org.moreunit.mock.model.SetterDependency;
import org.moreunit.mock.templates.MockingContext;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class SetterInjectionPatternResolverTest
{
    @Mock
    private MockingContext context;

    private Dependencies dependencies;
    private SetterInjectionPatternResolver resolver;

    @BeforeEach
    public void createResolver() throws Exception
    {
        dependencies = new Dependencies(null, null, null);
        when(context.dependenciesToMock()).thenReturn(dependencies);
        resolver = new SetterInjectionPatternResolver(context);
    }

    @Test
    public void should_return_unmodified_pattern_when_does_not_match() throws Exception
    {
        assertThat(resolver.resolve("does not match")).isEqualTo("does not match");
    }

    @Test
    public void should_return_empty_string_when_there_are_no_dependencies_to_inject_by_setter() throws Exception
    {
        assertThat(resolver.resolve("a ${:setDependency(objectUnderTest, dependency)} c")).isEqualTo("");
    }

    @Test
    public void should_assign_one_setter_dependency() throws Exception
    {
        // given
        dependencies.injectableBySetter().add(new SetterDependency("pack.age.Foo", "setFoo"));

        // when
        String resolvedPattern = resolver.resolve("pre ${:setDependency(objectUnderTest, dependency)} post");

        // then
        assertThat(resolvedPattern).isEqualTo("pre ${objectUnderTest}.setFoo(foo) post");
    }

    @Test
    public void should_assign_several_setter_dependencies() throws Exception
    {
        // given
        dependencies.injectableBySetter().add(new SetterDependency("pack.age.Foo", "setFoo"));
        dependencies.injectableBySetter().add(new SetterDependency("some.where.Thing", "setBar"));
        dependencies.injectableBySetter().add(new SetterDependency("BlobClass", "setABlob"));

        // when
        String resolvedPattern = resolver.resolve("pre ${:setDependency(objectUnderTest, dependency)} post");

        // then
        assertThat(resolvedPattern).isEqualTo("pre ${objectUnderTest}.setFoo(foo) post" +
                                              "pre ${objectUnderTest}.setBar(bar) post" +
                                              "pre ${objectUnderTest}.setABlob(aBlob) post");
    }
}
