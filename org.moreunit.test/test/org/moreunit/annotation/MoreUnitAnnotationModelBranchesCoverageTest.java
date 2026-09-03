package org.moreunit.annotation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationModelEvent;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.junit.jupiter.api.Test;
import org.moreunit.elements.ClassTypeFacade;
import org.moreunit.log.LogHandler;
import org.moreunit.preferences.Preferences;
import org.moreunit.preferences.TestAnnotationMode;
import org.moreunit.test.context.Context;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.configs.SimpleJUnit4Project;

public class MoreUnitAnnotationModelBranchesCoverageTest extends ContextTestCase
{
    private ITextEditor editorWithProvider(IDocumentProvider provider)
    {
        final ITextEditor editor = mock(ITextEditor.class);
        when(editor.getDocumentProvider()).thenReturn(provider);
        return editor;
    }

    @Test
    public void should_do_nothing_on_update_when_editor_has_no_document_provider()
    {
        final ITextEditor editor = mock(ITextEditor.class);
        when(editor.getDocumentProvider()).thenReturn(null);

        assertDoesNotThrow(() -> MoreUnitAnnotationModel.updateAnnotations(editor));
    }

    @Test
    public void should_do_nothing_on_update_when_annotation_model_is_not_extensible()
    {
        final IDocumentProvider provider = mock(IDocumentProvider.class);
        when(provider.getAnnotationModel(any())).thenReturn(mock(IAnnotationModel.class));

        assertDoesNotThrow(() -> MoreUnitAnnotationModel.updateAnnotations(editorWithProvider(provider)));
        verify(provider).getAnnotationModel(any());
    }

    @Test
    public void should_do_nothing_on_update_when_no_model_is_attached()
    {
        final IAnnotationModelExtension model = mock(IAnnotationModelExtension.class, withSettings().extraInterfaces(IAnnotationModel.class));
        when(model.getAnnotationModel(anyString())).thenReturn(null);
        final IDocumentProvider provider = mock(IDocumentProvider.class);
        when(provider.getAnnotationModel(any())).thenReturn((IAnnotationModel) model);

        assertDoesNotThrow(() -> MoreUnitAnnotationModel.updateAnnotations(editorWithProvider(provider)));
        verify(model).getAnnotationModel(anyString());
    }

    @Test
    public void should_do_nothing_on_attach_when_editor_has_no_document_provider()
    {
        final ITextEditor editor = mock(ITextEditor.class);
        when(editor.getDocumentProvider()).thenReturn(null);

        assertDoesNotThrow(() -> MoreUnitAnnotationModel.attach(editor));
    }

    @Test
    public void should_do_nothing_on_attach_when_annotation_model_is_not_extensible()
    {
        final IDocumentProvider provider = mock(IDocumentProvider.class);
        when(provider.getAnnotationModel(any())).thenReturn(mock(IAnnotationModel.class));

        assertDoesNotThrow(() -> MoreUnitAnnotationModel.attach(editorWithProvider(provider)));
        verify(provider).getAnnotationModel(any());
    }

    @Test
    public void should_do_nothing_on_detach_when_editor_has_no_document_provider()
    {
        final ITextEditor editor = mock(ITextEditor.class);
        when(editor.getDocumentProvider()).thenReturn(null);

        assertDoesNotThrow(() -> MoreUnitAnnotationModel.detach(editor));
    }

    @Test
    public void should_do_nothing_on_detach_when_annotation_model_is_not_extensible()
    {
        final IDocumentProvider provider = mock(IDocumentProvider.class);
        when(provider.getAnnotationModel(any())).thenReturn(mock(IAnnotationModel.class));

        assertDoesNotThrow(() -> MoreUnitAnnotationModel.detach(editorWithProvider(provider)));
        verify(provider).getAnnotationModel(any());
    }

    @Test
    public void should_do_nothing_on_detach_when_no_model_is_attached()
    {
        final IAnnotationModelExtension model = mock(IAnnotationModelExtension.class, withSettings().extraInterfaces(IAnnotationModel.class));
        when(model.removeAnnotationModel(anyString())).thenReturn(null);
        final IDocumentProvider provider = mock(IDocumentProvider.class);
        when(provider.getAnnotationModel(any())).thenReturn((IAnnotationModel) model);

        assertDoesNotThrow(() -> MoreUnitAnnotationModel.detach(editorWithProvider(provider)));
        verify(model).removeAnnotationModel(anyString());
    }

