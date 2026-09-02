package org.moreunit.annotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModelListener;
import org.eclipse.jface.text.source.AnnotationModelEvent;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.junit.jupiter.api.Test;
import org.moreunit.preferences.Preferences;
import org.moreunit.preferences.TestAnnotationMode;
import org.moreunit.test.context.Context;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.configs.SimpleJUnit4Project;

public class MoreUnitAnnotationModelTest extends ContextTestCase
{
    private MoreUnitAnnotationModel createModel(IDocument document)
    {
        IEditorInput editorInput = mock(IEditorInput.class);
        when(editorInput.getAdapter(IFile.class)).thenReturn(null);
        ITextEditor editor = mock(ITextEditor.class);
        when(editor.getEditorInput()).thenReturn(editorInput);

        return new MoreUnitAnnotationModel(document, editor);
    }

    @Test
    public void constructor_should_create_empty_model_and_schedule_update()
    {
        IDocument document = mock(IDocument.class);
        MoreUnitAnnotationModel model = createModel(document);

        assertFalse(model.getAnnotationIterator().hasNext());
    }

    @Test
    public void getAnnotationIterator_should_return_empty_iterator_initially()
    {
        MoreUnitAnnotationModel model = createModel(mock(IDocument.class));

        // annotations are only added by the (asynchronously running) update job
        Iterator<Annotation> iterator = model.getAnnotationIterator();
        assertNotNull(iterator);
        assertFalse(iterator.hasNext());
    }

    @Test
    public void addAnnotationModelListener_should_notify_listener_of_world_change()
    {
        MoreUnitAnnotationModel model = createModel(mock(IDocument.class));

        IAnnotationModelListener listener = mock(IAnnotationModelListener.class);
        model.addAnnotationModelListener(listener);

        verify(listener).modelChanged(model);
    }

    @Test
    public void addAnnotationModelListener_should_not_add_same_listener_twice()
    {
        MoreUnitAnnotationModel model = createModel(mock(IDocument.class));

        IAnnotationModelListener listener = mock(IAnnotationModelListener.class);
        model.addAnnotationModelListener(listener);
        model.addAnnotationModelListener(listener);

        verify(listener).modelChanged(any());
    }

    @Test
    public void removeAnnotationModelListener_should_not_notify_removed_listener_anymore()
    {
        MoreUnitAnnotationModel model = createModel(mock(IDocument.class));
        IAnnotationModelListener listener = mock(IAnnotationModelListener.class);

        // adding the listener immediately notifies it (world change event)
        model.addAnnotationModelListener(listener);
        verify(listener).modelChanged(model);

        model.removeAnnotationModelListener(listener);

        // trigger another event through a new listener registration
        model.addAnnotationModelListener(mock(IAnnotationModelListener.class));

        verifyNoMoreInteractions(listener);
    }

    @Test
    public void connect_should_accept_expected_document_and_reject_others()
    {
        IDocument document = mock(IDocument.class);
        MoreUnitAnnotationModel model = createModel(document);

        model.connect(document);

        assertThrows(RuntimeException.class, () -> model.connect(mock(IDocument.class)));
    }

    @Test
    public void disconnect_should_accept_expected_document_and_reject_others()
    {
        IDocument document = mock(IDocument.class);
        MoreUnitAnnotationModel model = createModel(document);

        model.disconnect(document);

        assertThrows(RuntimeException.class, () -> model.disconnect(mock(IDocument.class)));
    }

    @Test
    public void getPosition_should_return_position_of_own_annotations_and_null_for_foreign_ones()
    {
        MoreUnitAnnotationModel model = createModel(mock(IDocument.class));

        assertNull(model.getPosition(mock(Annotation.class)));

        MoreUnitAnnotation annotation = MoreUnitAnnotation.createAnnotationForTestedMethod(mock(ISourceRange.class));
        assertEquals(new Position(0, 0), model.getPosition(annotation));
    }

    @Test
    public void addAnnotation_and_removeAnnotation_should_not_be_supported()
    {
        MoreUnitAnnotationModel model = createModel(mock(IDocument.class));

        assertThrows(UnsupportedOperationException.class, () -> model.addAnnotation(mock(Annotation.class), new Position(0, 1)));
        assertThrows(UnsupportedOperationException.class, () -> model.removeAnnotation(mock(Annotation.class)));
    }

    @Test
    public void getAnnotationIterator_should_return_empty_iterator()
    {
        MoreUnitAnnotationModel model = createModel(mock(IDocument.class));

        Iterator<Annotation> iterator = model.getAnnotationIterator();
        assertNotNull(iterator);
        assertFalse(iterator.hasNext());
    }

