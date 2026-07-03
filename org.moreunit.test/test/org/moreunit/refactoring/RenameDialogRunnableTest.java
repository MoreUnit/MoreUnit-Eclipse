package org.moreunit.refactoring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.junit.jupiter.api.Test;
import org.moreunit.elements.ClassTypeFacade;

public class RenameDialogRunnableTest
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
}
