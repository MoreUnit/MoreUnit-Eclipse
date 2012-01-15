package org.moreunit.elements;

/**
 * @author vera
 *
 * 23.05.2006 21:09:05
 */

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
    public void testIsTestCaseRegularClass() throws CoreException
    {
        assertFalse(TypeFacade.isTestCase(context.getCompilationUnit("Hello")));
    }

    @Project(mainCls="HelloTest")
    @Preferences(testClassSuffixes="Test")
    @Test
    public void testIsTestCaseTestWithSuffix() throws JavaModelException
    {
        assertTrue(TypeFacade.isTestCase(context.getCompilationUnit("HelloTest")));
    }

    @Project(mainCls="TestHello")
    @Preferences(testClassPrefixes="Test")
    @Test
    public void testIsTestCaseTestWithPrefix() throws JavaModelException
    {
        assertTrue(TypeFacade.isTestCase(context.getCompilationUnit("TestHello")));
    }
}
