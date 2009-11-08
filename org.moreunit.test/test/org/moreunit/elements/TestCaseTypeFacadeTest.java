package org.moreunit.elements;

/**
 * @author vera
 *
 * 23.05.2006 21:13:50
 */

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.junit.Test;
import org.moreunit.SimpleProjectTestCase;
import org.moreunit.WorkspaceHelper;
import static org.junit.Assert.*;

public class TestCaseTypeFacadeTest extends SimpleProjectTestCase
{

    @Test
    public void testGetCorrespondingTestedMethod() throws CoreException
    {
        IType cutType = WorkspaceHelper.createJavaClass(sourcesPackage, "Hello");
        IType testcaseType = WorkspaceHelper.createJavaClass(testPackage, "HelloTest");

        IMethod testedMethod = WorkspaceHelper.createMethodInJavaType(cutType, "public int getNumberOne()", "return 1");
        IMethod testMethod = WorkspaceHelper.createMethodInJavaType(cutType, "public void testGetNumberOne()", "");
        IMethod testMethodWithNoCorrespondingTestedMethod = WorkspaceHelper.createMethodInJavaType(cutType, "public void testAnything()", "");

        TestCaseTypeFacade testCaseTypeFacade = new TestCaseTypeFacade(testcaseType.getCompilationUnit());
        assertEquals(testedMethod, testCaseTypeFacade.getCorrespondingTestedMethod(testMethod, cutType));
        assertNull(testCaseTypeFacade.getCorrespondingTestedMethod(testMethodWithNoCorrespondingTestedMethod, cutType));
        
        // cleanup
        WorkspaceHelper.deleteCompilationUnitsForTypes(new IType[] {cutType, testcaseType});
    }
}
