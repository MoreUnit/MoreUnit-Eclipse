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

    @Preferences(testClassSuffixes="Test")
    @Project(mainCls="Hello")
    @Test
    public void testIsTestCaseRegularClass() throws CoreException
    {
        assertFalse(TypeFacade.isTestCase(context.getCompilationUnit("Hello")));
    }

    @Preferences(testClassSuffixes="Test")
    @Project(mainCls="HelloTest")
    @Test
    public void testIsTestCaseTestWithSuffix() throws JavaModelException
    {
        assertTrue(TypeFacade.isTestCase(context.getCompilationUnit("HelloTest")));
    }

    @Preferences(testClassPrefixes="Test")
    @Project(mainCls="TestHello")
    @Test
    public void testIsTestCaseTestWithPrefix() throws JavaModelException
    {
        String[] prefixes = org.moreunit.preferences.Preferences.getInstance().getPrefixes(context.getProjectHandler().get());
        String[] suffixes = org.moreunit.preferences.Preferences.getInstance().getSuffixes(context.getProjectHandler().get());
        assertEquals(1, prefixes.length);
        assertEquals(0, suffixes.length);
        assertTrue(TypeFacade.isTestCase(context.getCompilationUnit("TestHello")));
    }
}
