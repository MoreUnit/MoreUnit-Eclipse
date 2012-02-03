package org.moreunit.decorator;

import static org.fest.assertions.Assertions.assertThat;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.Test;
import org.moreunit.test.context.Context;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.Project;
import org.moreunit.test.context.configs.SimpleJUnit4Project;

@Context(SimpleJUnit4Project.class)
public class UnitDecoratorTest extends ContextTestCase
{
    private UnitDecorator unitDecorator = new UnitDecorator();

    @Test
    public void tryToGetCompilationUnitFromElement_should_return_null_when_is_no_file()
    {
        IPackageFragmentRoot packageFragmentRoot = context.getProjectHandler().getMainSrcFolderHandler().get();
        assertThat(unitDecorator.tryToGetCompilationUnitFromElement(packageFragmentRoot.getResource())).isNull();
    }

    @Test
    @Preferences(testClassSuffixes="Test")
    @Project(mainCls = "org:SomeClass", testCls = "org:SomeClassTest")
    public void tryToGetCompilationUnitFromElement_should_return_null_when_testcase() throws JavaModelException
    {
        IResource testCaseResource = context.getCompilationUnit("org.SomeClassTest").getResource();
        assertThat(unitDecorator.tryToGetCompilationUnitFromElement(testCaseResource)).isNull();
    }

    @Test
    @Preferences(testClassSuffixes="Test")
    @Project(mainCls = "org:SomeClass", testCls = "org:SomeClassTest")
    public void tryToGetCompilationUnitFromElement_should_not_return_null_when_no_testcase() throws JavaModelException
    {
        IResource classResource = context.getCompilationUnit("org.SomeClass").getResource();
        assertThat(unitDecorator.tryToGetCompilationUnitFromElement(classResource)).isNotNull();
    }
}
