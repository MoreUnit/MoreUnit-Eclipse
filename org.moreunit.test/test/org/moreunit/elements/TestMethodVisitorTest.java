package org.moreunit.elements;

/**
 * @author vera
 *
 * 23.05.2006 19:54:02
 */
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moreunit.test.SimpleProjectTestCase;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Project;
import org.moreunit.test.workspace.MethodHandler;
import org.moreunit.test.workspace.TypeHandler;
import org.moreunit.test.workspace.WorkspaceHelper;

@Project(testCls="testing:HelloTest")
public class TestMethodVisitorTest extends ContextTestCase
{

    TypeHandler testcaseType;

    @Before
    public void init() throws Exception
    {
        testcaseType = context.getCompilationUnitHandler("testing.HelloTest").getPrimaryTypeHandler();
    }
    
    @Test
    public void testGetTestMethodsOnlyTestAnnotation() throws JavaModelException
    {
        String methodSource = "@Test \n public int getOne() { return 1; }";
        IMethod annotationTestMethod = testcaseType.get().createMethod(methodSource, null, true, null);
        TestMethodVisitor visitor = new TestMethodVisitor(testcaseType.get());
        List<MethodDeclaration> testMethods = visitor.getTestMethods();
        assertEquals(1, testMethods.size());
        WorkspaceHelper.assertSameMethodName(annotationTestMethod, testMethods.get(0));
    }

    @Test
    public void testGetTestMethodsTestPrefix() throws JavaModelException
    {
        MethodHandler testMethodWithPrefix = testcaseType.addMethod("public int testGetTwo()");
        TestMethodVisitor visitor = new TestMethodVisitor(testcaseType.get());
        List<MethodDeclaration> testMethods = visitor.getTestMethods();
        assertEquals(1, testMethods.size());
        WorkspaceHelper.assertSameMethodName(testMethodWithPrefix.get(), testMethods.get(0));
    }

    @Test
    public void testGetTestMethodsTestAnnotationAndTestPrefix() throws JavaModelException
    {
        String methodSource = "@Test \n public void testGetOne() {  }";
        IMethod annotationTestMethod = testcaseType.get().createMethod(methodSource, null, true, null);
        TestMethodVisitor visitor = new TestMethodVisitor(testcaseType.get());
        List<MethodDeclaration> testMethods = visitor.getTestMethods();
        assertEquals(1, testMethods.size());
        WorkspaceHelper.assertSameMethodName(annotationTestMethod, testMethods.get(0));
    }

    @Test
    public void testGetTestMethodsNoTestMethod() throws JavaModelException
    {
        TestMethodVisitor visitor = new TestMethodVisitor(testcaseType.get());
        List<MethodDeclaration> testMethods = visitor.getTestMethods();
        assertEquals(0, testMethods.size());
    }
}