    // ------------------------------------------------------------------
    // Tests running against a real compilation unit and the update job
    // ------------------------------------------------------------------

    private ITextEditor editorOver(ICompilationUnit compilationUnit)
    {
        IEditorInput editorInput = mock(IEditorInput.class);
        when(editorInput.getAdapter(IFile.class)).thenReturn((IFile) compilationUnit.getResource());
        ITextEditor editor = mock(ITextEditor.class);
        when(editor.getEditorInput()).thenReturn(editorInput);
        return editor;
    }

    private MoreUnitAnnotationModel modelFor(ICompilationUnit compilationUnit) throws Exception
    {
        IDocument document = new org.eclipse.jface.text.Document(compilationUnit.getSource());
        return new MoreUnitAnnotationModel(document, editorOver(compilationUnit));
    }

    private void await(Runnable assertion) throws Exception
    {
        Display display = Display.getDefault();
        long deadline = System.currentTimeMillis() + 30_000;
        AssertionError lastFailure = null;
        while (System.currentTimeMillis() < deadline)
        {
            while (display.readAndDispatch())
            {
            }
            try
            {
                assertion.run();
                return;
            }
            catch (AssertionError e)
            {
                lastFailure = e;
            }
            Thread.sleep(20);
            display.readAndDispatch();
        }
        throw lastFailure != null ? lastFailure : new AssertionError("condition not met in time");
    }

    private int annotationCount(MoreUnitAnnotationModel model)
    {
        int count = 0;
        for (Iterator<Annotation> it = model.getAnnotationIterator(); it.hasNext(); it.next())
        {
            count++;
        }
        return count;
    }

    @Context(SimpleJUnit4Project.class)
    @Test
    public void updateJob_should_annotate_methods_having_a_test() throws Exception
    {
        ICompilationUnit cut = context.getCompilationUnit("org.SomeClass");
        context.getPrimaryTypeHandler("org.SomeClass").addMethod("public int getNumber()", "return 1;");
        context.getPrimaryTypeHandler("org.SomeClassTest").addMethod("public void getNumber()", "");
        Preferences.getInstance().setTestAnnotationMode(cut.getJavaProject(), TestAnnotationMode.BY_NAME);

        MoreUnitAnnotationModel model = modelFor(cut);

        await(() -> assertEquals(1, annotationCount(model)));

        MoreUnitAnnotation annotation = (MoreUnitAnnotation) model.getAnnotationIterator().next();
        assertEquals(MoreUnitAnnotation.ANNOTATION_ID, annotation.getType());

        // the annotation position must match the name range of the tested method
        IMethod method = context.getPrimaryTypeHandler("org.SomeClass").get().getMethods()[0];
        assertEquals(new Position(method.getNameRange().getOffset(), method.getNameRange().getLength()), annotation.getPosition());

        // connect/disconnect must accept the annotated document
        IDocument document = getDocument(model);
        model.connect(document);
        model.disconnect(document);
    }

