package org.moreunit.decorator;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.Test;
import org.moreunit.SimpleProjectTestCase;
import org.moreunit.WorkspaceHelper;
import static org.junit.Assert.*;

public class UnitDecoratorTest extends SimpleProjectTestCase
{
    private UnitDecorator unitDecorator;

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        unitDecorator = new UnitDecorator();
    }

    @Override
    public void tearDown() throws Exception
    {
        super.tearDown();
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
        IType classType = WorkspaceHelper.createJavaClass(sourcesPackage, "Hello");
        WorkspaceHelper.createJavaClass(testPackage, "HelloTest");

        return classType.getResource();
    }
}
