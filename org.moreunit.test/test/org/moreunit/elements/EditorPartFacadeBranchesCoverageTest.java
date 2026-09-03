package org.moreunit.elements;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.moreunit.log.LogHandler;

public class EditorPartFacadeBranchesCoverageTest
{
    private IEditorPart mockEditorPartShowing(IFile file)
    {
        final IEditorInput editorInput = mock(IEditorInput.class);
        when(editorInput.getAdapter(IFile.class)).thenReturn(file);

        final IEditorPart editorPart = mock(IEditorPart.class);
        when(editorPart.getEditorInput()).thenReturn(editorInput);
        return editorPart;
    }

    private EditorPartFacade facadeWithCursorAt(IEditorPart editorPart, int offset)
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
    public void should_return_null_and_log_when_element_lookup_fails() throws Exception
    {
        // given a compilation unit that fails on element lookup
        final ICompilationUnit compilationUnit = mock(ICompilationUnit.class);
        when(compilationUnit.getElementAt(anyInt())).thenThrow(new JavaModelException(new RuntimeException("boom"), 1));

        try (var javaCore = mockStatic(JavaCore.class, Answers.CALLS_REAL_METHODS);
             var logs = mockStatic(LogHandler.class))
        {
            javaCore.when(() -> JavaCore.createCompilationUnitFrom(any(IFile.class))).thenReturn(compilationUnit);
            final LogHandler mockLog = mock(LogHandler.class);
            logs.when(LogHandler::getInstance).thenReturn(mockLog);

            final EditorPartFacade facade = facadeWithCursorAt(mockEditorPartShowing(mock(IFile.class)), 5);

            // when
            final IMethod result = facade.getMethodUnderCursorPosition();

            // then
            assertNull(result);
            verify(mockLog).handleExceptionLog(any(JavaModelException.class));
        }
    }

    @Test
    public void should_log_when_surrounding_method_lookup_fails() throws Exception
    {
        // given a compilation unit that fails on element lookup
        final ICompilationUnit compilationUnit = mock(ICompilationUnit.class);
        when(compilationUnit.getElementAt(anyInt())).thenThrow(new JavaModelException(new RuntimeException("boom"), 1));

        try (var javaCore = mockStatic(JavaCore.class, Answers.CALLS_REAL_METHODS);
             var logs = mockStatic(LogHandler.class))
        {
            javaCore.when(() -> JavaCore.createCompilationUnitFrom(any(IFile.class))).thenReturn(compilationUnit);
            final LogHandler mockLog = mock(LogHandler.class);
            logs.when(LogHandler::getInstance).thenReturn(mockLog);

            final EditorPartFacade facade = facadeWithCursorAt(mockEditorPartShowing(mock(IFile.class)), 5);

            // when
            final IMethod result = facade.getFirstNonAnonymousMethodSurroundingCursorPosition();

            // then
            assertNull(result);
            verify(mockLog).handleExceptionLog(any(JavaModelException.class));
        }
    }

    @Test
    public void should_return_null_for_first_non_anonymous_method_when_no_compilation_unit()
    {
        // given an editor without any file
        final EditorPartFacade facade = facadeWithCursorAt(mockEditorPartShowing(null), 0);

        // when, then
        assertNull(facade.getFirstNonAnonymousMethodSurroundingCursorPosition());
    }

    @Test
    public void should_return_enclosing_method_when_cursor_is_on_anonymous_type() throws Exception
    {
        // given a cursor located on an anonymous type
        final IMethod enclosingMethod = mock(IMethod.class);
        final IType anonymousType = mock(IType.class);
        when(anonymousType.isAnonymous()).thenReturn(true);
        when(anonymousType.getParent()).thenReturn(enclosingMethod);
        final IType declaringType = mock(IType.class);
        when(declaringType.isAnonymous()).thenReturn(false);
        when(enclosingMethod.getParent()).thenReturn(declaringType);

        final ICompilationUnit compilationUnit = mock(ICompilationUnit.class);
        when(compilationUnit.getElementAt(7)).thenReturn(anonymousType);

        try (var javaCore = mockStatic(JavaCore.class, Answers.CALLS_REAL_METHODS))
        {
            javaCore.when(() -> JavaCore.createCompilationUnitFrom(any(IFile.class))).thenReturn(compilationUnit);

            final EditorPartFacade facade = facadeWithCursorAt(mockEditorPartShowing(mock(IFile.class)), 7);

            // when
            final IMethod result = facade.getFirstNonAnonymousMethodSurroundingCursorPosition();

            // then the enclosing method is returned
            assertSame(enclosingMethod, result);
        }
    }
}
