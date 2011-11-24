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

import java.util.List;

import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.junit.Test;
import org.moreunit.elements.TypeFacade.MethodSearchMode;
import org.moreunit.test.context.Context;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Project;
import org.moreunit.test.context.Properties;
import org.moreunit.test.context.TestType;
import org.moreunit.test.context.configs.SimpleJUnit3Project;
import org.moreunit.test.workspace.MethodHandler;
import org.moreunit.test.workspace.TypeHandler;

@Context(SimpleJUnit3Project.class)
public class ClassTypeFacadeTest extends ContextTestCase
{
    @Test
    public void testGetOneCorrespondingTestCase() throws Exception
    {
        ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());
        IType oneCorrespondingTestCase = classTypeFacade.getOneCorrespondingTestCase(false);

        testCaseHandler().assertThat().isEqualTo(oneCorrespondingTestCase);
    }

    @Test
    @Project(mainCls = "com: enum SomeEnum", properties = @Properties(testType = TestType.JUNIT3, testClassSuffixes = "Test"))
    public void testGetOneCorrespondingTestCaseWithEnum() throws Exception
    {
        ClassTypeFacade classTypeFacade = new ClassTypeFacade(context.getCompilationUnit("com.SomeEnum"));
        assertThat(classTypeFacade.getCorrespondingTestCaseList()).isEmpty();
    }

    @Test
    public void testGetCorrespondingTestMethodWithTestMethod() throws Exception
    {
        IMethod getNumberOneMethod = cutHandler().addMethod("public int getNumberOne()", "return 1;").get();
        IMethod getNumberOneTestMethod = testCaseHandler().addMethod("public void testGetNumberOne()").get();

        ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());
        IMethod correspondingTestMethod = classTypeFacade.getCorrespondingTestMethod(getNumberOneMethod, testCaseHandler().get());
        assertEquals(getNumberOneTestMethod, correspondingTestMethod);
    }

    @Test
    public void testHasTestMethodFollowingNamingPatternWhenTestMethodExists() throws Exception
    {
        IMethod getNumberOneMethod = cutHandler().addMethod("public int getNumberOne()", "return 1;").get();
        testCaseHandler().addMethod("public void testGetNumberOne()");

        ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());
        assertTrue(classTypeFacade.hasTestMethod(getNumberOneMethod, MethodSearchMode.BY_NAME));
    }

    @Test
    public void testGetCorrespondingTestMethodWithoutTestMethod() throws Exception
    {
        IMethod methodWithoutCorrespondingTestMethod = cutHandler().addMethod("public int getNumberTwo()", "return 2;").get();

        ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());
        assertNull(classTypeFacade.getCorrespondingTestMethod(methodWithoutCorrespondingTestMethod, testCaseHandler().get()));
    }

    @Test
    public void testHasTestMethodFollowingNamingPatternWhenTestMethodDoesNotExist() throws Exception
    {
        IMethod methodWithoutCorrespondingTestMethod = cutHandler().addMethod("public int getNumberTwo()", "return 2;").get();

        ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());
        assertFalse(classTypeFacade.hasTestMethod(methodWithoutCorrespondingTestMethod, MethodSearchMode.BY_NAME));
    }

    @Test
    public void testHasTestMethodCallingMethodUnderTestWhenTestMethodExists() throws Exception
    {
        IMethod getNumberOneMethod = cutHandler().addMethod("public int getNumberOne()", "return 1;").get();
        testCaseHandler().addMethod("public void testWhichNameDoesNotMatchTestedMethodName()", "new SomeClass().getNumberOne();");

        ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());
        assertTrue(classTypeFacade.hasTestMethod(getNumberOneMethod, MethodSearchMode.BY_CALL));
    }

    @Test
    public void testHasTestMethodCallingMethodUnderTestWhenTestMethodDoesNotExist() throws Exception
    {
        IMethod methodWithoutCorrespondingTestMethod = cutHandler().addMethod("public int getNumberTwo()", "return 2;").get();

        ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());
        assertFalse(classTypeFacade.hasTestMethod(methodWithoutCorrespondingTestMethod, MethodSearchMode.BY_CALL));
    }

    @Test
    public void testGetCorrespondingTestMethods() throws Exception
    {
        IMethod getNumberOneMethod = cutHandler().addMethod("public int getNumberOne()", "return 1;").get();
        IMethod getNumberOneTestMethod = testCaseHandler().addMethod("public int testGetNumberOne()").get();
        IMethod getNumberOneTestMethod2 = testCaseHandler().addMethod("public int testGetNumberOne2()").get();

        ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());
        List<IMethod> correspondingTestMethods = classTypeFacade.getCorrespondingTestMethods(getNumberOneMethod);
        assertThat(correspondingTestMethods).containsExactly(getNumberOneTestMethod, getNumberOneTestMethod2);
    }

    @Test
    public void testGetOneCorrespondingMemberWithoutTestMethod() throws Exception
    {
        ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());
        IMember oneCorrespondingTestMember = classTypeFacade.getOneCorrespondingMember(null, false, false, null);

        testCaseHandler().assertThat().isEqualTo(oneCorrespondingTestMember);
    }

    @Test
    public void testGetOneCorrespondingMemberWithTestMethod() throws Exception
    {
        IMethod getNumberOneMethod = cutHandler().addMethod("public int getNumberOne()", "return 1;").get();
        IMethod getNumberOneTestMethod = testCaseHandler().addMethod("public void testGetNumberOne()").get();

        ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());
        IMember oneCorrespondingTestMember = classTypeFacade.getOneCorrespondingMember(getNumberOneMethod, false, false, null);

        assertEquals(getNumberOneTestMethod, oneCorrespondingTestMember);
    }

    @Test
    public void testGetOneCorrespondingMemberWithExtendedSearchAndTestMethodFollowingNamingPattern() throws Exception
    {
        IMethod getNumberOneMethod = cutHandler().addMethod("public int getNumberOne()", "return 1;").get();
        IMethod getNumberOneTestMethod = testCaseHandler().addMethod("public void testGetNumberOne()").get();

        ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());
        IMember oneCorrespondingTestMember = classTypeFacade.getOneCorrespondingMember(getNumberOneMethod, false, true, null);

        assertEquals(getNumberOneTestMethod, oneCorrespondingTestMember);
    }

    @Test
    public void testGetOneCorrespondingMemberWithExtendedSearchAndTestMethodCallingMethodUnderTest() throws Exception
    {
        IMethod getNumberOneMethod = cutHandler().addMethod("public int getNumberOne()", "return 1;").get();
        IMethod giveMe1TestMethod = testCaseHandler().addMethod("public void testGiveMe1()", "new SomeClass().getNumberOne();").get();

        ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());
        IMember oneCorrespondingTestMember = classTypeFacade.getOneCorrespondingMember(getNumberOneMethod, false, true, null);

        assertEquals(giveMe1TestMethod, oneCorrespondingTestMember);
    }

    @Test
    public void testGetOneCorrespondingMemberWithExtendedSearchAndTestMethodFollowingNamingPatternAndCallingMethodUnderTest() throws Exception
    {
        IMethod getNumberOneMethod = cutHandler().addMethod("public int getNumberOne()", "return 1;").get();
        IMethod getNumberOneTestMethod = testCaseHandler().addMethod("public void testGetNumberOne()", "new SomeClass().getNumberOne();").get();

        ClassTypeFacade classTypeFacade = new ClassTypeFacade(cutHandler().getCompilationUnit());
        IMember oneCorrespondingTestMember = classTypeFacade.getOneCorrespondingMember(getNumberOneMethod, false, true, null);

        assertEquals(getNumberOneTestMethod, oneCorrespondingTestMember);
    }

    @Test
    public void testGetOneCorrespondingMemberWithMethodUnderTestOverridingAnotherMethodUnderTest() throws Exception
    {
        cutHandler().addMethod("public void doIt()");

        testCaseHandler().addMethod("public void testDoIt()", "new SomeClass().doIt();");

        TypeHandler subTypeHandler = cutHandler().createSubclass("org.SomeSubClass");
        IMethod overridingMethod = subTypeHandler.addMethod("public void doIt()", null).get();

        TypeHandler subTypeTestHandler = context.getProjectHandler().getTestSrcFolderHandler().createClass("org.SomeSubClassTest");
        MethodHandler overridingMethodTestHandler = subTypeTestHandler.addMethod("public void testDoIt()", "new SomeSubClass().doIt();");

        ClassTypeFacade classTypeFacade = new ClassTypeFacade(subTypeHandler.getCompilationUnit());
        IMember oneCorrespondingTestMember = classTypeFacade.getOneCorrespondingMember(overridingMethod, false, true, null);

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
