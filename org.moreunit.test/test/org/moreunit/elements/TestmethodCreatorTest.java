package org.moreunit.elements;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.moreunit.preferences.PreferenceConstants.TEST_TYPE_VALUE_JUNIT_3;
import static org.moreunit.preferences.PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4;
import static org.moreunit.preferences.PreferenceConstants.TEST_TYPE_VALUE_TESTNG;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMethod;
import org.junit.Before;
import org.junit.Test;
import org.moreunit.elements.TestmethodCreator.TestMethodCreationSettings;
import org.moreunit.preferences.PreferenceConstants;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.Project;
import org.moreunit.test.context.TestType;
import org.moreunit.test.workspace.MethodHandler;
import org.moreunit.test.workspace.TypeHandler;

/**
 * @author vera 02.08.2007 07:37:24
 */
@Preferences(testClassNameTemplate = "${srcFile}Test", testSrcFolder = "test", testMethodPrefix = false)
@Project(mainCls = "testing:Hello", testCls = "testing:HelloTest", mainSrcFolder = "src", testSrcFolder = "test")
public class TestmethodCreatorTest extends ContextTestCase
{
    private static final String SOME_TEST_CODE = "// irrelevant";

    private TypeHandler cutType;
    private MethodHandler methodUnderTest;
    private TypeHandler testcaseType;

    @Before
    public void setUp() throws Exception
    {
        TestmethodCreator.discardExtensions = true;
        cutType = context.getCompilationUnitHandler("testing.Hello").getPrimaryTypeHandler();
        methodUnderTest = cutType.addMethod("public int getNumberOne()", "return 1");

        testcaseType = context.getCompilationUnitHandler("testing.HelloTest").getPrimaryTypeHandler();
    }

    @Test
    public void createTestMethod_should_create_junit3_testmethod() throws CoreException
    {
        TestmethodCreator testmethodCreator = new TestmethodCreator(new TestMethodCreationSettings()
                .compilationUnit(cutType.getCompilationUnit())
                .testType(TEST_TYPE_VALUE_JUNIT_3)
                .defaultTestMethodContent(SOME_TEST_CODE));

        MethodCreationResult result = testmethodCreator.createTestMethod(methodUnderTest.get());
        assertTrue(result.methodCreated());
        assertFalse(result.methodAlreadyExists());

        IMethod createTestMethod = result.getMethod();
        assertThat(createTestMethod.getElementName()).isEqualTo("testGetNumberOne");
        assertThat(createTestMethod.getSource()).doesNotContain("@Test").contains(SOME_TEST_CODE);
        IMethod[] methods = testcaseType.get().getMethods();
        assertThat(methods).containsOnly(createTestMethod);
    }

    @Test
    public void createTestMethod_should_create_junit4_testmethod() throws CoreException
    {
        TestmethodCreator testmethodCreator = new TestmethodCreator(new TestMethodCreationSettings()
                .compilationUnit(cutType.getCompilationUnit())
                .testType(TEST_TYPE_VALUE_JUNIT_4)
                .defaultTestMethodContent(SOME_TEST_CODE));

        IMethod createTestMethod = testmethodCreator.createTestMethod(methodUnderTest.get()).getMethod();

        assertThat(createTestMethod.getElementName()).isEqualTo("getNumberOne");
        assertThat(createTestMethod.getSource()).startsWith("@Test");
        assertThat(createTestMethod.getSource()).contains(SOME_TEST_CODE);

        IMethod[] methods = testcaseType.get().getMethods();
        assertThat(methods).containsOnly(createTestMethod);
    }

    @Test
    @Preferences(testClassNameTemplate = "${srcFile}Test", testSrcFolder = "test", testMethodPrefix = true)
    public void createTestMethod_should_create_junit4_testmethod_with_prefix() throws CoreException
    {
        TestmethodCreator testmethodCreator = new TestmethodCreator(new TestMethodCreationSettings()
                .compilationUnit(cutType.getCompilationUnit())
                .testType(TEST_TYPE_VALUE_JUNIT_4)
                .defaultTestMethodContent(SOME_TEST_CODE));

        IMethod createTestMethod = testmethodCreator.createTestMethod(methodUnderTest.get()).getMethod();

        assertThat(createTestMethod.getElementName()).isEqualTo("testGetNumberOne");
        assertThat(createTestMethod.getSource()).startsWith("@Test");
        assertThat(createTestMethod.getSource()).contains(SOME_TEST_CODE);

        IMethod[] methods = testcaseType.get().getMethods();
        assertThat(methods).containsOnly(createTestMethod);
    }

