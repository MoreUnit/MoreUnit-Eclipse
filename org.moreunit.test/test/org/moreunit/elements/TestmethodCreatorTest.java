package org.moreunit.elements;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.junit.Test;
import org.moreunit.preferences.PreferenceConstants;
import org.moreunit.test.SimpleProjectTestCase;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.Project;
import org.moreunit.test.context.TestType;
import org.moreunit.test.workspace.MethodHandler;
import org.moreunit.test.workspace.TypeHandler;
import org.moreunit.test.workspace.WorkspaceHelper;

/**
 * @author vera 02.08.2007 07:37:24
 */
@Preferences(testClassSuffixes="Test", testSourcefolder="test", testMethodPrefix=true)
@Project(mainCls="testing:Hello", testCls="testing:HelloTest", mainSrcFolder="src", testSrcFolder="test")
public class TestmethodCreatorTest extends ContextTestCase
{

    @Test
    public void testCreateFirstTestMethodJUnit3() throws CoreException
    {
        TypeHandler cutType = context.getCompilationUnitHandler("testing.Hello").getPrimaryTypeHandler();
        TypeHandler testcaseType = context.getCompilationUnitHandler("testing.HelloTest").getPrimaryTypeHandler();
        MethodHandler getNumberOneMethod = cutType.addMethod("public int getNumberOne()", "return 1");

        TestmethodCreator testmethodCreator = new TestmethodCreator(cutType.getCompilationUnit(), PreferenceConstants.TEST_TYPE_VALUE_JUNIT_3, "foo");
        IMethod createTestMethod = testmethodCreator.createTestMethod(getNumberOneMethod.get());

        assertEquals("testGetNumberOne", createTestMethod.getElementName());
        assertFalse(createTestMethod.getSource().startsWith("@Test"));
        assertTrue(createTestMethod.getSource().contains("foo"));
        IMethod[] methods = testcaseType.get().getMethods();
        assertEquals(1, methods.length);
        assertEquals(createTestMethod, methods[0]);
    }

    
    @Test
    public void testCreateFirstTestMethodJUnit4() throws CoreException
    {
        TypeHandler cutType = context.getCompilationUnitHandler("testing.Hello").getPrimaryTypeHandler();
        TypeHandler testcaseType = context.getCompilationUnitHandler("testing.HelloTest").getPrimaryTypeHandler();
        MethodHandler getNumberOneMethod = cutType.addMethod("public int getNumberOne()", "return 1");

        TestmethodCreator testmethodCreator = new TestmethodCreator(cutType.getCompilationUnit(), PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4, "foo");
        IMethod createTestMethod = testmethodCreator.createTestMethod(getNumberOneMethod.get());

        assertEquals("testGetNumberOne", createTestMethod.getElementName());
        assertTrue(createTestMethod.getSource().startsWith("@Test"));
        assertTrue(createTestMethod.getSource().contains("foo"));

        IMethod[] methods = testcaseType.get().getMethods();
        assertEquals(1, methods.length);
        assertEquals(createTestMethod, methods[0]);
    }

    @Test
    public void testCreateSecondTestMethodJUnit3() throws CoreException
    {
        TypeHandler cutType = context.getCompilationUnitHandler("testing.Hello").getPrimaryTypeHandler();
        TypeHandler testcaseType = context.getCompilationUnitHandler("testing.HelloTest").getPrimaryTypeHandler();
        cutType.addMethod("public int getNumberOne()", "return 1");
        MethodHandler testMethod = testcaseType.addMethod("public void testGetNumberOne()");

        TestmethodCreator testmethodCreator = new TestmethodCreator(testcaseType.getCompilationUnit(), PreferenceConstants.TEST_TYPE_VALUE_JUNIT_3, "foo");
        IMethod createTestMethod = testmethodCreator.createTestMethod(testMethod.get());
        assertEquals("testGetNumberOneSuffix", createTestMethod.getElementName());
        assertFalse(createTestMethod.getSource().startsWith("@Test"));
        assertTrue(createTestMethod.getSource().contains("foo"));

        IMethod[] methods = testcaseType.get().getMethods();
        assertEquals(2, methods.length);
    }

    @Test
    public void testCreateSecondTestMethodJUnit4() throws CoreException
    {
        TypeHandler cutType = context.getCompilationUnitHandler("testing.Hello").getPrimaryTypeHandler();
        TypeHandler testcaseType = context.getCompilationUnitHandler("testing.HelloTest").getPrimaryTypeHandler();
        cutType.addMethod("public int getNumberOne()", "return 1");
        MethodHandler testMethod = testcaseType.addMethod("public void testGetNumberOne()");

        TestmethodCreator testmethodCreator = new TestmethodCreator(testcaseType.getCompilationUnit(), PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4, "foo");
        IMethod createTestMethod = testmethodCreator.createTestMethod(testMethod.get());
        assertEquals("testGetNumberOneSuffix", createTestMethod.getElementName());
        assertTrue(createTestMethod.getSource().startsWith("@Test"));
        assertTrue(createTestMethod.getSource().contains("foo"));

        IMethod[] methods = testcaseType.get().getMethods();
        assertEquals(2, methods.length);
    }

}
