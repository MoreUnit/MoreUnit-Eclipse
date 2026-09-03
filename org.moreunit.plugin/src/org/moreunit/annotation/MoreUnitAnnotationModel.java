package org.moreunit.annotation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationModelEvent;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension;
import org.eclipse.jface.text.source.IAnnotationModelListener;
import org.eclipse.jface.text.source.IAnnotationModelListenerExtension;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.moreunit.elements.ClassTypeFacade;
import org.moreunit.elements.EditorPartFacade;
import org.moreunit.elements.TypeFacade;
import org.moreunit.log.LogHandler;
import org.moreunit.preferences.Preferences;
import org.moreunit.preferences.TestAnnotationMode;

/**
 * @author vera 01.02.2009 14:27:06
 */
public class MoreUnitAnnotationModel implements IAnnotationModel
{

    private static final String IGNORE_ANNOTATION_NAME = "Ignore";

    private static final String MODEL_KEY = "org.moreunit.model_key";

    private final List<MoreUnitAnnotation> annotations = Collections.synchronizedList(new ArrayList<>());
    private final List<IAnnotationModelListener> annotationModelListeners = new ArrayList<>(2);
    private final IDocument document;
    private final ITextEditor textEditor;
    private Job updateJob;

    /*
     * Could be private, but is public for testing.
     */
    public MoreUnitAnnotationModel(IDocument document, ITextEditor textEditor)
    {
        this.document = document;
        this.textEditor = textEditor;
        updateAnnotations();
    }

    public static void updateAnnotations(ITextEditor editor)
    {
        final IDocumentProvider provider = editor.getDocumentProvider();
        if(provider == null)
        {
            return;
        }
        final IAnnotationModel model = provider.getAnnotationModel(editor.getEditorInput());
        if(! (model instanceof final IAnnotationModelExtension modelExtension))
        {
            return;
        }

        final MoreUnitAnnotationModel annotationModel = (MoreUnitAnnotationModel) modelExtension.getAnnotationModel(MODEL_KEY);
        if(annotationModel != null)
        {
            annotationModel.updateAnnotations();
        }
    }

    public static void attachForAllOpenEditor()
    {
        final IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
        for (final IWorkbenchWindow window : windows)
        {
            final IWorkbenchPage[] pages = window.getPages();
            for (final IWorkbenchPage page : pages)
            {
                final IEditorReference[] editors = page.getEditorReferences();
                for (final IEditorReference editorReference : editors)
                {
                    final IWorkbenchPart editorPart = editorReference.getPart(false);
                    if(editorPart instanceof final ITextEditor editor)
                    {
                        attach(editor);
                    }
                }
            }
        }
    }

    public static void attach(ITextEditor editor)
    {
        final IDocumentProvider provider = editor.getDocumentProvider();
        if(provider == null)
        {
            return;
        }
        final IAnnotationModel model = provider.getAnnotationModel(editor.getEditorInput());
        if(! (model instanceof final IAnnotationModelExtension modelExtension))
        {
            return;
        }

        final IDocument document = provider.getDocument(editor.getEditorInput());

        MoreUnitAnnotationModel annotationModel = (MoreUnitAnnotationModel) modelExtension.getAnnotationModel(MODEL_KEY);

        if(annotationModel == null)
        {
            annotationModel = new MoreUnitAnnotationModel(document, editor);
            modelExtension.addAnnotationModel(MODEL_KEY, annotationModel);
        }
    }

    public static void detach(ITextEditor editor)
    {
        final IDocumentProvider provider = editor.getDocumentProvider();
        if(provider == null)
        {
            return;
        }

        IAnnotationModel model = provider.getAnnotationModel(editor.getEditorInput());
        if(! (model instanceof final IAnnotationModelExtension modelExtension))
        {
            return;
        }
        model = modelExtension.removeAnnotationModel(MODEL_KEY);
        if(model instanceof final MoreUnitAnnotationModel annotationModel && annotationModel.updateJob != null)
        {
            annotationModel.updateJob.cancel();
        }
    }

    private void clear(AnnotationModelEvent event)
    {
        synchronized (annotations)
        {
            for (final MoreUnitAnnotation annotation : annotations)
            {
                annotation.markDeleted(true);
                event.annotationRemoved(annotation, annotation.getPosition());
            }

            annotations.clear();
        }
    }