    @Test
    public void should_visit_all_open_editors_when_attaching_for_all_open_editors()
    {
        final ITextEditor textEditor = mock(ITextEditor.class);
        when(textEditor.getDocumentProvider()).thenReturn(null);
        final IEditorReference textEditorRef = mock(IEditorReference.class);
        when(textEditorRef.getPart(false)).thenReturn(textEditor);
        final IEditorReference otherPartRef = mock(IEditorReference.class);
        when(otherPartRef.getPart(false)).thenReturn(mock(IWorkbenchPart.class));

        final IWorkbenchPage page = mock(IWorkbenchPage.class);
        when(page.getEditorReferences()).thenReturn(new IEditorReference[] { textEditorRef, otherPartRef });
        final IWorkbenchWindow window = mock(IWorkbenchWindow.class);
        when(window.getPages()).thenReturn(new IWorkbenchPage[] { page });
        final IWorkbench workbench = mock(IWorkbench.class);
        when(workbench.getWorkbenchWindows()).thenReturn(new IWorkbenchWindow[] { window });

        try (var platform = mockStatic(PlatformUI.class))
        {
            platform.when(PlatformUI::getWorkbench).thenReturn(workbench);

            assertDoesNotThrow(MoreUnitAnnotationModel::attachForAllOpenEditor);
            verify(textEditorRef).getPart(false);
            verify(otherPartRef).getPart(false);
        }
    }

    @Test
    public void should_log_exception_when_update_job_fails()
    {
        // given an editor that fails as soon as it is accessed
        final ITextEditor editor = mock(ITextEditor.class);
        when(editor.getEditorInput()).thenThrow(new RuntimeException("boom"));

        try (var logs = mockStatic(LogHandler.class))
        {
            final LogHandler mockLog = mock(LogHandler.class);
            logs.when(LogHandler::getInstance).thenReturn(mockLog);

            new MoreUnitAnnotationModel(mock(IDocument.class), editor);

            verify(mockLog, timeout(15000)).handleExceptionLog(any(Throwable.class));
        }
    }

    @Test
    public void should_not_reschedule_update_when_previous_run_was_cancelled() throws Exception
    {
        // given a model whose previous update was cancelled
        final IEditorInput editorInput = mock(IEditorInput.class);
        when(editorInput.getAdapter(IFile.class)).thenReturn(null);
        final ITextEditor editor = mock(ITextEditor.class);
        when(editor.getEditorInput()).thenReturn(editorInput);
        final MoreUnitAnnotationModel model = new MoreUnitAnnotationModel(mock(IDocument.class), editor);

        final Job cancelledJob = mock(Job.class);
        when(cancelledJob.getResult()).thenReturn(Status.CANCEL_STATUS);
        final Field jobField = MoreUnitAnnotationModel.class.getDeclaredField("updateJob");
        jobField.setAccessible(true);
        jobField.set(model, cancelledJob);

        // when triggering another update, then no new job is scheduled
        final Method updateAnnotations = MoreUnitAnnotationModel.class.getDeclaredMethod("updateAnnotations");
        updateAnnotations.setAccessible(true);
        assertDoesNotThrow(() -> {
            try
            {
                updateAnnotations.invoke(model);
            }
            catch (final ReflectiveOperationException e)
            {
                throw new RuntimeException(e);
            }
        });
        verify(cancelledJob, never()).schedule();
    }

    @Test
    public void should_stop_annotating_when_monitor_is_cancelled() throws Exception
    {
        // given a model and a cancelled monitor
        final IEditorInput editorInput = mock(IEditorInput.class);
        when(editorInput.getAdapter(IFile.class)).thenReturn(null);
        final ITextEditor editor = mock(ITextEditor.class);
        when(editor.getEditorInput()).thenReturn(editorInput);
        final MoreUnitAnnotationModel model = new MoreUnitAnnotationModel(mock(IDocument.class), editor);

        final IType type = mock(IType.class);
        when(type.getElementName()).thenReturn("Foo");
        when(type.getMethods()).thenReturn(new IMethod[] { mock(IMethod.class) });
        final IProgressMonitor monitor = mock(IProgressMonitor.class);
        when(monitor.isCanceled()).thenReturn(true);

        try (var prefs = mockStatic(Preferences.class))
        {
            final Preferences.ProjectPreferences view = mock(Preferences.ProjectPreferences.class);
            prefs.when(() -> Preferences.forProject(any())).thenReturn(view);
            when(view.getTestAnnotationMode()).thenReturn(TestAnnotationMode.BY_NAME);

            final Method annotate = MoreUnitAnnotationModel.class.getDeclaredMethod("annotateTestedMethods", IType.class, ClassTypeFacade.class, AnnotationModelEvent.class, IProgressMonitor.class);
            annotate.setAccessible(true);
            try
            {
                annotate.invoke(model, type, mock(ClassTypeFacade.class), mock(AnnotationModelEvent.class), monitor);
            }
            catch (final java.lang.reflect.InvocationTargetException e)
            {
                throw new AssertionError("unexpected invocation failure", e.getCause());
            }

            verify(monitor).beginTask(anyString(), org.mockito.ArgumentMatchers.eq(1));
        }
    }

