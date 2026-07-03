package org.moreunit.refactoring;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.eclipse.jdt.core.ICompilationUnit;
import org.junit.jupiter.api.Test;

public class CutImportChangeTest
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
}
