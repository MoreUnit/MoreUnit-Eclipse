package org.moreunit.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.junit.jupiter.api.Test;

public class AddTestMethodContextTest
{
    @Test
    public void two_arg_constructor_should_store_methods()
    {
        final IMethod testMethod = mock(IMethod.class);
        final IMethod methodUnderTest = mock(IMethod.class);
        final ICompilationUnit testCu = mock(ICompilationUnit.class);
        final ICompilationUnit cutCu = mock(ICompilationUnit.class);
        when(testMethod.getCompilationUnit()).thenReturn(testCu);
        when(methodUnderTest.getCompilationUnit()).thenReturn(cutCu);

        final AddTestMethodContext ctx = new AddTestMethodContext(testMethod, methodUnderTest);

        assertEquals(testCu, ctx.getTestClass());
        assertEquals(testMethod, ctx.getTestMethod());
        assertEquals(cutCu, ctx.getClassUnderTest());
        assertEquals(methodUnderTest, ctx.getMethodUnderTest());
        assertFalse(ctx.isNewTestClassCreated());
    }

    @Test
    public void three_arg_constructor_should_store_new_test_class_flag()
    {
        final IMethod testMethod = mock(IMethod.class);
        final IMethod methodUnderTest = mock(IMethod.class);
        final ICompilationUnit testCu = mock(ICompilationUnit.class);
        final ICompilationUnit cutCu = mock(ICompilationUnit.class);
        when(testMethod.getCompilationUnit()).thenReturn(testCu);
        when(methodUnderTest.getCompilationUnit()).thenReturn(cutCu);

        assertTrue(new AddTestMethodContext(testMethod, methodUnderTest, true).isNewTestClassCreated());
        assertFalse(new AddTestMethodContext(testMethod, methodUnderTest, false).isNewTestClassCreated());
    }

    @Test
    public void should_set_and_get_preferences()
    {
        final IMethod testMethod = mock(IMethod.class);
        final IMethod methodUnderTest = mock(IMethod.class);
        final ICompilationUnit testCu = mock(ICompilationUnit.class);
        final ICompilationUnit cutCu = mock(ICompilationUnit.class);
        when(testMethod.getCompilationUnit()).thenReturn(testCu);
        when(methodUnderTest.getCompilationUnit()).thenReturn(cutCu);

        final AddTestMethodContext ctx = new AddTestMethodContext(testMethod, methodUnderTest);
        assertNull(ctx.getPreferences());

        final var prefs = mock(org.moreunit.preferences.Preferences.class);
        ctx.setPreferences(prefs);
        assertEquals(prefs, ctx.getPreferences());
    }

    @Test
    public void four_arg_constructor_should_store_compilation_units_and_methods()
    {
        final IMethod testMethod = mock(IMethod.class);
        final IMethod methodUnderTest = mock(IMethod.class);
        final ICompilationUnit testCu = mock(ICompilationUnit.class);
        final ICompilationUnit cutCu = mock(ICompilationUnit.class);

        final AddTestMethodContext ctx = new AddTestMethodContext(testCu, testMethod, cutCu, methodUnderTest);

        assertEquals(testCu, ctx.getTestClass());
        assertEquals(testMethod, ctx.getTestMethod());
        assertEquals(cutCu, ctx.getClassUnderTest());
        assertEquals(methodUnderTest, ctx.getMethodUnderTest());
        assertFalse(ctx.isNewTestClassCreated());
    }

    @Test
    public void setTestMethod_should_replace_the_test_method()
    {
        final IMethod testMethod = mock(IMethod.class);
        final IMethod methodUnderTest = mock(IMethod.class);
        final ICompilationUnit testCu = mock(ICompilationUnit.class);
        final ICompilationUnit cutCu = mock(ICompilationUnit.class);
        when(testMethod.getCompilationUnit()).thenReturn(testCu);
        when(methodUnderTest.getCompilationUnit()).thenReturn(cutCu);

        final AddTestMethodContext ctx = new AddTestMethodContext(testMethod, methodUnderTest);
        assertEquals(testMethod, ctx.getTestMethod());

        final IMethod newTestMethod = mock(IMethod.class);
        ctx.setTestMethod(newTestMethod);

        assertEquals(newTestMethod, ctx.getTestMethod());
        assertEquals(methodUnderTest, ctx.getMethodUnderTest());
    }

    @Test
    public void toString_should_describe_all_members()
    {
        final IMethod testMethod = mock(IMethod.class);
        final IMethod methodUnderTest = mock(IMethod.class);
        final ICompilationUnit testCu = mock(ICompilationUnit.class);
        final ICompilationUnit cutCu = mock(ICompilationUnit.class);
        when(testMethod.getCompilationUnit()).thenReturn(testCu);
        when(methodUnderTest.getCompilationUnit()).thenReturn(cutCu);
        when(testMethod.getElementName()).thenReturn("testFoo");
        when(methodUnderTest.getElementName()).thenReturn("foo");
        when(testCu.getElementName()).thenReturn("FooTest.java");
        when(cutCu.getElementName()).thenReturn("Foo.java");

        final AddTestMethodContext ctx = new AddTestMethodContext(testMethod, methodUnderTest, true);

        final String s = ctx.toString();

        assertTrue(s.startsWith("AddTestMethodContext ["));
        assertTrue(s.contains("classUnderTestCompilationUnit=Foo.java"));
        assertTrue(s.contains("methodUnderTest=foo"));
        assertTrue(s.contains("testMethod=testFoo"));
        assertTrue(s.contains("testClassCompilationUnit=FooTest.java"));
        assertTrue(s.contains("isNewTestClassCreated=true"));
        assertTrue(s.endsWith("]"));
    }
}
