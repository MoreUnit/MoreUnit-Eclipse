package org.moreunit.decorator;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moreunit.test.SimpleProjectTestCase;
import org.moreunit.test.context.Context;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.Project;
import org.moreunit.test.context.configs.SimpleJUnit3Project;
import org.moreunit.test.context.configs.SimpleJUnit4Project;
import org.moreunit.test.workspace.WorkspaceHelper;

@Context(SimpleJUnit4Project.class)
public class UnitDecoratorTest extends ContextTestCase
{
    private UnitDecorator unitDecorator = new UnitDecorator();

    @Test
    public void testTryToGetCompilationUnitFromElementIsNoFile()
    {
        IPackageFragmentRoot packageFragmentRoot = context.getProjectHandler().getMainSrcFolderHandler().get();
        assertNull(unitDecorator.tryToGetCompilationUnitFromElement(packageFragmentRoot.getResource()));
    }

    @Test
    @Preferences(testClassSuffixes="Test")
    @Project(mainCls = "org:SomeClass", testCls = "org:SomeClassTest")
    public void testTryToGetCompilationUnitFromElementIsTestCase() throws JavaModelException
    {
        IResource testCaseResource = context.getCompilationUnit("org.SomeClassTest").getResource();
        assertNull(unitDecorator.tryToGetCompilationUnitFromElement(testCaseResource));
    }

    @Test
    @Preferences(testClassSuffixes="Test")
    @Project(mainCls = "org:SomeClass", testCls = "org:SomeClassTest")
    public void testTryToGetCompilationUnitFromElementIsNotTestCase() throws JavaModelException
    {
        IResource classResource = context.getCompilationUnit("org.SomeClass").getResource();
        assertNotNull(unitDecorator.tryToGetCompilationUnitFromElement(classResource));
    }
}