    @Test
    public void should_log_when_annotation_position_cannot_be_added_on_connect() throws Exception
    {
        // given a model holding one annotation
        final IDocument document = mock(IDocument.class);
        final IEditorInput editorInput = mock(IEditorInput.class);
        when(editorInput.getAdapter(IFile.class)).thenReturn(null);
        final ITextEditor editor = mock(ITextEditor.class);
        when(editor.getEditorInput()).thenReturn(editorInput);
        final MoreUnitAnnotationModel model = new MoreUnitAnnotationModel(document, editor);

        @SuppressWarnings("unchecked")
        final List<MoreUnitAnnotation> annotations = (List<MoreUnitAnnotation>) field(model, "annotations");
        annotations.add(MoreUnitAnnotation.createAnnotationForTestedMethod(mock(ISourceRange.class)));

        org.mockito.Mockito.doThrow(new BadLocationException()).when(document).addPosition(any(Position.class));

        try (var logs = mockStatic(LogHandler.class))
        {
            final LogHandler mockLog = mock(LogHandler.class);
            logs.when(LogHandler::getInstance).thenReturn(mockLog);

            // when
            model.connect(document);

            // then the failure was logged
            verify(mockLog).handleExceptionLog(any(BadLocationException.class));
        }
    }

    private static Object field(Object target, String fieldName) throws Exception
    {
        final Field field = MoreUnitAnnotationModel.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(target);
    }

    // ------------------------------------------------------------------
    // Tests running against a real compilation unit and the update job
    // ------------------------------------------------------------------

    private ITextEditor editorOver(ICompilationUnit compilationUnit)
    {
        final IEditorInput editorInput = mock(IEditorInput.class);
        when(editorInput.getAdapter(IFile.class)).thenReturn((IFile) compilationUnit.getResource());
        final ITextEditor editor = mock(ITextEditor.class);
        when(editor.getEditorInput()).thenReturn(editorInput);
        return editor;
    }

    private MoreUnitAnnotationModel modelFor(ICompilationUnit compilationUnit) throws Exception
    {
        final IDocument document = new org.eclipse.jface.text.Document(compilationUnit.getSource());
        return new MoreUnitAnnotationModel(document, editorOver(compilationUnit));
    }

    private void await(Runnable assertion) throws Exception
    {
        final Display display = Display.getDefault();
        final long deadline = System.currentTimeMillis() + 30_000;
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
            catch (final AssertionError e)
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
        for (final Iterator<Annotation> it = model.getAnnotationIterator(); it.hasNext(); it.next())
        {
            count++;
        }
        return count;
    }

    @Context(SimpleJUnit4Project.class)
    @Test
    public void should_create_tested_annotation_when_test_method_has_other_annotations() throws Exception
    {
        final ICompilationUnit cut = context.getCompilationUnit("org.SomeClass");
        context.getPrimaryTypeHandler("org.SomeClass").addMethod("public int getNumber()", "return 1;");
        context.getPrimaryTypeHandler("org.SomeClassTest").addMethod("@Deprecated public void getNumber()", "");
        Preferences.getInstance().setTestAnnotationMode(cut.getJavaProject(), TestAnnotationMode.BY_NAME);

        final MoreUnitAnnotationModel model = modelFor(cut);

        await(() -> assertEquals(1, annotationCount(model)));

        final MoreUnitAnnotation annotation = (MoreUnitAnnotation) model.getAnnotationIterator().next();
        assertEquals(MoreUnitAnnotation.ANNOTATION_ID, annotation.getType());
    }

    @Test
    public void should_keep_existing_model_when_attaching_twice()
    {
        // given an editor with a plain annotation model
        final org.eclipse.jface.text.source.AnnotationModel annotationModel = new org.eclipse.jface.text.source.AnnotationModel();
        final IDocumentProvider provider = mock(IDocumentProvider.class);
        when(provider.getAnnotationModel(any())).thenReturn(annotationModel);
        when(provider.getDocument(any())).thenReturn(new org.eclipse.jface.text.Document(""));
        final IEditorInput editorInput = mock(IEditorInput.class);
        when(editorInput.getAdapter(IFile.class)).thenReturn(null);
        final ITextEditor editor = mock(ITextEditor.class);
        when(editor.getEditorInput()).thenReturn(editorInput);
        when(editor.getDocumentProvider()).thenReturn(provider);

        // when attaching twice, then the same model is kept
        MoreUnitAnnotationModel.attach(editor);
        final Object attached = annotationModel.getAnnotationModel("org.moreunit.model_key");
        assertNotNull(attached);

        MoreUnitAnnotationModel.attach(editor);
        assertSame(attached, annotationModel.getAnnotationModel("org.moreunit.model_key"));
    }
}
