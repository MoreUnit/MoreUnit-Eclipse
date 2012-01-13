package org.moreunit.elements;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.Before;
import org.junit.Test;
import org.moreunit.test.SimpleProjectTestCase;
import org.moreunit.test.context.Context;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.Project;
import org.moreunit.test.context.TestType;
import org.moreunit.test.workspace.CompilationUnitHandler;
import org.moreunit.test.workspace.MethodHandler;
import org.moreunit.test.workspace.TypeHandler;
import org.moreunit.test.workspace.WorkspaceHelper;

@Project(mainCls = "org:AType")
public class MethodFacadeTest extends ContextTestCase
{
    private TypeHandler typeHandler;
    
    @Before
    public void init()
    {
        this.typeHandler = context.getCompilationUnitHandler("org.AType").getPrimaryTypeHandler();
    }

    @Preferences(testType = TestType.TESTNG)
    @Test
    public void testIsTestNgTestMethod() throws JavaModelException
    {
        MethodHandler method = typeHandler.addMethod("public void testIt()");
        assertFalse(new MethodFacade(method.get()).isTestMethod());

        method = typeHandler.addMethod("@Test public void testIt2()");
        assertTrue(new MethodFacade(method.get()).isTestMethod());
    }

    @Preferences(testType = TestType.JUNIT4)
    @Test
    public void testIsJunit4TestMethod() throws JavaModelException
    {
        MethodHandler method = typeHandler.addMethod("public void testIt()");
        assertFalse(new MethodFacade(method.get()).isTestMethod());

        method = typeHandler.addMethod("@Test public void testIt2()", "");
        assertTrue(new MethodFacade(method.get()).isTestMethod());
    }

    @Preferences(testType = TestType.JUNIT3)
    @Test
    public void testIsJunit3TestMethod() throws JavaModelException
    {
        MethodHandler method = typeHandler.addMethod("protected void testIt()");
        assertFalse(new MethodFacade(method.get()).isTestMethod());

        method = typeHandler.addMethod("public int testIt2()", "return 1;");
        assertFalse(new MethodFacade(method.get()).isTestMethod());

        method = typeHandler.addMethod("@Test void testIt3()");
        assertFalse(new MethodFacade(method.get()).isTestMethod());

        method = typeHandler.addMethod("public void testIt4()");
        assertTrue(new MethodFacade(method.get()).isTestMethod());
    }

    @Test
    public void testIsAnonymous() throws JavaModelException
    {
        MethodHandler method = typeHandler.addMethod("void methodUsingAnonymousType()"
            ,"Object o = new Object() {public String toString(){return \"\";}};");
        assertFalse(new MethodFacade(method.get()).isAnonymous());

        int offsetInOverriddenToStringMethod = method.get().getSourceRange().getOffset() + 60;
        IMethod overriddenToStringMethod = (IMethod) typeHandler.getCompilationUnit().getElementAt(offsetInOverriddenToStringMethod);
        assertTrue(new MethodFacade(overriddenToStringMethod).isAnonymous());
    }

    @Test
    public void testGetFirstNonAnonymousMethodCallingThisMethod() throws JavaModelException
    {
        MethodHandler method = typeHandler.addMethod("void methodUsingAnonymousType()"
            ,"Object o = new Object() {public String toString(){return \"\";}};");
        assertEquals(method.get(), new MethodFacade(method.get()).getFirstNonAnonymousMethodCallingThisMethod());

        int offsetInOverriddenToStringMethod = method.get().getSourceRange().getOffset() + 60;
        IMethod overriddenToStringMethod = (IMethod) typeHandler.getCompilationUnit().getElementAt(offsetInOverriddenToStringMethod);
        assertEquals(method.get(), new MethodFacade(overriddenToStringMethod).getFirstNonAnonymousMethodCallingThisMethod());
    }
}
