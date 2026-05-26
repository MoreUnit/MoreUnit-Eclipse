package org.moreunit.core.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.ui.IEditorPart;
import org.junit.jupiter.api.Test;
import org.moreunit.core.resources.SrcFile;

public class SelectedSrcFileTest
{

    @Test
    public void testFromEditor()
    {
        SrcFile mockFile = mock(SrcFile.class);
        IEditorPart mockEditor = mock(IEditorPart.class);
        ExecutionContext mockContext = mock(ExecutionContext.class);

        SelectedSrcFile selectedFile = SelectedSrcFile.fromEditor(mockFile, mockEditor, mockContext);

        assertNotNull(selectedFile);
        assertSame(mockFile, selectedFile.getSrcFile());
    }

    @Test
    public void testFromSelection()
    {
        SrcFile mockFile = mock(SrcFile.class);
        ExecutionContext mockContext = mock(ExecutionContext.class);

        SelectedSrcFile selectedFile = SelectedSrcFile.fromSelection(mockFile, mockContext);

        assertNotNull(selectedFile);
        assertSame(mockFile, selectedFile.getSrcFile());
    }

    @Test
    public void testNone()
    {
        SelectedSrcFile noneFile = SelectedSrcFile.none();

        assertNotNull(noneFile);
        assertNull(noneFile.getSrcFile());
        assertFalse(noneFile.isSupported());
    }

    @Test
    public void testIsSupportedWhenFileIsSupported()
    {
        SrcFile mockFile = mock(SrcFile.class);
        when(mockFile.isSupported()).thenReturn(true);
        ExecutionContext mockContext = mock(ExecutionContext.class);

        SelectedSrcFile selectedFile = SelectedSrcFile.fromSelection(mockFile, mockContext);

        assertTrue(selectedFile.isSupported());
    }

    @Test
    public void testIsSupportedWhenFileIsNotSupported()
    {
        SrcFile mockFile = mock(SrcFile.class);
        when(mockFile.isSupported()).thenReturn(false);
        ExecutionContext mockContext = mock(ExecutionContext.class);

        SelectedSrcFile selectedFile = SelectedSrcFile.fromSelection(mockFile, mockContext);

        assertFalse(selectedFile.isSupported());
    }

    @Test
    public void testIsSupportedWhenFileIsNull()
    {
        SelectedSrcFile noneFile = SelectedSrcFile.none();

        assertFalse(noneFile.isSupported());
    }

    @Test
    public void testCreateJumpContextThrowsExceptionWhenFileIsNull()
    {
        SelectedSrcFile noneFile = SelectedSrcFile.none();
        assertThrows(IllegalStateException.class, noneFile::createJumpContext);
    }
}
