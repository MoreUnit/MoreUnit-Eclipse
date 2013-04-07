package org.moreunit.elements;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMethod;
import org.junit.Before;
import org.junit.Test;
import org.moreunit.preferences.PreferenceConstants;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.Project;
import org.moreunit.test.workspace.MethodHandler;
import org.moreunit.test.workspace.TypeHandler;

/**
 * @author vera 02.08.2007 07:37:24
 */
@Preferences(testClassSuffixes = "Test", testSrcFolder = "test", testMethodPrefix = false)
@Project(mainCls = "testing:Hello", testCls = "testing:HelloTest", mainSrcFolder = "src", testSrcFolder = "test")
public class TestmethodCreatorTest extends ContextTestCase
{
    private TypeHandler cutType;
    private MethodHandler methodUnderTest;
    private TypeHandler testcaseType;

    @Before
    public void setUp() throws Exception
    {
        cutType = context.getCompilationUnitHandler("testing.Hello").getPrimaryTypeHandler();
        methodUnderTest = cutType.addMethod("public int getNumberOne()", "return 1");

        testcaseType = context.getCompilationUnitHandler("testing.HelloTest").getPrimaryTypeHandler();
    }

    @Test
    public void createTestMethod_should_create_junit3_testmethod() throws CoreException
    {
        TestmethodCreator testmethodCreator = new TestmethodCreator(cutType.getCompilationUnit(), PreferenceConstants.TEST_TYPE_VALUE_JUNIT_3, "foo");
        IMethod createTestMethod = testmethodCreator.createTestMethod(methodUnderTest.get());

        assertThat(createTestMethod.getElementName()).isEqualTo("testGetNumberOne");
        assertThat(createTestMethod.getSource()).doesNotContain("@Test").contains("foo");
        IMethod[] methods = testcaseType.get().getMethods();
        assertThat(methods).containsOnly(createTestMethod);
    }

    @Test
    public void createTestMethod_should_create_junit4_testmethod() throws CoreException
    {
        TestmethodCreator testmethodCreator = new TestmethodCreator(cutType.getCompilationUnit(), PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4, "foo");
        IMethod createTestMethod = testmethodCreator.createTestMethod(methodUnderTest.get());

        assertThat(createTestMethod.getElementName()).isEqualTo("getNumberOne");
        assertThat(createTestMethod.getSource()).startsWith("@Test");
        assertThat(createTestMethod.getSource()).contains("foo");

        IMethod[] methods = testcaseType.get().getMethods();
        assertThat(methods).containsOnly(createTestMethod);
    }

    @Test
    @Preferences(testClassSuffixes = "Test", testSrcFolder = "test", testMethodPrefix = true)
    public void createTestMethod_should_create_junit4_testmethod_with_prefix() throws CoreException
    {
        TestmethodCreator testmethodCreator = new TestmethodCreator(cutType.getCompilationUnit(), PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4, "foo");
        IMethod createTestMethod = testmethodCreator.createTestMethod(methodUnderTest.get());

        assertThat(createTestMethod.getElementName()).isEqualTo("testGetNumberOne");
        assertThat(createTestMethod.getSource()).startsWith("@Test");
        assertThat(createTestMethod.getSource()).contains("foo");

        IMethod[] methods = testcaseType.get().getMethods();
        assertThat(methods).containsOnly(createTestMethod);
    }

    @Test
    public void createTestMethod_should_create_testng_testmethod() throws CoreException
    {
        TestmethodCreator testmethodCreator = new TestmethodCreator(cutType.getCompilationUnit(), PreferenceConstants.TEST_TYPE_VALUE_TESTNG, "foo");
        IMethod createTestMethod = testmethodCreator.createTestMethod(methodUnderTest.get());

        assertThat(createTestMethod.getElementName()).isEqualTo("getNumberOne");
        assertThat(createTestMethod.getSource()).startsWith("@Test");
        assertThat(createTestMethod.getSource()).contains("foo");

        IMethod[] methods = testcaseType.get().getMethods();
        assertThat(methods).containsOnly(createTestMethod);
    }

    @Test
    @Preferences(testClassSuffixes = "Test", testSrcFolder = "test", testMethodPrefix = true)
    public void createTestMethod_should_create_testng_testmethod_with_prefix() throws CoreException
    {
        TestmethodCreator testmethodCreator = new TestmethodCreator(cutType.getCompilationUnit(), PreferenceConstants.TEST_TYPE_VALUE_TESTNG, "foo");
        IMethod createTestMethod = testmethodCreator.createTestMethod(methodUnderTest.get());

        assertThat(createTestMethod.getElementName()).isEqualTo("testGetNumberOne");
        assertThat(createTestMethod.getSource()).startsWith("@Test");
        assertThat(createTestMethod.getSource()).contains("foo");

        IMethod[] methods = testcaseType.get().getMethods();
        assertThat(methods).containsOnly(createTestMethod);
    }

