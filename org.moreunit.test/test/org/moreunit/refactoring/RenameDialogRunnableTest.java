package org.moreunit.refactoring;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.junit.jupiter.api.Test;
import org.moreunit.elements.ClassTypeFacade;
import org.moreunit.util.TestMethodDiviner;
import org.moreunit.util.TestMethodDivinerFactory;

public class RenameDialogRunnableTest extends org.moreunit.test.context.ContextTestCase
{
    @Test
    public void constructor_should_store_parameters_and_create_diviner()
    {
        final ClassTypeFacade javaFile = mock(ClassTypeFacade.class);
        final ICompilationUnit cu = mock(ICompilationUnit.class);
        when(javaFile.getCompilationUnit()).thenReturn(cu);

        final IMethod method = mock(IMethod.class);

        final RenameDialogRunnable runnable = new RenameDialogRunnable(javaFile, method, "newName");

        assertEquals(method, runnable.renamedMethod);
        assertEquals("newName", runnable.newMethodName);
        assertNotNull(runnable.testMethodDivinerFactory);
        assertNotNull(runnable.testMethodDiviner);
    }

    @org.moreunit.test.context.Context(value = org.moreunit.test.context.configs.SimpleJUnit4Project.class, //
            preferences = @org.moreunit.test.context.Preferences(testClassNameTemplate = "${srcFile}Test", testSrcFolder = "test", testMethodPrefix = true))
    @org.junit.jupiter.api.Test
    public void run_should_open_the_rename_dialog_and_do_nothing_when_it_is_cancelled() throws Exception
    {
        final IMethod method = context.getPrimaryTypeHandler("org.SomeClass").addMethod("public int getNumberOne()", "return 1;").get();
        context.getPrimaryTypeHandler("org.SomeClassTest").addMethod("public void testGetNumberOne()", "");

        final IWorkbenchPage page = org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        final org.eclipse.core.resources.IFile file = (org.eclipse.core.resources.IFile) context.getCompilationUnit("org.SomeClass").getResource();
        final IEditorPart editor = page.openEditor(new org.eclipse.ui.part.FileEditorInput(file), "org.eclipse.ui.DefaultTextEditor", true);
        try
        {
            assertNotNull(editor);
            awaitActiveEditor(page);

            final Display display = Display.getDefault();
            final java.util.Set<Shell> knownShells = org.moreunit.test.support.DialogHelper.knownShells(display);
            display.asyncExec(org.moreunit.test.support.DialogHelper.closerFor(display, knownShells, Shell::close, 2000));

            final ClassTypeFacade facade = new ClassTypeFacade(context.getCompilationUnit("org.SomeClass"));
            new RenameDialogRunnable(facade, method, "getNumberTwo").run();

            // dialog was cancelled: no test method must have been renamed
            assertEquals("testGetNumberOne", context.getPrimaryTypeHandler("org.SomeClassTest").get().getMethods()[0].getElementName());
        }
        finally
        {
            page.closeAllEditors(false);
        }
    }

    private void awaitActiveEditor(IWorkbenchPage page) throws InterruptedException
    {
        final long deadline = System.currentTimeMillis() + 10_000;
        while (page.getActiveEditor() == null && System.currentTimeMillis() < deadline)
        {
            while (Display.getDefault().readAndDispatch())
            {
            }
            Thread.sleep(20);
        }
    }

    @org.moreunit.test.context.Context(value = org.moreunit.test.context.configs.SimpleJUnit4Project.class, //
            preferences = @org.moreunit.test.context.Preferences(testClassNameTemplate = "${srcFile}Test", testSrcFolder = "test", testMethodPrefix = true))
    @org.junit.jupiter.api.Test
    public void run_should_rename_test_methods_when_dialog_is_confirmed() throws Exception
    {
        // name the test method through the same diviner the production code
        // uses, so the lookup succeeds whatever naming convention is active
        final TestMethodDiviner diviner = new TestMethodDivinerFactory(context.getCompilationUnit("org.SomeClass")).create();
        final String testMethodName = diviner.getTestMethodNameFromMethodName("getNumberOne");
        final IMethod method = context.getPrimaryTypeHandler("org.SomeClass").addMethod("public int getNumberOne()", "return 1;").get();
        context.getPrimaryTypeHandler("org.SomeClassTest").addMethod("public void " + testMethodName + "()", "");

        final IWorkbenchPage page = org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        final org.eclipse.core.resources.IFile file = (org.eclipse.core.resources.IFile) context.getCompilationUnit("org.SomeClass").getResource();
        final IEditorPart editor = page.openEditor(new org.eclipse.ui.part.FileEditorInput(file), "org.eclipse.ui.DefaultTextEditor", true);
        try
        {
            assertNotNull(editor);
            awaitActiveEditor(page);

            final Display display = Display.getDefault();
            final java.util.Set<Shell> knownShells = org.moreunit.test.support.DialogHelper.knownShells(display);
            display.asyncExec(org.moreunit.test.support.DialogHelper.closerFor(display, knownShells, org.moreunit.test.support.DialogHelper::confirmOkButton, 2000));

            final ClassTypeFacade facade = new ClassTypeFacade(context.getCompilationUnit("org.SomeClass"));
            assertFalse(facade.getCorrespondingTestCases().isEmpty(), "setup: test case should be found");
            assertFalse(facade.getCorrespondingTestMethodsByName(method).isEmpty(), "setup: corresponding test method should be found");
            new RenameDialogRunnable(facade, method, "getNumberTwo").run();

            final String expectedRenamedTest = diviner.getTestMethodNameAfterRename("getNumberOne", "getNumberTwo", testMethodName);
            assertEquals(expectedRenamedTest, context.getPrimaryTypeHandler("org.SomeClassTest").get().getMethods()[0].getElementName());
        }
        finally
        {
            page.closeAllEditors(false);
        }
    }

