package org.moreunit.decorator;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moreunit.test.SimpleProjectTestCase;
import org.moreunit.test.workspace.WorkspaceHelper;

public class UnitDecoratorTest extends SimpleProjectTestCase
{
    private UnitDecorator unitDecorator;

    @Before
    public void setUp() throws Exception
    {
        unitDecorator = new UnitDecorator();
    }

    @After
    public void tearDown() throws Exception
    {
        unitDecorator = null;
    }

    @Test
    public void testTryToGetCompilationUnitFromElementIsNoFile()
    {
        IResource packageFragmentResource = sourcesPackage.getResource();
        assertNull(unitDecorator.tryToGetCompilationUnitFromElement(packageFragmentResource));
    }

    @Test
    public void testTryToGetCompilationUnitFromElementIsTestCase() throws JavaModelException
    {
        IResource testCaseResource = initWorkspaceWithClassAndTestAndReturnResourceOfTestCase();
        assertNull(unitDecorator.tryToGetCompilationUnitFromElement(testCaseResource));
    }

    private IResource initWorkspaceWithClassAndTestAndReturnResourceOfTestCase() throws JavaModelException
    {
        WorkspaceHelper.createJavaClass(sourcesPackage, "Hello");
        IType testcaseType = WorkspaceHelper.createJavaClass(testPackage, "HelloTest");

        return testcaseType.getResource();
    }

    @Test
    public void testTryToGetCompilationUnitFromElementIsNotTestCase() throws JavaModelException
    {
        IResource classResource = initWorkspaceWithClassAndTestAndReturnResourceOfClass();
        assertNotNull(unitDecorator.tryToGetCompilationUnitFromElement(classResource));
    }

    private IResource initWorkspaceWithClassAndTestAndReturnResourceOfClass() throws JavaModelException
    {
        IType classType = WorkspaceHelper.createJavaClass(sourcesPackage, "HelloWorld");
        WorkspaceHelper.createJavaClass(testPackage, "HelloWorldTest");

        return classType.getResource();
    }
}