    @Test
    public void createTestMethod_should_create_another_junit3_testmethod_when_called_with_testmethod() throws CoreException
    {
        MethodHandler existingTestMethod = testcaseType.addMethod("public void testGetNumberOne()");

        TestmethodCreator testmethodCreator = new TestmethodCreator(testcaseType.getCompilationUnit(), PreferenceConstants.TEST_TYPE_VALUE_JUNIT_3, "foo");
        IMethod createTestMethod = testmethodCreator.createTestMethod(existingTestMethod.get());
        assertThat(createTestMethod.getElementName()).isEqualTo("testGetNumberOneSuffix");
        assertThat(createTestMethod.getSource()).doesNotContain("@Test").contains("foo");

        IMethod[] methods = testcaseType.get().getMethods();
        assertThat(methods).hasSize(2);
    }

    @Test
    public void createTestMethod_should_create_another_junit4_testmethod_when_called_with_testmethod() throws CoreException
    {
        MethodHandler existingTestMethod = testcaseType.addMethod("public void getNumberOne()");

        TestmethodCreator testmethodCreator = new TestmethodCreator(testcaseType.getCompilationUnit(), PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4, "foo");
        IMethod createTestMethod = testmethodCreator.createTestMethod(existingTestMethod.get());
        assertThat(createTestMethod.getElementName()).isEqualTo("getNumberOneSuffix");
        assertThat(createTestMethod.getSource()).startsWith("@Test").contains("foo");

        IMethod[] methods = testcaseType.get().getMethods();
        assertThat(methods).hasSize(2);
    }

    @Test
    public void createTestMethod_should_create_final_method_when_selected() throws Exception
    {
        TestmethodCreator testmethodCreator = new TestmethodCreator(cutType.getCompilationUnit(), PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4, "foo", true, false);
        IMethod createTestMethod = testmethodCreator.createTestMethod(methodUnderTest.get());
        assertThat(createTestMethod.getSource()).contains("public final void");
    }

    @Test
    public void createTestMethod_should_use_parameter_types_in_method_name_when_method_under_test_is_overloaded() throws Exception
    {
        // given
        MethodHandler doSomethingWithoutArg = cutType.addMethod("public void doSomething()", "");
        MethodHandler doSomethingWithString = cutType.addMethod("public void doSomething(String str)", "");
        MethodHandler doSomethingWithInteger = cutType.addMethod("public void doSomething(Integer i, Double d)", "");
        MethodHandler doSomethingElseWithString = cutType.addMethod("public void doSomethingElse(String str)", "");

        TestmethodCreator testmethodCreator = new TestmethodCreator(cutType.getCompilationUnit(), PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4, "foo", true, false);

        // when
        testmethodCreator.createTestMethod(doSomethingWithoutArg.get());
        testmethodCreator.createTestMethod(doSomethingWithString.get());
        testmethodCreator.createTestMethod(doSomethingWithInteger.get());
        testmethodCreator.createTestMethod(doSomethingElseWithString.get());

        // then
        assertThat(testcaseType.get().getMethods()).hasSize(4).onProperty("elementName")
            // overloaded method: no parameter
            .contains("doSomething")
            // overloaded method: parameter types are used
            .contains("doSomethingString", "doSomethingIntegerDouble")
            // not overloaded: parameter types are not used
            .contains("doSomethingElse");
    }

    @Test
    public void createTestMethods_should_use_parameter_types_in_method_name_when_method_under_test_is_overloaded() throws Exception
    {
        // given
        MethodHandler doSomethingWithoutArg = cutType.addMethod("public void doSomething()", "");
        MethodHandler doSomethingWithStringArray = cutType.addMethod("public void doSomething(String[] str)", "");

        TestmethodCreator testmethodCreator = new TestmethodCreator(cutType.getCompilationUnit(), PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4, "foo", true, false);

        // when
        testmethodCreator.createTestMethods(asList(doSomethingWithoutArg.get(), doSomethingWithStringArray.get()));

        // then
        assertThat(testcaseType.get().getMethods()).hasSize(2).onProperty("elementName")
            // overloaded method: no parameter
            .contains("doSomething")
            // overloaded method: parameter types are used
            .contains("doSomethingStringArray");
    }
}
