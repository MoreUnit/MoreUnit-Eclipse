package org.moreunit.elements;

/**
 * @author vera
 *
 * 23.05.2006 21:22:53
 */

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.moreunit.elements.CorrespondingMemberRequest.newCorrespondingMemberRequest;

import java.util.List;

import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.moreunit.SourceFolderContext;
import org.moreunit.elements.CorrespondingMemberRequest.MemberType;
import org.moreunit.elements.TypeFacade.MethodSearchMode;
import org.moreunit.test.context.Context;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.Project;
import org.moreunit.test.context.Properties;
import org.moreunit.test.context.TestType;
import org.moreunit.test.context.configs.SimpleJUnit3Project;
import org.moreunit.test.workspace.MethodHandler;
import org.moreunit.test.workspace.TypeHandler;
import org.moreunit.util.SearchScopeSingelton;

@Context(SimpleJUnit3Project.class)
public class ClassTypeFacadeTest extends ContextTestCase
{
    
    @Test
    public void getOneCorrespondingTestCase_should_return_test_for_cut() throws Exception
    {
        ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());
        IType oneCorrespondingTestCase = classTypeFacade.getOneCorrespondingTestCase(false).get();

        testCaseHandler().assertThat().isEqualTo(oneCorrespondingTestCase);
    }

    @Test
    @Project(mainCls = "com: enum SomeEnum", properties = @Properties(testType = TestType.JUNIT3, testClassNameTemplate = "${srcFile}Test"))
    public void getOneCorrespondingTestCase_should_return_test_for_enum() throws Exception
    {
        ClassTypeFacade classTypeFacade = new ClassTypeFacade(context.getCompilationUnit("com.SomeEnum"));
        assertThat(classTypeFacade.getCorrespondingTestCases()).isEmpty();
    }

    @Test
    public void getCorrespondingTestMethod_should_return_testmethod_for_method() throws Exception
    {
        IMethod getNumberOneMethod = cutHandler().addMethod("public int getNumberOne()", "return 1;").get();
        IMethod getNumberOneTestMethod = testCaseHandler().addMethod("public void testGetNumberOne()").get();

        ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());
        IMethod correspondingTestMethod = classTypeFacade.getCorrespondingTestMethod(getNumberOneMethod, testCaseHandler().get());
        assertThat(getNumberOneTestMethod).isEqualTo(correspondingTestMethod);
    }

    @Test
    public void getCorrespondingTestMethod_should_return_null_when_testmethod_is_missing() throws Exception
    {
        IMethod methodWithoutCorrespondingTestMethod = cutHandler().addMethod("public int getNumberTwo()", "return 2;").get();

        ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());
        assertThat(classTypeFacade.getCorrespondingTestMethod(methodWithoutCorrespondingTestMethod, testCaseHandler().get())).isNull();
    }

    @Test
    public void getCorrespondingTestMethods_should_return_all_testmethods_for_method() throws Exception
    {
        IMethod getNumberOneMethod = cutHandler().addMethod("public int getNumberOne()", "return 1;").get();
        IMethod getNumberOneTestMethod = testCaseHandler().addMethod("public int testGetNumberOne()").get();
        IMethod getNumberOneTestMethod2 = testCaseHandler().addMethod("public int testGetNumberOne2()").get();

        ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());
        List<IMethod> correspondingTestMethods = classTypeFacade.getCorrespondingTestMethods(getNumberOneMethod);
        assertThat(correspondingTestMethods).containsExactly(getNumberOneTestMethod, getNumberOneTestMethod2);
    }

    @Test
    public void getOneCorrespondingMember_should_return_testcase_when_no_testmethod_given() throws Exception
    {
        TypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());
        
        CorrespondingMemberRequest request = newCorrespondingMemberRequest() //
                .withExpectedResultType(MemberType.TYPE_OR_METHOD) //
                .build();
        
        IMember oneCorrespondingTestMember = classTypeFacade.getOneCorrespondingMember(request);

        testCaseHandler().assertThat().isEqualTo(oneCorrespondingTestMember);
    }

    @Test
    public void getOneCorrespondingMember_should_return_testmethod_when_exits() throws Exception
    {
        IMethod getNumberOneMethod = cutHandler().addMethod("public int getNumberOne()", "return 1;").get();
        IMethod getNumberOneTestMethod = testCaseHandler().addMethod("public void testGetNumberOne()").get();

        TypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());
        
        CorrespondingMemberRequest request = newCorrespondingMemberRequest() //
                .withExpectedResultType(MemberType.TYPE_OR_METHOD) //
                .withCurrentMethod(getNumberOneMethod) //
                .build();
        
        IMember oneCorrespondingTestMember = classTypeFacade.getOneCorrespondingMember(request);

        assertThat(getNumberOneTestMethod).isEqualTo(oneCorrespondingTestMember);
    }

    @Test
    public void getOneCorrespondingMember_should_return_testmethod_when_caller_exists() throws Exception
    {
        IMethod getNumberOneMethod = cutHandler().addMethod("public int getNumberOne()", "return 1;").get();
        IMethod giveMe1TestMethod = testCaseHandler().addMethod("public void testGiveMe1()", "new SomeClass().getNumberOne();").get();

        TypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());
        
        CorrespondingMemberRequest request = newCorrespondingMemberRequest() //
                .withExpectedResultType(MemberType.TYPE_OR_METHOD) //
                .withCurrentMethod(getNumberOneMethod) //
                .extendedSearch(true) //
                .build();
        
        IMember oneCorrespondingTestMember = classTypeFacade.getOneCorrespondingMember(request);

        assertThat(giveMe1TestMethod).isEqualTo(oneCorrespondingTestMember);
    }

    @Test
    public void getOneCorrespondingMember_should_return_testmethod_when_testmethod_and_caller_exist() throws Exception
    {
        IMethod getNumberOneMethod = cutHandler().addMethod("public int getNumberOne()", "return 1;").get();
        IMethod getNumberOneTestMethod = testCaseHandler().addMethod("public void testGetNumberOne()", "new SomeClass().getNumberOne();").get();

        TypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());
        
        CorrespondingMemberRequest request = newCorrespondingMemberRequest() //
                .withExpectedResultType(MemberType.TYPE_OR_METHOD) //
                .withCurrentMethod(getNumberOneMethod) //
                .extendedSearch(true) //
                .build();
        
        IMember oneCorrespondingTestMember = classTypeFacade.getOneCorrespondingMember(request);

        assertThat(getNumberOneTestMethod).isEqualTo(oneCorrespondingTestMember);
    }

    @Test
    public void getOneCorrespondingMember_should_return_overridden_method_when_subtype_exists() throws Exception
    {
        cutHandler().addMethod("public void doIt()");

        testCaseHandler().addMethod("public void testDoIt()", "new SomeClass().doIt();");

        TypeHandler subTypeHandler = cutHandler().createSubclass("org.SomeSubClass");
        IMethod overridingMethod = subTypeHandler.addMethod("public void doIt()", null).get();

        TypeHandler subTypeTestHandler = context.getProjectHandler().getTestSrcFolderHandler().createClass("org.SomeSubClassTest");
        MethodHandler overridingMethodTestHandler = subTypeTestHandler.addMethod("public void testDoIt()", "new SomeSubClass().doIt();");

        TypeFacade classTypeFacade = new ClassTypeFacade(subTypeHandler.getCompilationUnit());
        
        CorrespondingMemberRequest request = newCorrespondingMemberRequest() //
                .withExpectedResultType(MemberType.TYPE_OR_METHOD) //
                .withCurrentMethod(overridingMethod) //
                .extendedSearch(true) //
                .build();
        
        IMember oneCorrespondingTestMember = classTypeFacade.getOneCorrespondingMember(request);

        overridingMethodTestHandler.assertThat().isEqualTo(oneCorrespondingTestMember);
    }

    private TypeHandler cutHandler()
    {
        return context.getPrimaryTypeHandler("org.SomeClass");
    }

    private TypeHandler testCaseHandler()
    {
        return context.getPrimaryTypeHandler("org.SomeClassTest");
    }
}
