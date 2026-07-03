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
        IMethod testMethod = mock(IMethod.class);
        IMethod methodUnderTest = mock(IMethod.class);
        ICompilationUnit testCu = mock(ICompilationUnit.class);
        ICompilationUnit cutCu = mock(ICompilationUnit.class);
        when(testMethod.getCompilationUnit()).thenReturn(testCu);
        when(methodUnderTest.getCompilationUnit()).thenReturn(cutCu);

        AddTestMethodContext ctx = new AddTestMethodContext(testMethod, methodUnderTest);

        assertEquals(testCu, ctx.getTestClass());
        assertEquals(testMethod, ctx.getTestMethod());
        assertEquals(cutCu, ctx.getClassUnderTest());
        assertEquals(methodUnderTest, ctx.getMethodUnderTest());
        assertFalse(ctx.isNewTestClassCreated());
    }

    @Test
    public void three_arg_constructor_should_store_new_test_class_flag()
    {
        IMethod testMethod = mock(IMethod.class);
        IMethod methodUnderTest = mock(IMethod.class);
        ICompilationUnit testCu = mock(ICompilationUnit.class);
        ICompilationUnit cutCu = mock(ICompilationUnit.class);
        when(testMethod.getCompilationUnit()).thenReturn(testCu);
        when(methodUnderTest.getCompilationUnit()).thenReturn(cutCu);

        assertTrue(new AddTestMethodContext(testMethod, methodUnderTest, true).isNewTestClassCreated());
        assertFalse(new AddTestMethodContext(testMethod, methodUnderTest, false).isNewTestClassCreated());
    }

    @Test
    public void should_set_and_get_preferences()
    {
        IMethod testMethod = mock(IMethod.class);
        IMethod methodUnderTest = mock(IMethod.class);
        ICompilationUnit testCu = mock(ICompilationUnit.class);
        ICompilationUnit cutCu = mock(ICompilationUnit.class);
        when(testMethod.getCompilationUnit()).thenReturn(testCu);
        when(methodUnderTest.getCompilationUnit()).thenReturn(cutCu);

        AddTestMethodContext ctx = new AddTestMethodContext(testMethod, methodUnderTest);
        assertNull(ctx.getPreferences());

        var prefs = mock(org.moreunit.preferences.Preferences.class);
        ctx.setPreferences(prefs);
        assertEquals(prefs, ctx.getPreferences());
    }
}