    @Test
    public void createTestMethod_should_create_testng_testmethod() throws CoreException
    {
        TestmethodCreator testmethodCreator = new TestmethodCreator(new TestMethodCreationSettings()
                .compilationUnit(cutType.getCompilationUnit())
                .testType(TEST_TYPE_VALUE_TESTNG)
                .defaultTestMethodContent(SOME_TEST_CODE));

        IMethod createTestMethod = testmethodCreator.createTestMethod(methodUnderTest.get()).getMethod();

        assertThat(createTestMethod.getElementName()).isEqualTo("getNumberOne");
        assertThat(createTestMethod.getSource()).startsWith("@Test");
        assertThat(createTestMethod.getSource()).contains(SOME_TEST_CODE);

        IMethod[] methods = testcaseType.get().getMethods();
        assertThat(methods).containsOnly(createTestMethod);
    }

    @Test
    @Preferences(testClassNameTemplate = "${srcFile}Test", testSrcFolder = "test", testMethodPrefix = true)
    public void createTestMethod_should_create_testng_testmethod_with_prefix() throws CoreException
    {
        TestmethodCreator testmethodCreator = new TestmethodCreator(new TestMethodCreationSettings()
                .compilationUnit(cutType.getCompilationUnit())
                .testType(TEST_TYPE_VALUE_TESTNG)
                .defaultTestMethodContent(SOME_TEST_CODE));

        IMethod createTestMethod = testmethodCreator.createTestMethod(methodUnderTest.get()).getMethod();

        assertThat(createTestMethod.getElementName()).isEqualTo("testGetNumberOne");
        assertThat(createTestMethod.getSource()).startsWith("@Test");
        assertThat(createTestMethod.getSource()).contains(SOME_TEST_CODE);

        IMethod[] methods = testcaseType.get().getMethods();
        assertThat(methods).containsOnly(createTestMethod);
    }

    @Test
    public void createTestMethod_should_create_another_junit3_testmethod_when_called_with_testmethod() throws CoreException
    {
        MethodHandler existingTestMethod = testcaseType.addMethod("public void testGetNumberOne()");

        TestmethodCreator testmethodCreator = new TestmethodCreator(new TestMethodCreationSettings()
                .compilationUnit(testcaseType.getCompilationUnit())
                .testType(TEST_TYPE_VALUE_JUNIT_3)
                .defaultTestMethodContent(SOME_TEST_CODE));

        IMethod createTestMethod = testmethodCreator.createTestMethod(existingTestMethod.get()).getMethod();
        assertThat(createTestMethod.getElementName()).isEqualTo("testGetNumberOneSuffix");
        assertThat(createTestMethod.getSource()).doesNotContain("@Test").contains(SOME_TEST_CODE);

        IMethod[] methods = testcaseType.get().getMethods();
        assertThat(methods).hasSize(2);
    }

    @Test
    public void createTestMethod_should_create_another_junit4_testmethod_when_called_with_testmethod() throws CoreException
    {
        MethodHandler existingTestMethod = testcaseType.addMethod("public void getNumberOne()");

        TestmethodCreator testmethodCreator = new TestmethodCreator(new TestMethodCreationSettings()
                .compilationUnit(testcaseType.getCompilationUnit())
                .testType(TEST_TYPE_VALUE_JUNIT_4)
                .defaultTestMethodContent(SOME_TEST_CODE));

        IMethod createTestMethod = testmethodCreator.createTestMethod(existingTestMethod.get()).getMethod();
        assertThat(createTestMethod.getElementName()).isEqualTo("getNumberOneSuffix");
        assertThat(createTestMethod.getSource()).startsWith("@Test").contains(SOME_TEST_CODE);

        IMethod[] methods = testcaseType.get().getMethods();
        assertThat(methods).hasSize(2);
    }

    @Test
    public void createTestMethod_should_create_final_method_when_selected() throws Exception
    {
        TestmethodCreator testmethodCreator = new TestmethodCreator(new TestMethodCreationSettings()
                .compilationUnit(cutType.getCompilationUnit())
                .testType(TEST_TYPE_VALUE_JUNIT_4)
                .defaultTestMethodContent(SOME_TEST_CODE)
                .createFinalMethod(true)
                .createTasks(false));

        IMethod createTestMethod = testmethodCreator.createTestMethod(methodUnderTest.get()).getMethod();
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

        TestmethodCreator testmethodCreator = new TestmethodCreator(new TestMethodCreationSettings()
                .compilationUnit(cutType.getCompilationUnit())
                .testType(TEST_TYPE_VALUE_JUNIT_4)
                .defaultTestMethodContent(SOME_TEST_CODE)
                .createFinalMethod(true)
                .createTasks(false));

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

        TestmethodCreator testmethodCreator = new TestmethodCreator(new TestMethodCreationSettings()
                .compilationUnit(cutType.getCompilationUnit())
                .testType(TEST_TYPE_VALUE_JUNIT_4)
                .defaultTestMethodContent(SOME_TEST_CODE)
                .createFinalMethod(true)
                .createTasks(false));

        // when
        testmethodCreator.createTestMethods(asList(doSomethingWithoutArg.get(), doSomethingWithStringArray.get()));

        // then
        assertThat(testcaseType.get().getMethods()).hasSize(2).onProperty("elementName")
            // overloaded method: no parameter
            .contains("doSomething")
            // overloaded method: parameter types are used
            .contains("doSomethingStringArray");
    }