    private static IDocument getDocument(MoreUnitAnnotationModel model)
    {
        try
        {
            java.lang.reflect.Field field = MoreUnitAnnotationModel.class.getDeclaredField("document");
            field.setAccessible(true);
            return (IDocument) field.get(model);
        }
        catch (ReflectiveOperationException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Context(SimpleJUnit4Project.class)
    @Test
    public void updateJob_should_create_no_annotation_for_methods_without_test() throws Exception
    {
        ICompilationUnit cut = context.getCompilationUnit("org.SomeClass");
        context.getPrimaryTypeHandler("org.SomeClass").addMethod("public int getNumber()", "return 1;");
        Preferences.getInstance().setTestAnnotationMode(cut.getJavaProject(), TestAnnotationMode.BY_NAME);

        MoreUnitAnnotationModel model = modelFor(cut);

        Thread.sleep(500);
        while (Display.getDefault().readAndDispatch())
        {
        }

        assertEquals(0, annotationCount(model));
    }

    @Context(SimpleJUnit4Project.class)
    @Test
    public void updateJob_should_annotate_nothing_when_test_annotation_mode_is_off() throws Exception
    {
        ICompilationUnit cut = context.getCompilationUnit("org.SomeClass");
        context.getPrimaryTypeHandler("org.SomeClass").addMethod("public int getNumber()", "return 1;");
        context.getPrimaryTypeHandler("org.SomeClassTest").addMethod("public void getNumber()", "");
        Preferences.getInstance().setTestAnnotationMode(cut.getJavaProject(), TestAnnotationMode.OFF);

        MoreUnitAnnotationModel model = modelFor(cut);

        Thread.sleep(500);
        while (Display.getDefault().readAndDispatch())
        {
        }

        assertEquals(0, annotationCount(model));
    }

    @Context(SimpleJUnit4Project.class)
    @Test
    public void updateJob_should_create_ignored_annotation_when_test_method_is_annotated_with_ignore() throws Exception
    {
        ICompilationUnit cut = context.getCompilationUnit("org.SomeClass");
        context.getPrimaryTypeHandler("org.SomeClass").addMethod("public int getNumber()", "return 1;");
        context.getPrimaryTypeHandler("org.SomeClassTest").addMethod("@Ignore\n@Test\npublic void getNumber()", "");
        Preferences.getInstance().setTestAnnotationMode(cut.getJavaProject(), TestAnnotationMode.BY_NAME);

        MoreUnitAnnotationModel model = modelFor(cut);

        await(() -> assertEquals(1, annotationCount(model)));

        MoreUnitAnnotation annotation = (MoreUnitAnnotation) model.getAnnotationIterator().next();
        assertEquals(MoreUnitAnnotation.ANNOTATION_ID_IGNORED, annotation.getType());
    }

    @Context(SimpleJUnit4Project.class)
    @Test
    public void updateAnnotations_should_refresh_existing_annotations() throws Exception
    {
        ICompilationUnit cut = context.getCompilationUnit("org.SomeClass");
        context.getPrimaryTypeHandler("org.SomeClass").addMethod("public int getNumber()", "return 1;");
        context.getPrimaryTypeHandler("org.SomeClassTest").addMethod("public void getNumber()", "");
        Preferences.getInstance().setTestAnnotationMode(cut.getJavaProject(), TestAnnotationMode.BY_NAME);

        ITextEditor editor = editorOver(cut);
        org.eclipse.jface.text.source.AnnotationModel annotationModel = new org.eclipse.jface.text.source.AnnotationModel();
        IDocumentProvider provider = mock(IDocumentProvider.class);
        when(provider.getAnnotationModel(any())).thenReturn(annotationModel);
        when(provider.getDocument(any())).thenReturn(new org.eclipse.jface.text.Document(cut.getSource()));
        when(editor.getDocumentProvider()).thenReturn(provider);

        MoreUnitAnnotationModel.attach(editor);
        MoreUnitAnnotationModel attached = (MoreUnitAnnotationModel) annotationModel.getAnnotationModel("org.moreunit.model_key");
        await(() -> assertEquals(1, annotationCount(attached)));

        // now the tested method has no test anymore: refreshing must clear the annotation
        org.eclipse.jdt.core.IMethod testMethod = context.getPrimaryTypeHandler("org.SomeClassTest").get().getMethods()[0];
        testMethod.delete(true, null);
        MoreUnitAnnotationModel.updateAnnotations(editor);

        await(() -> assertEquals(0, annotationCount(attached)));
    }

    @Test
    public void updateAnnotations_should_do_nothing_when_editor_has_no_document_provider()
    {
        ITextEditor editor = mock(ITextEditor.class);
        when(editor.getDocumentProvider()).thenReturn(null);

        // must neither throw nor schedule anything
        MoreUnitAnnotationModel.updateAnnotations(editor);
    }

    @Context(SimpleJUnit4Project.class)
    @Test
    public void attach_should_register_a_model_for_the_editor_and_detach_should_remove_it() throws Exception
    {
        ICompilationUnit cut = context.getCompilationUnit("org.SomeClass");

        org.eclipse.jface.text.source.AnnotationModel annotationModel = new org.eclipse.jface.text.source.AnnotationModel();
        IDocumentProvider provider = mock(IDocumentProvider.class);
        when(provider.getAnnotationModel(any())).thenReturn(annotationModel);
        when(provider.getDocument(any())).thenReturn(new org.eclipse.jface.text.Document(cut.getSource()));
        ITextEditor editor = editorOver(cut);
        when(editor.getDocumentProvider()).thenReturn(provider);

        MoreUnitAnnotationModel.attach(editor);

        MoreUnitAnnotationModel attached = (MoreUnitAnnotationModel) annotationModel.getAnnotationModel("org.moreunit.model_key");
        assertNotNull(attached);

        // attaching again must not create a second model
        MoreUnitAnnotationModel.attach(editor);
        assertSame(attached, annotationModel.getAnnotationModel("org.moreunit.model_key"));

        MoreUnitAnnotationModel.detach(editor);
        assertNull(annotationModel.getAnnotationModel("org.moreunit.model_key"));
    }
}
