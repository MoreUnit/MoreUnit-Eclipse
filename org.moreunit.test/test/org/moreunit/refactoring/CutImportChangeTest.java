package org.moreunit.refactoring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.junit.jupiter.api.Test;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Project;

public class CutImportChangeTest extends ContextTestCase
{
    @Test
    public void should_store_import_in_name()
    {
        ICompilationUnit cu = mock(ICompilationUnit.class);
        CutImportChange change = new CutImportChange("java.util.List", cu);

        assertTrue(change.getName().contains("java.util.List"));
    }

    @Test
    public void should_initialize_validation_data()
    {
        CutImportChange change = new CutImportChange("java.util.List", mock(ICompilationUnit.class));
        // should not throw despite null pm
        change.initializeValidationData(null);
    }

    @Project(mainCls = "com:Foo", testCls = "com:FooTest")
    @Test
    public void isValid_should_always_be_ok() throws CoreException
    {
        ICompilationUnit testCu = withImport("com.FooTest", "java.util.List");

        CutImportChange change = new CutImportChange("java.util.List", testCu);
        change.initializeValidationData(new NullProgressMonitor());

        RefactoringStatus status = change.isValid(new NullProgressMonitor());

        assertTrue(status.isOK());
        assertEquals(testCu, change.getModifiedElement());
    }

    @Project(mainCls = "com:Foo", testCls = "com:FooTest")
    @Test
    public void perform_should_remove_the_import_and_return_an_undo_change_that_re_adds_it() throws CoreException
    {
        ICompilationUnit testCu = withImport("com.FooTest", "java.util.List");

        Change undo = new CutImportChange("java.util.List", testCu).perform(new NullProgressMonitor());

        assertFalse(testCu.getImport("java.util.List").exists());
        assertNotNull(undo);
        assertTrue(undo instanceof CutImportChange);

        undo.perform(new NullProgressMonitor());

        assertTrue(testCu.getImport("java.util.List").exists());
    }

    @Project(mainCls = "com:Foo", testCls = "com:FooTest")
    @Test
    public void perform_should_do_nothing_when_the_import_does_not_exist() throws CoreException
    {
        ICompilationUnit testCu = withImport("com.FooTest", null);

        Change undo = new CutImportChange("java.lang.Thread", testCu).perform(new NullProgressMonitor());

        assertFalse(testCu.getImport("java.lang.Thread").exists());
        assertNotNull(undo);
    }

    private ICompilationUnit withImport(String compilationUnitName, String importName) throws CoreException
    {
        ICompilationUnit cu = context.getCompilationUnit(compilationUnitName);
        String importStatement = importName == null ? "" : "import %s;\n".formatted(importName);
        String packageName = compilationUnitName.substring(0, compilationUnitName.lastIndexOf('.'));
        String typeName = compilationUnitName.substring(compilationUnitName.lastIndexOf('.') + 1);
        cu.getBuffer().setContents("package %s;\n%spublic class %s {}\n".formatted(packageName, importStatement, typeName));
        cu.save(null, true);
        return cu;
    }
}