    @Test
    public void createTestMethod_should_not_create_another_test_method_when_called_with_method_under_test() throws CoreException
    {
        MethodHandler existingTestMethod = testcaseType.addMethod("public void testGetNumberOne()");

        TestmethodCreator testmethodCreator = new TestmethodCreator(new TestMethodCreationSettings()
                .compilationUnit(cutType.getCompilationUnit())
                .testType(TEST_TYPE_VALUE_JUNIT_3)
                .defaultTestMethodContent(SOME_TEST_CODE));

        MethodCreationResult result = testmethodCreator.createTestMethod(methodUnderTest.get());

        assertTrue(result.methodAlreadyExists());
        assertFalse(result.methodCreated());
        assertThat(result.getMethod()).isEqualTo(existingTestMethod.get());
    }
    
    @Test
    @Preferences(testClassNameTemplate = "${srcFile}Test", testSrcFolder = "test", testMethodPrefix = false, testType=TestType.JUNIT4)
    public void createTestMethod_should_not_add_comments_for_the_new_test_method_when_not_requested() throws Exception
    {
        TestmethodCreator testmethodCreator = new TestmethodCreator(new TestMethodCreationSettings()
                .compilationUnit(cutType.getCompilationUnit())
                .testType(TEST_TYPE_VALUE_JUNIT_4)
                .generateComments(false));

        MethodCreationResult result = testmethodCreator.createTestMethod(methodUnderTest.get());

        assertTrue(result.methodCreated());
        assertThat(result.getMethod().getJavadocRange()).isNull();
    }

    @Test
    @Preferences(testClassNameTemplate = "${srcFile}Test", testSrcFolder = "test", testMethodPrefix = false, testType=TestType.JUNIT4)
    public void createTestMethod_should_generate_comments_for_the_new_test_method_when_called_with_method_under_test() throws Exception
    {
        TestmethodCreator testmethodCreator = new TestmethodCreator(new TestMethodCreationSettings()
                .compilationUnit(cutType.getCompilationUnit())
                .testType(TEST_TYPE_VALUE_JUNIT_4)
                .defaultTestMethodContent("")
                .generateComments(true));

        MethodCreationResult result = testmethodCreator.createTestMethod(methodUnderTest.get());

        assertTrue(result.methodCreated());
        assertThat(result.getMethod().getJavadocRange()).isNotNull();
        assertThat(result.getMethod().getSource().replaceAll("\\s+", " "))
                .isEqualTo("/**" +
                           " * Test method for {@link testing.Hello#getNumberOne()}." +
                           " */" +
                           " @Test" +
                           " public void getNumberOne() throws Exception { }");
    }

    @Test
    @Preferences(testClassNameTemplate = "${srcFile}Test", testSrcFolder = "test", testMethodPrefix = false, testType=TestType.JUNIT4)
    public void createTestMethod_should_copy_comments_from_the_existing_test_method_when_called_with_that_test_method() throws Exception
    {
        MethodHandler existingTestMethod = testcaseType.addMethod("/** Some test comments. */ @Test public void getNumberOne()");

        TestmethodCreator testmethodCreator = new TestmethodCreator(new TestMethodCreationSettings()
                .compilationUnit(testcaseType.getCompilationUnit())
                .testType(TEST_TYPE_VALUE_JUNIT_4)
                .defaultTestMethodContent("")
                .generateComments(true));

        IMethod createTestMethod = testmethodCreator.createTestMethod(existingTestMethod.get()).getMethod();
        assertThat(createTestMethod.getJavadocRange()).isNotNull();
        assertThat(createTestMethod.getSource().replaceAll("\\s+", " "))
                .isEqualTo("/** Some test comments. */" +
                           " @Test" +
                           " public void getNumberOneSuffix() throws Exception { }");
    }
}
