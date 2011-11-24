package org.moreunit.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.moreunit.util.CollectionUtils.asSet;

import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.Before;
import org.junit.Test;
import org.moreunit.SimpleProjectTestCase;
import org.moreunit.test.workspace.WorkspaceHelper;

public class MethodTestCallerFinderTest extends SimpleProjectTestCase
{

    private IType cutType;
    private IType testcaseType;
    private IMethod getNumberOneMethod;

    @Before
    public void setUp() throws JavaModelException
    {
        cutType = createJavaClass("Hello", true);
        testcaseType = createTestCase("HelloTest", true);
        getNumberOneMethod = WorkspaceHelper.createMethodInJavaType(cutType, "public int getNumberOne()", "return 1;");
    }

    @Test
    public void testGetMatchesWithoutTestMethod() throws JavaModelException
    {
        Set<IMethod> matches = new MethodTestCallerFinder(getNumberOneMethod, asSet(testcaseType)).getMatches(new NullProgressMonitor());
        assertTrue(matches.isEmpty());
    }

    @Test
    public void testGetMatchesWithOneTestMethod() throws JavaModelException
    {
        IMethod giveMe1TestMethod = WorkspaceHelper.createMethodInJavaType(testcaseType, "public void testGiveMe1()", "new Hello().getNumberOne();");

        Set<IMethod> matches = new MethodTestCallerFinder(getNumberOneMethod, asSet(testcaseType)).getMatches(new NullProgressMonitor());
        assertEquals(asSet(giveMe1TestMethod), matches);
    }

    @Test
    public void testGetMatchesWithSeveralTestMethods() throws JavaModelException
    {
        IMethod giveMe1TestMethod = WorkspaceHelper.createMethodInJavaType(testcaseType, "public void testGiveMe1()", "new Hello().getNumberOne();");
        IMethod gimme1TestMethod = WorkspaceHelper.createMethodInJavaType(testcaseType, "public void testGimme1()", "new Hello().getNumberOne();");
        IMethod getNumber1TestMethod = WorkspaceHelper.createMethodInJavaType(testcaseType, "public void testGetNumber1()", "new Hello().getNumberOne();");

        Set<IMethod> matches = new MethodTestCallerFinder(getNumberOneMethod, asSet(testcaseType)).getMatches(new NullProgressMonitor());
        assertEquals(asSet(giveMe1TestMethod, gimme1TestMethod, getNumber1TestMethod), matches);
    }
}
