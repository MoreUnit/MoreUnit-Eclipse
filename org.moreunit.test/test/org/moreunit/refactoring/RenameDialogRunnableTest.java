package org.moreunit.refactoring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

public class RenameDialogRunnableTest extends org.moreunit.test.context.ContextTestCase
{
    @Test
    public void constructor_should_store_parameters_and_create_diviner()
    {
        ClassTypeFacade javaFile = mock(ClassTypeFacade.class);
        ICompilationUnit cu = mock(ICompilationUnit.class);
        when(javaFile.getCompilationUnit()).thenReturn(cu);

        IMethod method = mock(IMethod.class);

        RenameDialogRunnable runnable = new RenameDialogRunnable(javaFile, method, "newName");

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
        IMethod method = context.getPrimaryTypeHandler("org.SomeClass").addMethod("public int getNumberOne()", "return 1;").get();
        context.getPrimaryTypeHandler("org.SomeClassTest").addMethod("public void testGetNumberOne()", "");

        IWorkbenchPage page = org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        org.eclipse.core.resources.IFile file = (org.eclipse.core.resources.IFile) context.getCompilationUnit("org.SomeClass").getResource();
        IEditorPart editor = page.openEditor(new org.eclipse.ui.part.FileEditorInput(file), "org.eclipse.ui.DefaultTextEditor", true);
        try
        {
            assertNotNull(editor);
            awaitActiveEditor(page);

            Display display = Display.getDefault();
            java.util.Set<Shell> knownShells = org.moreunit.test.support.DialogHelper.knownShells(display);
            display.asyncExec(org.moreunit.test.support.DialogHelper.closerFor(display, knownShells, Shell::close, 2000));

            ClassTypeFacade facade = new ClassTypeFacade(context.getCompilationUnit("org.SomeClass"));
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
        long deadline = System.currentTimeMillis() + 10_000;
        while (page.getActiveEditor() == null && System.currentTimeMillis() < deadline)
        {
            while (Display.getDefault().readAndDispatch())
            {
            }
            Thread.sleep(20);
        }
    }
}
