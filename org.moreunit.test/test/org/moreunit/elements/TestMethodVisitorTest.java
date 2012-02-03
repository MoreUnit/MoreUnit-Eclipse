package org.moreunit.elements;

/**
 * @author vera

 *
 * 23.05.2006 19:54:02
 */
import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.junit.Before;
import org.junit.Test;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Project;
import org.moreunit.test.workspace.MethodHandler;
import org.moreunit.test.workspace.TypeHandler;

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
    public void getTestMethods_should_detect_annotated_method_as_test() throws JavaModelException
    {
        String methodSource = "@Test \n public int getOne() { return 1; }";
        IMethod annotationTestMethod = testcaseType.get().createMethod(methodSource, null, true, null);
        TestMethodVisitor visitor = new TestMethodVisitor(testcaseType.get());
        List<MethodDeclaration> testMethods = visitor.getTestMethods();
        assertThat(testMethods).hasSize(1);
        assertThat(testMethods.get(0).getName().getFullyQualifiedName()).isEqualTo(annotationTestMethod.getElementName());
    }

    @Test
    public void getTestMethods_should_detect_test_prefix_as_test() throws JavaModelException
    {
        MethodHandler testMethodWithPrefix = testcaseType.addMethod("public int testGetTwo()");
        TestMethodVisitor visitor = new TestMethodVisitor(testcaseType.get());
        List<MethodDeclaration> testMethods = visitor.getTestMethods();
        assertThat(testMethods).hasSize(1);
        assertThat(testMethods.get(0).getName().getFullyQualifiedName()).isEqualTo(testMethodWithPrefix.get().getElementName());
    }

    @Test
    public void getTestMethods_should_detect_annotated_and_test_prefix_as_test() throws JavaModelException
    {
        String methodSource = "@Test \n public void testGetOne() {  }";
        IMethod annotationTestMethod = testcaseType.get().createMethod(methodSource, null, true, null);
        TestMethodVisitor visitor = new TestMethodVisitor(testcaseType.get());
        List<MethodDeclaration> testMethods = visitor.getTestMethods();
        assertThat(testMethods).hasSize(1);
        assertThat(testMethods.get(0).getName().getFullyQualifiedName()).isEqualTo(annotationTestMethod.getElementName());
    }

    @Test
    public void getTestMethods_should_return_empty_list_when_type_doesn_not_contain_testmethods() throws JavaModelException
    {
        TestMethodVisitor visitor = new TestMethodVisitor(testcaseType.get());
        List<MethodDeclaration> testMethods = visitor.getTestMethods();
        assertThat(testMethods).isEmpty();
    }
}
