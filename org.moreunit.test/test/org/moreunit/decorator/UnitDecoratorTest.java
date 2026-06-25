package org.moreunit.decorator;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.jupiter.api.Test;
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
        assertNull(unitDecorator.getCompilationUnitIfIsTypeUnderTest(packageFragmentRoot.getResource(), logBuilder));
    }

    @Test
    public void getCompilationUnitIfIsTypeUnderTest_should_return_null_when_testcase() throws JavaModelException
    {
        IResource testCaseResource = context.getCompilationUnit("org.SomeClassTest").getResource();
        assertNull(unitDecorator.getCompilationUnitIfIsTypeUnderTest(testCaseResource, logBuilder));
    }

    @Test
    public void getCompilationUnitIfIsTypeUnderTest_should_not_return_null_when_not_testcase() throws JavaModelException
    {
        IResource classResource = context.getCompilationUnit("org.SomeClass").getResource();
        assertNotNull(unitDecorator.getCompilationUnitIfIsTypeUnderTest(classResource, logBuilder));
    }

    @Test
    public void getCompilationUnitIfIsTypeUnderTest_should_return_null_when_unit_does_not_contain_type() throws JavaModelException
    {
        CompilationUnitHandler cu = context.getProjectHandler().getMainSrcFolderHandler().createCompilationUnit("package-info", "blah");
        IResource resource = cu.get().getResource();
        assertNull(unitDecorator.getCompilationUnitIfIsTypeUnderTest(resource, logBuilder));
    }
}
