package org.moreunit.decorator;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.junit.jupiter.api.Test;
import org.moreunit.core.log.Logger;
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

    @Test
    public void decorate_should_add_overlay_when_class_has_test_case() throws JavaModelException
    {
        IResource classResource = context.getCompilationUnit("org.SomeClass").getResource();
        IDecoration decoration = mock(IDecoration.class);

        unitDecorator.decorate(classResource, decoration);

        verify(decoration).addOverlay(any(ImageDescriptor.class), eq(IDecoration.TOP_RIGHT));
    }

    @Test
    public void decorate_should_not_add_overlay_when_class_has_no_test_case() throws CoreException
    {
        context.getProjectHandler().getMainSrcFolderHandler().createClass("org.ClassWithoutTest");
        IResource classResource = context.getCompilationUnit("org.ClassWithoutTest").getResource();
        IDecoration decoration = mock(IDecoration.class);

        unitDecorator.decorate(classResource, decoration);

        verify(decoration, never()).addOverlay(any(ImageDescriptor.class), anyInt());
    }

    @Test
    public void decorate_should_not_decoration_when_element_is_not_a_file()
    {
        IPackageFragmentRoot packageFragmentRoot = context.getProjectHandler().getMainSrcFolderHandler().get();
        IDecoration decoration = mock(IDecoration.class);

        unitDecorator.decorate(packageFragmentRoot.getResource(), decoration);

        verifyNoInteractions(decoration);
    }

    @Test
    public void decorate_should_trace_its_decisions_when_trace_is_enabled() throws CoreException
    {
        Logger logger = mock(Logger.class);
        when(logger.traceEnabled()).thenReturn(true);
        UnitDecorator tracingDecorator = new UnitDecorator(logger);

        IResource classResource = context.getCompilationUnit("org.SomeClass").getResource();
        IDecoration decoration = mock(IDecoration.class);

        tracingDecorator.decorate(classResource, decoration);

        verify(logger, atLeastOnce()).trace(anyString());
        verify(decoration).addOverlay(any(ImageDescriptor.class), eq(IDecoration.TOP_RIGHT));
    }

    @Test
    public void decorate_should_trace_when_element_is_not_a_file_when_trace_is_enabled()
    {
        Logger logger = mock(Logger.class);
        when(logger.traceEnabled()).thenReturn(true);
        UnitDecorator tracingDecorator = new UnitDecorator(logger);

        IPackageFragmentRoot packageFragmentRoot = context.getProjectHandler().getMainSrcFolderHandler().get();

        tracingDecorator.decorate(packageFragmentRoot.getResource(), mock(IDecoration.class));

        verify(logger, atLeastOnce()).trace(anyString());
    }

    @Test
    public void getCompilationUnitIfIsTypeUnderTest_should_return_null_when_resource_is_not_a_java_element() throws CoreException
    {
        org.eclipse.core.resources.IFolder folder = context.getProjectHandler().get().getProject().getFolder("nonjava");
        if(! folder.exists())
        {
            folder.create(true, true, null);
        }
        org.eclipse.core.resources.IFile textFile = folder.getFile("readme.txt");
        if(! textFile.exists())
        {
            textFile.create(new java.io.ByteArrayInputStream("hello".getBytes()), true, null);
        }

        assertNull(unitDecorator.getCompilationUnitIfIsTypeUnderTest(textFile, logBuilder));
    }

    @Test
    public void getCompilationUnitIfIsTypeUnderTest_should_return_null_when_java_element_is_not_a_compilation_unit() throws CoreException
    {
        org.eclipse.core.resources.IFolder folder = ((org.eclipse.core.resources.IFolder) context.getProjectHandler().getMainSrcFolderHandler().get().getResource()).getFolder("org");
        org.eclipse.core.resources.IFile classFile = folder.getFile("SomeClass.class");
        if(! classFile.exists())
        {
            classFile.create(new java.io.ByteArrayInputStream(new byte[0]), true, null);
        }

        assertNull(unitDecorator.getCompilationUnitIfIsTypeUnderTest(classFile, logBuilder));
    }

    @Test
    public void getCompilationUnitIfIsTypeUnderTest_should_trace_every_rejection_when_trace_is_enabled() throws CoreException
    {
        Logger logger = mock(Logger.class);
        when(logger.traceEnabled()).thenReturn(true);
        UnitDecorator tracingDecorator = new UnitDecorator(logger);

        // not a file
        IPackageFragmentRoot packageFragmentRoot = context.getProjectHandler().getMainSrcFolderHandler().get();
        tracingDecorator.getCompilationUnitIfIsTypeUnderTest(packageFragmentRoot.getResource(), new StringBuilder("x"));
        // not a Java element
        org.eclipse.core.resources.IFolder folder = context.getProjectHandler().get().getProject().getFolder("nonjava2");
        if(! folder.exists())
        {
            folder.create(true, true, null);
        }
        org.eclipse.core.resources.IFile textFile = folder.getFile("notes.txt");
        if(! textFile.exists())
        {
            textFile.create(new java.io.ByteArrayInputStream("hello".getBytes()), true, null);
        }
        tracingDecorator.getCompilationUnitIfIsTypeUnderTest(textFile, new StringBuilder("x"));
        // not a compilation unit
        org.eclipse.core.resources.IFolder srcFolder = ((org.eclipse.core.resources.IFolder) context.getProjectHandler().getMainSrcFolderHandler().get().getResource()).getFolder("org");
        org.eclipse.core.resources.IFile classFile = srcFolder.getFile("Other.class");
        if(! classFile.exists())
        {
            classFile.create(new java.io.ByteArrayInputStream(new byte[0]), true, null);
        }
        tracingDecorator.getCompilationUnitIfIsTypeUnderTest(classFile, new StringBuilder("x"));
        // test case
        IResource testCaseResource = context.getCompilationUnit("org.SomeClassTest").getResource();
        tracingDecorator.getCompilationUnitIfIsTypeUnderTest(testCaseResource, new StringBuilder("x"));

        verify(logger, atLeastOnce()).trace(org.mockito.ArgumentMatchers.contains("is a not a file"));
        verify(logger, atLeastOnce()).trace(org.mockito.ArgumentMatchers.contains("is a not a Java element"));
        verify(logger, atLeastOnce()).trace(org.mockito.ArgumentMatchers.contains("is a not a compilation unit"));
        verify(logger, atLeastOnce()).trace(org.mockito.ArgumentMatchers.contains("is a test case"));
    }

    @Test
    public void decorate_should_trace_when_class_has_no_test_case_when_trace_is_enabled() throws CoreException
    {
        Logger logger = mock(Logger.class);
        when(logger.traceEnabled()).thenReturn(true);
        UnitDecorator tracingDecorator = new UnitDecorator(logger);

        context.getProjectHandler().getMainSrcFolderHandler().createClass("org.ClassWithoutTest2");
        IResource classResource = context.getCompilationUnit("org.ClassWithoutTest2").getResource();
        IDecoration decoration = mock(IDecoration.class);

        tracingDecorator.decorate(classResource, decoration);

        verify(logger, atLeastOnce()).trace(org.mockito.ArgumentMatchers.contains("has no test cases"));
        verify(decoration, never()).addOverlay(any(ImageDescriptor.class), anyInt());
    }

    @Test
    public void getUnitDecorator_should_not_throw_even_when_decorator_is_not_registered()
    {
        // must not throw whether or not the decorator is enabled in the running workbench
        org.moreunit.decorator.UnitDecorator.getUnitDecorator();
    }
}
