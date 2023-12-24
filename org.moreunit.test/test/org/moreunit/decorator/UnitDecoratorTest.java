package org.moreunit.decorator;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.Test;
import org.moreunit.test.context.Context;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.configs.SimpleJUnit4Project;
import org.moreunit.test.workspace.CompilationUnitHandler;

@Context(SimpleJUnit4Project.class)
public class UnitDecoratorTest extends ContextTestCase
{
    private UnitDecorator unitDecorator = new UnitDecorator();
    private StringBuilder logBuilder = null; // irrelevant

    @Test
    public void getCompilationUnitIfIsTypeUnderTest_should_return_null_when_not_file()
    {
        IPackageFragmentRoot packageFragmentRoot = context.getProjectHandler().getMainSrcFolderHandler().get();
        assertThat(unitDecorator.getCompilationUnitIfIsTypeUnderTest(packageFragmentRoot.getResource(), logBuilder)).isNull();
    }

    @Test
    public void getCompilationUnitIfIsTypeUnderTest_should_return_null_when_testcase() throws JavaModelException
    {
        IResource testCaseResource = context.getCompilationUnit("org.SomeClassTest").getResource();
        assertThat(unitDecorator.getCompilationUnitIfIsTypeUnderTest(testCaseResource, logBuilder)).isNull();
    }

    @Test
    public void getCompilationUnitIfIsTypeUnderTest_should_not_return_null_when_not_testcase() throws JavaModelException
    {
        IResource classResource = context.getCompilationUnit("org.SomeClass").getResource();
        assertThat(unitDecorator.getCompilationUnitIfIsTypeUnderTest(classResource, logBuilder)).isNotNull();
    }

    @Test
    public void getCompilationUnitIfIsTypeUnderTest_should_return_null_when_unit_does_not_contain_type() throws JavaModelException
    {
        CompilationUnitHandler cu = context.getProjectHandler().getMainSrcFolderHandler().createCompilationUnit("package-info", "blah");
        IResource resource = cu.get().getResource();
        assertThat(unitDecorator.getCompilationUnitIfIsTypeUnderTest(resource, logBuilder)).isNull();
    }
}
