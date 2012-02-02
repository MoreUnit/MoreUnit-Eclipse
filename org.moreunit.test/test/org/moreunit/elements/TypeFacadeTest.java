package org.moreunit.elements;

/**
 * @author vera
 *
 * 23.05.2006 21:09:05
 */

import static org.fest.assertions.Assertions.assertThat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moreunit.test.WorkspaceTestCase;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.Project;
import org.moreunit.test.workspace.WorkspaceHelper;

public class TypeFacadeTest extends ContextTestCase
{
    
    @Project(mainCls="Hello")
    @Preferences(testClassSuffixes="Test")
    @Test
    public void isTestCase_should_return_false_when_regular_class() throws CoreException
    {
        assertThat(TypeFacade.isTestCase(context.getCompilationUnit("Hello"))).isFalse();
    }

    @Project(mainCls="HelloTest")
    @Preferences(testClassSuffixes="Test")
    @Test
    public void isTestCase_should_return_true_when_class_has_test_suffix() throws JavaModelException
    {
        assertThat(TypeFacade.isTestCase(context.getCompilationUnit("HelloTest"))).isTrue();
    }

    @Project(mainCls="TestHello")
    @Preferences(testClassPrefixes="Test")
    @Test
    public void isTestCase_should_return_true_when_class_has_test_prefix() throws JavaModelException
    {
        assertThat(TypeFacade.isTestCase(context.getCompilationUnit("TestHello"))).isTrue();
    }
}
