package org.moreunit.mock.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ConversionUtilsTest
{
    private ConversionUtils conversionUtils;

    @BeforeEach
    public void setUp()
    {
        conversionUtils = new ConversionUtils();
    }

    @Test
    public void adapt_should_return_adapter()
    {
        IAdaptable adaptable = mock(IAdaptable.class);
        String expectedAdapter = "adapter";
        when(adaptable.getAdapter(String.class)).thenReturn(expectedAdapter);

        String result = conversionUtils.adapt(adaptable, String.class);

        assertEquals(result, expectedAdapter);
    }

    @Test
    public void getFile_should_return_file_from_editor_input()
    {
        IEditorPart editorPart = mock(IEditorPart.class);
        IEditorInput editorInput = mock(IEditorInput.class);
        IFile expectedFile = mock(IFile.class);

        when(editorPart.getEditorInput()).thenReturn(editorInput);
        when(editorInput.getAdapter(IFile.class)).thenReturn(expectedFile);

        IFile result = conversionUtils.getFile(editorPart);

        assertEquals(result, expectedFile);
    }

    @Test
    public void getPrimaryType_from_CompilationUnit()
    {
        ICompilationUnit cu = mock(ICompilationUnit.class);
        IType expectedType = mock(IType.class);
        when(cu.findPrimaryType()).thenReturn(expectedType);

        IType result = conversionUtils.getPrimaryType(cu);

        assertEquals(result, expectedType);
    }
}
