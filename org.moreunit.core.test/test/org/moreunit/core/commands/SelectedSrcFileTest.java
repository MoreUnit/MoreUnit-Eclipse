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
        final SrcFile mockFile = mock(SrcFile.class);
        final IEditorPart mockEditor = mock(IEditorPart.class);
        final ExecutionContext mockContext = mock(ExecutionContext.class);

        final SelectedSrcFile selectedFile = SelectedSrcFile.fromEditor(mockFile, mockEditor, mockContext);

        assertNotNull(selectedFile);
        assertSame(mockFile, selectedFile.getSrcFile());
    }

    @Test
    public void testFromSelection()
    {
        final SrcFile mockFile = mock(SrcFile.class);
        final ExecutionContext mockContext = mock(ExecutionContext.class);

        final SelectedSrcFile selectedFile = SelectedSrcFile.fromSelection(mockFile, mockContext);

        assertNotNull(selectedFile);
        assertSame(mockFile, selectedFile.getSrcFile());
    }

    @Test
    public void testNone()
    {
        final SelectedSrcFile noneFile = SelectedSrcFile.none();

        assertNotNull(noneFile);
        assertNull(noneFile.getSrcFile());
        assertFalse(noneFile.isSupported());
    }

    @Test
    public void testIsSupportedWhenFileIsSupported()
    {
        final SrcFile mockFile = mock(SrcFile.class);
        when(mockFile.isSupported()).thenReturn(true);
        final ExecutionContext mockContext = mock(ExecutionContext.class);

        final SelectedSrcFile selectedFile = SelectedSrcFile.fromSelection(mockFile, mockContext);

        assertTrue(selectedFile.isSupported());
    }

    @Test
    public void testIsSupportedWhenFileIsNotSupported()
    {
        final SrcFile mockFile = mock(SrcFile.class);
        when(mockFile.isSupported()).thenReturn(false);
        final ExecutionContext mockContext = mock(ExecutionContext.class);

        final SelectedSrcFile selectedFile = SelectedSrcFile.fromSelection(mockFile, mockContext);

        assertFalse(selectedFile.isSupported());
    }

    @Test
    public void testIsSupportedWhenFileIsNull()
    {
        final SelectedSrcFile noneFile = SelectedSrcFile.none();

        assertFalse(noneFile.isSupported());
    }

    @Test
    public void testCreateJumpContextThrowsExceptionWhenFileIsNull()
    {
        final SelectedSrcFile noneFile = SelectedSrcFile.none();
        assertThrows(IllegalStateException.class, noneFile::createJumpContext);
    }
}
