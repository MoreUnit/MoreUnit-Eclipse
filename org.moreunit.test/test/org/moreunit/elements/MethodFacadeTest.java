package org.moreunit.elements;

import static org.fest.assertions.Assertions.assertThat;

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
    public void isTestMethod_should_return_true_when_for_testng_method_and_testng_pref() throws JavaModelException
    {
        MethodHandler method = typeHandler.addMethod("public void testIt()");
        assertThat(new MethodFacade(method.get()).isTestMethod()).isFalse();

        method = typeHandler.addMethod("@Test public void testIt2()");
        assertThat(new MethodFacade(method.get()).isTestMethod()).isTrue();
    }

    @Preferences(testType = TestType.JUNIT4)
    @Test
    public void isTestMethod_should_return_true_when_junit4_method_and_junit4_pref() throws JavaModelException
    {
        MethodHandler method = typeHandler.addMethod("public void testIt()");
        assertThat(new MethodFacade(method.get()).isTestMethod()).isFalse();

        method = typeHandler.addMethod("@Test public void testIt2()", "");
        assertThat(new MethodFacade(method.get()).isTestMethod()).isTrue();
    }

    
    @Preferences(testType = TestType.JUNIT3)
    @Test
    public void isTestMethod_should_return_false_when_method_protected()
    {
        MethodHandler method = typeHandler.addMethod("protected void testIt()");
        assertThat(new MethodFacade(method.get()).isTestMethod()).isFalse();
    }
    
    @Preferences(testType = TestType.JUNIT3)
    @Test
    public void isTestMethod_should_return_false_when_method_signature_has_return_type()
    {
        MethodHandler method = typeHandler.addMethod("public int testIt2()", "return 1;");
        assertThat(new MethodFacade(method.get()).isTestMethod()).isFalse();
    }
    
    @Preferences(testType = TestType.JUNIT3)
    @Test
    public void isTestMethod_should_return_false_when_method_is_annotated_with_test_and_junit3_prefs()
    {
        MethodHandler method = typeHandler.addMethod("@Test void testIt3()", "return 1;");
        assertThat(new MethodFacade(method.get()).isTestMethod()).isFalse();
    }
    
    @Preferences(testType = TestType.JUNIT3)
    @Test
    public void isTestMethod_should_return_true_for_simple_testmethod_and_junit3_prefs()
    {
        MethodHandler method = typeHandler.addMethod("public void testIt4()");
        assertThat(new MethodFacade(method.get()).isTestMethod()).isTrue();
    }

    @Test
    public void isAnonymous_should_return_false_for_method_containing_anonymous_type()
    {
        MethodHandler method = typeHandler.addMethod("void methodUsingAnonymousType()"
                                                     ,"Object o = new Object() {public String toString(){return \"\";}};");
        assertThat(new MethodFacade(method.get()).isAnonymous()).isFalse();
    }
    
    @Test
    public void isAnonymour_should_return_true_for_inner_type() throws JavaModelException
    {
        MethodHandler method = typeHandler.addMethod("void methodUsingAnonymousType()"
                                                     ,"Object o = new Object() {public String toString(){return \"\";}};");
        int offsetInOverriddenToStringMethod = method.get().getSourceRange().getOffset() + 60;
        IMethod overriddenToStringMethod = (IMethod) typeHandler.getCompilationUnit().getElementAt(offsetInOverriddenToStringMethod);
        assertThat(new MethodFacade(overriddenToStringMethod).isAnonymous()).isTrue();
    }
    
    @Test
    public void getFirstNonAnonymousMethodCallingThisMethod_should_not_return_inner_type_when_called_on_outer_type() throws JavaModelException
    {
        MethodHandler method = typeHandler.addMethod("void methodUsingAnonymousType()"
                                                    ,"Object o = new Object() {public String toString(){return \"\";}};");
        assertThat(new MethodFacade(method.get()).getFirstNonAnonymousMethodCallingThisMethod()).isEqualTo(method.get());
    }
    
    @Test
    public void getFirstNonAnonymousMethodCallingThisMethod_should_return_outer_type_when_called_from_inner_type() throws JavaModelException
    {
        MethodHandler method = typeHandler.addMethod("void methodUsingAnonymousType()"
                                                     ,"Object o = new Object() {public String toString(){return \"\";}};");
        
        int offsetInOverriddenToStringMethod = method.get().getSourceRange().getOffset() + 60;
        IMethod overriddenToStringMethod = (IMethod) typeHandler.getCompilationUnit().getElementAt(offsetInOverriddenToStringMethod);
        assertThat(new MethodFacade(overriddenToStringMethod).getFirstNonAnonymousMethodCallingThisMethod()).isEqualTo(method.get());
    }
}
