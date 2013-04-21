package org.moreunit.elements;

/**
 * @author vera
 *
 * 23.05.2006 21:09:05
 */

import static org.fest.assertions.Assertions.assertThat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.Test;
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
        assertThat(TypeFacade.isTestCase(context.getCompilationUnit("Hello"))).isFalse();
    }

    @Project(mainCls="HelloTest")
    @Preferences(testClassNameTemplate="${srcFile}Test")
    @Test
    public void isTestCase_should_return_true_when_class_has_test_suffix() throws JavaModelException
    {
        assertThat(TypeFacade.isTestCase(context.getCompilationUnit("HelloTest"))).isTrue();
    }

    @Project(mainCls="TestHello")
    @Preferences(testClassNameTemplate="Test${srcFile}")
    @Test
    public void isTestCase_should_return_true_when_class_has_test_prefix() throws JavaModelException
    {
        assertThat(TypeFacade.isTestCase(context.getCompilationUnit("TestHello"))).isTrue();
    }
}
