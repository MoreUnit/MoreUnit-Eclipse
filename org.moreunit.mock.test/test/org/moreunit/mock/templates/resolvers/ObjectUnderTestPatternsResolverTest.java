package org.moreunit.mock.templates.resolvers;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.eclipse.jdt.core.IType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.moreunit.mock.dependencies.Dependencies;
import org.moreunit.mock.templates.MockingContext;
import org.moreunit.mock.templates.PatternResolver;

@RunWith(MockitoJUnitRunner.class)
public class ObjectUnderTestPatternsResolverTest
{
    @Mock
    private IType classUnderTest;

    private Dependencies dependencies;
    private MockingContext context;
    private ObjectUnderTestPatternsResolver resolver;

    @Before
    public void createResolver() throws Exception
    {
        when(classUnderTest.getFullyQualifiedName()).thenReturn("some.pack.age.SomeThing");
        when(classUnderTest.getElementName()).thenReturn("SomeThing");

        dependencies = new Dependencies(null, null, null);
        context = new MockingContext(dependencies, classUnderTest, null, new ArrayList<PatternResolver>());
        resolver = new ObjectUnderTestPatternsResolver(context);
    }

    @Test
    public void should_return_unmodified_pattern_when_does_not_match() throws Exception
    {
        assertThat(resolver.resolve("does not match")).isEqualTo("does not match");
    }

    @Test
    public void should_resolve_objectUnderTest() throws Exception
    {
        assertThat(resolver.resolve("pre ${objectUnderTest} between ${objectUnderTest} post"))
                .isEqualTo("pre someThing between someThing post");
    }

    @Test
    public void should_resolve_objectUnderTestType() throws Exception
    {
        assertThat(resolver.resolve("pre ${objectUnderTestType} between ${objectUnderTestType} post"))
                .isEqualTo("pre ${classUnderTest:newType(some.pack.age.SomeThing)} between ${classUnderTest:newType(some.pack.age.SomeThing)} post");
    }

    @Test
    public void should_resolve_objectUnderTest_and_objectUnderTestType() throws Exception
    {
        assertThat(resolver.resolve("a ${objectUnderTestType} b ${objectUnderTest} c ${objectUnderTestType} d ${objectUnderTest} e"))
                .isEqualTo("a ${classUnderTest:newType(some.pack.age.SomeThing)} b someThing c ${classUnderTest:newType(some.pack.age.SomeThing)} d someThing e");
    }
}
