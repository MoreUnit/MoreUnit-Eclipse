package org.moreunit.elements;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.moreunit.test.context.Context;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.configs.SimpleJUnit3Project;
import org.moreunit.test.workspace.MethodHandler;
import org.moreunit.test.workspace.TypeHandler;

@Context(SimpleJUnit3Project.class)
public class EditorPartFacadeTest extends ContextTestCase
{
    private TypeHandler cutHandler;
    private MethodHandler methodUnderTest;

    @BeforeEach
    public void setUp() throws Exception
    {
        cutHandler = context.getPrimaryTypeHandler("org.SomeClass");
        methodUnderTest = cutHandler.addMethod("public int getNumberOne()", "return 1;");
    }

    private IEditorPart mockEditorPartShowing(IFile file)
    {
        final IEditorInput editorInput = mock(IEditorInput.class);
        when(editorInput.getAdapter(IFile.class)).thenReturn(file);

        final IEditorPart editorPart = mock(IEditorPart.class);
        when(editorPart.getEditorInput()).thenReturn(editorInput);
        return editorPart;
    }

    private EditorPartFacade facadeWithCursorInMethod(IEditorPart editorPart, int offset)
    {
        final IWorkbenchPartSite site = mock(IWorkbenchPartSite.class);
        final ISelectionProvider selectionProvider = mock(ISelectionProvider.class);
        final ITextSelection selection = mock(ITextSelection.class);
        when(selection.getOffset()).thenReturn(offset);
        when(selectionProvider.getSelection()).thenReturn(selection);
        when(site.getSelectionProvider()).thenReturn(selectionProvider);
        when(editorPart.getSite()).thenReturn(site);

        return new EditorPartFacade(editorPart);
    }

    @Test
    public void should_return_file_and_compilation_unit_of_the_edited_file()
    {
        final IFile file = (IFile) cutHandler.getCompilationUnit().getResource();
        final EditorPartFacade facade = new EditorPartFacade(mockEditorPartShowing(file));

        assertSame(file, facade.getFile());
        assertTrue(facade.isJavaLikeFile());
        assertEquals(cutHandler.getCompilationUnit(), facade.getCompilationUnit());
        assertEquals(context.getProjectHandler().get(), facade.getJavaProject());
    }

    @Test
    public void should_return_given_editor_part()
    {
        final IEditorPart editorPart = mockEditorPartShowing((IFile) cutHandler.getCompilationUnit().getResource());

        assertSame(editorPart, new EditorPartFacade(editorPart).getEditorPart());
    }

    @Test
    public void getTextSelection_should_return_selection_from_editor_site() throws Exception
    {
        final IEditorPart editorPart = mockEditorPartShowing((IFile) cutHandler.getCompilationUnit().getResource());
        final int offset = methodUnderTest.get().getNameRange().getOffset() + 1;
        final EditorPartFacade facade = facadeWithCursorInMethod(editorPart, offset);

        assertEquals(offset, facade.getTextSelection().getOffset());
    }

    @Test
    public void getMethodUnderCursorPosition_should_return_method_at_cursor_position() throws Exception
    {
        final IEditorPart editorPart = mockEditorPartShowing((IFile) cutHandler.getCompilationUnit().getResource());
        final int offset = methodUnderTest.get().getNameRange().getOffset() + 1;
        final EditorPartFacade facade = facadeWithCursorInMethod(editorPart, offset);

        assertEquals(methodUnderTest.get(), facade.getMethodUnderCursorPosition());
    }

    @Test
    public void getMethodUnderCursorPosition_should_return_null_when_cursor_is_not_in_a_method()
    {
        final IEditorPart editorPart = mockEditorPartShowing((IFile) cutHandler.getCompilationUnit().getResource());
        // offset 0 is on the package declaration, not within a method
        final EditorPartFacade facade = facadeWithCursorInMethod(editorPart, 0);

        assertNull(facade.getMethodUnderCursorPosition());
    }

    @Test
    public void getMethodUnderCursorPosition_should_return_null_when_no_file_is_edited()
    {
        final EditorPartFacade facade = facadeWithCursorInMethod(mockEditorPartShowing(null), 0);

        assertNull(facade.getMethodUnderCursorPosition());
    }

    @Test
    public void getFirstNonAnonymousMethodSurroundingCursorPosition_should_return_method_at_cursor_position() throws Exception
    {
        final IEditorPart editorPart = mockEditorPartShowing((IFile) cutHandler.getCompilationUnit().getResource());
        final int offset = methodUnderTest.get().getNameRange().getOffset() + 1;
        final EditorPartFacade facade = facadeWithCursorInMethod(editorPart, offset);

        assertEquals(methodUnderTest.get(), facade.getFirstNonAnonymousMethodSurroundingCursorPosition());
    }

    @Test
    public void should_not_consider_edited_file_as_java_like_file_when_it_is_not_a_file()
    {
        final EditorPartFacade facade = new EditorPartFacade(mockEditorPartShowing(null));

        assertFalse(facade.isJavaLikeFile());
        assertNull(facade.getCompilationUnit());
        assertNull(facade.getJavaProject());
    }

    @Test
    public void should_throw_exception_when_editor_part_is_null()
    {
        assertThrows(NullPointerException.class, () -> new EditorPartFacade(null));
    }
}
