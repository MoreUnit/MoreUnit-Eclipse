package org.moreunit.elements;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.Before;
import org.junit.Test;
import org.moreunit.SimpleProjectTestCase;
import org.moreunit.WorkspaceHelper;
import org.moreunit.preferences.PreferenceConstants;
import org.moreunit.preferences.Preferences;

public class MethodFacadeTest extends SimpleProjectTestCase
{
    private IType aType;

    @Before
    public void setUp() throws JavaModelException
    {
        aType = createTestCase("AType", true);
    }

    @Test
    public void testIsTestNgTestMethod() throws JavaModelException
    {
        Preferences.getInstance().setTestType(workspaceTestProject, PreferenceConstants.TEST_TYPE_VALUE_TESTNG);
        IMethod method = WorkspaceHelper.createMethodInJavaType(aType, "public void testIt()", "");
        assertFalse(new MethodFacade(method).isTestMethod());

        method = WorkspaceHelper.createMethodInJavaType(aType, "@Test public void testIt2()", "");
        assertTrue(new MethodFacade(method).isTestMethod());
    }

    @Test
    public void testIsJunit4TestMethod() throws JavaModelException
    {
        Preferences.getInstance().setTestType(workspaceTestProject, PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4);
        IMethod method = WorkspaceHelper.createMethodInJavaType(aType, "public void testIt()", "");
        assertFalse(new MethodFacade(method).isTestMethod());

        method = WorkspaceHelper.createMethodInJavaType(aType, "@Test public void testIt2()", "");
        assertTrue(new MethodFacade(method).isTestMethod());
    }

    @Test
    public void testIsJunit3TestMethod() throws JavaModelException
    {
        Preferences.getInstance().setTestType(workspaceTestProject, PreferenceConstants.TEST_TYPE_VALUE_JUNIT_3);
        IMethod method = WorkspaceHelper.createMethodInJavaType(aType, "protected void testIt()", "");
        assertFalse(new MethodFacade(method).isTestMethod());

        method = WorkspaceHelper.createMethodInJavaType(aType, "public int testIt2()", "return 1;");
        assertFalse(new MethodFacade(method).isTestMethod());

        method = WorkspaceHelper.createMethodInJavaType(aType, "@Test void testIt3()", "");
        assertFalse(new MethodFacade(method).isTestMethod());

        method = WorkspaceHelper.createMethodInJavaType(aType, "public void testIt4()", "");
        assertTrue(new MethodFacade(method).isTestMethod());
    }

    @Test
    public void testIsAnonymous() throws JavaModelException
    {
        IMethod methodUsingAnonymousType = WorkspaceHelper.createMethodInJavaType(aType, "void methodUsingAnonymousType()"
            ,"Object o = new Object() {public String toString(){return \"\";}};");
        assertFalse(new MethodFacade(methodUsingAnonymousType).isAnonymous());

        int offsetInOverriddenToStringMethod = methodUsingAnonymousType.getSourceRange().getOffset() + 60;
        IMethod overriddenToStringMethod = (IMethod) aType.getCompilationUnit().getElementAt(offsetInOverriddenToStringMethod);
        assertTrue(new MethodFacade(overriddenToStringMethod).isAnonymous());
    }

    @Test
    public void testGetFirstNonAnonymousMethodCallingThisMethod() throws JavaModelException
    {
        IMethod methodUsingAnonymousType = WorkspaceHelper.createMethodInJavaType(aType, "void methodUsingAnonymousType()"
            ,"Object o = new Object() {public String toString(){return \"\";}};");
        assertEquals(methodUsingAnonymousType, new MethodFacade(methodUsingAnonymousType).getFirstNonAnonymousMethodCallingThisMethod());

        int offsetInOverriddenToStringMethod = methodUsingAnonymousType.getSourceRange().getOffset() + 60;
        IMethod overriddenToStringMethod = (IMethod) aType.getCompilationUnit().getElementAt(offsetInOverriddenToStringMethod);
        assertEquals(methodUsingAnonymousType, new MethodFacade(overriddenToStringMethod).getFirstNonAnonymousMethodCallingThisMethod());
    }

}
