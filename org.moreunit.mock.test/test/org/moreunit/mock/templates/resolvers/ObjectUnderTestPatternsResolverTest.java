package org.moreunit.mock.templates.resolvers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.eclipse.jdt.core.IType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.moreunit.mock.dependencies.Dependencies;
import org.moreunit.mock.templates.MockingContext;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class ObjectUnderTestPatternsResolverTest
{
    @Mock
    private IType classUnderTest;

    private Dependencies dependencies;
    private MockingContext context;
    private ObjectUnderTestPatternsResolver resolver;

    @BeforeEach
    public void createResolver() throws Exception
    {
        when(classUnderTest.getFullyQualifiedName()).thenReturn("some.pack.age.SomeThing");
        when(classUnderTest.getElementName()).thenReturn("SomeThing");

        dependencies = new Dependencies(null, null, null);
        context = new MockingContext(dependencies, classUnderTest, null, null, new ArrayList<>());
        resolver = new ObjectUnderTestPatternsResolver(context);
    }

    @Test
    public void should_return_unmodified_pattern_when_does_not_match() throws Exception
    {
        assertEquals(resolver.resolve("does not match"), "does not match");
    }

    @Test
    public void should_resolve_objectUnderTest() throws Exception
    {
        assertEquals(resolver.resolve("pre ${objectUnderTest} between ${objectUnderTest} post"), "pre someThing between someThing post");
    }

    @Test
    public void should_resolve_objectUnderTestType() throws Exception
    {
        assertEquals(resolver.resolve("pre ${objectUnderTestType} between ${objectUnderTestType} post"), "pre ${classUnderTest:newType(some.pack.age.SomeThing)} between ${classUnderTest:newType(some.pack.age.SomeThing)} post");
    }

    @Test
    public void should_resolve_objectUnderTest_and_objectUnderTestType() throws Exception
    {
        assertEquals(resolver.resolve("a ${objectUnderTestType} b ${objectUnderTest} c ${objectUnderTestType} d ${objectUnderTest} e"), "a ${classUnderTest:newType(some.pack.age.SomeThing)} b someThing c ${classUnderTest:newType(some.pack.age.SomeThing)} d someThing e");
    }
}