    @org.moreunit.test.context.Context(value = org.moreunit.test.context.configs.SimpleJUnit4Project.class, //
            preferences = @org.moreunit.test.context.Preferences(testClassNameTemplate = "${srcFile}Test", testSrcFolder = "test", testMethodPrefix = true))
    @org.junit.jupiter.api.Test
    public void run_should_log_and_continue_when_rename_of_a_test_method_fails() throws Exception
    {
        final IMethod method = context.getPrimaryTypeHandler("org.SomeClass").addMethod("public int getNumberOne()", "return 1;").get();
        context.getPrimaryTypeHandler("org.SomeClassTest").addMethod("public void testGetNumberOne()", "");

        final IWorkbenchPage page = org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        final org.eclipse.core.resources.IFile file = (org.eclipse.core.resources.IFile) context.getCompilationUnit("org.SomeClass").getResource();
        final IEditorPart editor = page.openEditor(new org.eclipse.ui.part.FileEditorInput(file), "org.eclipse.ui.DefaultTextEditor", true);
        try
        {
            assertNotNull(editor);
            awaitActiveEditor(page);

            final Display display = Display.getDefault();
            final java.util.Set<Shell> knownShells = org.moreunit.test.support.DialogHelper.knownShells(display);
            display.asyncExec(org.moreunit.test.support.DialogHelper.closerFor(display, knownShells, org.moreunit.test.support.DialogHelper::confirmOkButton, 2000));

            final ClassTypeFacade facade = new ClassTypeFacade(context.getCompilationUnit("org.SomeClass"));
            final RenameDialogRunnable runnable = new RenameDialogRunnable(facade, method, "getNumberTwo");
            final TestMethodDiviner failingDiviner = mock(TestMethodDiviner.class);
            when(failingDiviner.getTestMethodNameAfterRename(anyString(), anyString(), anyString())).thenThrow(new RuntimeException("boom"));
            runnable.testMethodDiviner = failingDiviner;

            assertDoesNotThrow(runnable::run);

            // the failure was swallowed: no test method must have been renamed
            assertEquals("testGetNumberOne", context.getPrimaryTypeHandler("org.SomeClassTest").get().getMethods()[0].getElementName());
        }
        finally
        {
            page.closeAllEditors(false);
        }
    }

    @org.moreunit.test.context.Context(value = org.moreunit.test.context.configs.SimpleJUnit4Project.class, //
            preferences = @org.moreunit.test.context.Preferences(testClassNameTemplate = "${srcFile}Test", testSrcFolder = "test", testMethodPrefix = true))
    @org.junit.jupiter.api.Test
    public void run_should_do_nothing_when_confirmed_without_corresponding_test_methods() throws Exception
    {
        final IMethod method = context.getPrimaryTypeHandler("org.SomeClass").addMethod("public int getNumberThree()", "return 3;").get();

        final IWorkbenchPage page = org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        final org.eclipse.core.resources.IFile file = (org.eclipse.core.resources.IFile) context.getCompilationUnit("org.SomeClass").getResource();
        final IEditorPart editor = page.openEditor(new org.eclipse.ui.part.FileEditorInput(file), "org.eclipse.ui.DefaultTextEditor", true);
        try
        {
            assertNotNull(editor);
            awaitActiveEditor(page);

            final Display display = Display.getDefault();
            final java.util.Set<Shell> knownShells = org.moreunit.test.support.DialogHelper.knownShells(display);
            display.asyncExec(org.moreunit.test.support.DialogHelper.closerFor(display, knownShells, org.moreunit.test.support.DialogHelper::confirmOkButton, 2000));

            final ClassTypeFacade facade = new ClassTypeFacade(context.getCompilationUnit("org.SomeClass"));
            new RenameDialogRunnable(facade, method, "getNumberFour").run();

            assertEquals(0, context.getPrimaryTypeHandler("org.SomeClassTest").get().getMethods().length);
        }
        finally
        {
            page.closeAllEditors(false);
        }
    }
}