    private void updateAnnotations()
    {
        final MoreUnitAnnotationModel modelInstance = this;
        if(updateJob == null)
        {
            updateJob = new Job("Update MoreUnit Annotations")
            {
                @Override
                protected IStatus run(IProgressMonitor monitor)
                {
                    JavaModelManager.getIndexManager().waitForIndex(true, monitor);
                    if(monitor.isCanceled())
                    {
                        return Status.CANCEL_STATUS;
                    }
                    final AnnotationModelEvent event = new AnnotationModelEvent(modelInstance);
                    clear(event);
                    try
                    {
                        final EditorPartFacade editorPartFacade = new EditorPartFacade(textEditor);
                        if(! editorPartFacade.isJavaLikeFile())
                        {
                            return Status.OK_STATUS;
                        }
                        final ICompilationUnit compilationUnit = editorPartFacade.getCompilationUnit();
                        if(TypeFacade.isTestCase(compilationUnit))
                        {
                            return Status.OK_STATUS;
                        }
                        final ClassTypeFacade classTypeFacade = new ClassTypeFacade(compilationUnit);
                        final IType type = classTypeFacade.getType();
                        if(type == null)
                        {
                            return Status.OK_STATUS; // this could happen if the
                                                     // resource is out of sync
                                                     // with
                                                     // the file system
                        }
                        annotateTestedMethods(type, classTypeFacade, event, monitor);
                    }
                    catch (final Exception exc)
                    {
                        LogHandler.getInstance().handleExceptionLog(exc);
                    }
                    fireModelChanged(event);
                    return monitor.isCanceled() ? Status.CANCEL_STATUS : Status.OK_STATUS;
                }
            };
            updateJob.setPriority(Job.DECORATE);
        }
        if(updateJob.getResult() != null && updateJob.getResult().getSeverity() == IStatus.CANCEL)
        {
            return;
        }
        updateJob.schedule();
    }

    private void annotateTestedMethods(IType type, ClassTypeFacade classTypeFacade, AnnotationModelEvent event, IProgressMonitor monitor) throws JavaModelException
    {
        final TestAnnotationMode testAnnotationMode = Preferences.forProject(type.getJavaProject()).getTestAnnotationMode();
        if(testAnnotationMode == TestAnnotationMode.OFF)
        {
            return;
        }
        monitor.beginTask("Processing type \"" + type.getElementName() + "\"", type.getMethods().length);
        for (final IMethod method : type.getMethods())
        {
            if(monitor.isCanceled())
            {
                return;
            }
            // never search by call, as it causes a lot of issues, the
            // CallHierarchy singleton's state being shared between different
            // search tasks
            final Collection<IMethod> testMethods = classTypeFacade.getCorrespondingTestMethods(method, testAnnotationMode.getMethodSearchMode());

            boolean hasIgnoredTest = false;
            for (final IMethod testMethod : testMethods)
            {
                // Using getAnnotation(IGNORE_ANNOTATION_NAME).exists() seems to
                // give back true "for a while" after removing an annotation,
                // that is why I am using this loop
                final IAnnotation[] allAnnotations = testMethod.getAnnotations();
                for (final IAnnotation annotation : allAnnotations)
                {
                    if(IGNORE_ANNOTATION_NAME.equals(annotation.getElementName()))
                    {
                        hasIgnoredTest = true;
                        break;
                    }
                }
            }

            if(! testMethods.isEmpty())
            {
                final ISourceRange range = method.getNameRange();
                MoreUnitAnnotation annotation = null;
                if(hasIgnoredTest)
                    annotation = MoreUnitAnnotation.createAnnotationForIgnoredTesMethod(range);
                else
                    annotation = MoreUnitAnnotation.createAnnotationForTestedMethod(range);

                synchronized (annotations)
                {
                    annotations.add(annotation);
                }
                event.annotationAdded(annotation);
            }
            monitor.worked(1);
        }
    }

    @Override
    public void addAnnotation(Annotation annotation, Position position)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addAnnotationModelListener(IAnnotationModelListener listener)
    {
        if(! annotationModelListeners.contains(listener))
        {
            annotationModelListeners.add(listener);
            fireModelChanged(new AnnotationModelEvent(this, true));
        }
    }

    protected void fireModelChanged(AnnotationModelEvent event)
    {
        event.markSealed();
        if(! event.isEmpty())
        {
            for (final IAnnotationModelListener listener : annotationModelListeners)
            {
                if(listener instanceof final IAnnotationModelListenerExtension extension)
                {
                    extension.modelChanged(event);
                }
                else
                {
                    listener.modelChanged(this);
                }
            }
        }
    }

    @Override
    public void connect(IDocument document)
    {
        if(this.document != document)
        {
            throw new RuntimeException("Can not connect");
        }

        for (final MoreUnitAnnotation annotation : copyAnnotations())
        {
            try
            {
                document.addPosition(annotation.getPosition());
            }
            catch (final BadLocationException exc)
            {
                LogHandler.getInstance().handleExceptionLog(exc);
            }
        }
    }

    @Override
    public void disconnect(IDocument document)
    {
        if(this.document != document)
        {
            throw new RuntimeException("Can not connect");
        }

        for (final MoreUnitAnnotation annotation : copyAnnotations())
        {
            document.removePosition(annotation.getPosition());
        }
    }

    @Override
    public Iterator<Annotation> getAnnotationIterator()
    {
        return new ArrayList<Annotation>(copyAnnotations()).iterator();
    }

    @Override
    public Position getPosition(Annotation annotation)
    {
        if(annotation instanceof final MoreUnitAnnotation unitAnnotation)
        {
            return unitAnnotation.getPosition();
        }
        return null;
    }

    @Override
    public void removeAnnotation(Annotation annotation)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeAnnotationModelListener(IAnnotationModelListener listener)
    {
        annotationModelListeners.remove(listener);
    }

    private List<MoreUnitAnnotation> copyAnnotations()
    {
        synchronized (annotations)
        {
            return new ArrayList<>(annotations);
        }
    }
}
