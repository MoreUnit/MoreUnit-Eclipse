package org.moreunit.elements;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.jupiter.api.Test;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.Project;

public class TypeFacadeTest extends ContextTestCase
{

    @Project(mainCls="Hello")
    @Preferences(testClassNameTemplate="${srcFile}Test")
    @Test
    public void isTestCase_should_return_false_when_regular_class() throws CoreException
    {
        assertFalse(TypeFacade.isTestCase(context.getCompilationUnit("Hello")));
    }

    @Project(mainCls="HelloTest")
    @Preferences(testClassNameTemplate="${srcFile}Test")
    @Test
    public void isTestCase_should_return_true_when_class_has_test_suffix() throws JavaModelException
    {
        assertTrue(TypeFacade.isTestCase(context.getCompilationUnit("HelloTest")));
    }

    @Project(mainCls="TestHello")
    @Preferences(testClassNameTemplate="Test${srcFile}")
    @Test
    public void isTestCase_should_return_true_when_class_has_test_prefix() throws JavaModelException
    {
        assertTrue(TypeFacade.isTestCase(context.getCompilationUnit("TestHello")));
    }
}
