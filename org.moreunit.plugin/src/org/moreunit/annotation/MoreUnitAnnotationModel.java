package org.moreunit.annotation;

import java.util.*;

import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.*;
import org.eclipse.jface.text.*;
import org.eclipse.jface.text.source.*;
import org.eclipse.ui.*;
import org.eclipse.ui.texteditor.*;
import org.moreunit.elements.*;
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
        IDocumentProvider provider = editor.getDocumentProvider();
        if(provider == null)
        {
            return;
        }
        IAnnotationModel model = provider.getAnnotationModel(editor.getEditorInput());
        if(! (model instanceof IAnnotationModelExtension))
        {
            return;
        }

        IAnnotationModelExtension modelExtension = (IAnnotationModelExtension) model;
        MoreUnitAnnotationModel annotationModel = (MoreUnitAnnotationModel) modelExtension.getAnnotationModel(MODEL_KEY);
        if(annotationModel != null)
        {
            annotationModel.updateAnnotations();
        }
    }

    public static void attachForAllOpenEditor()
    {
        IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
        for (IWorkbenchWindow window : windows)
        {
            IWorkbenchPage[] pages = window.getPages();
            for (IWorkbenchPage page : pages)
            {
                IEditorReference[] editors = page.getEditorReferences();
                for (IEditorReference editorReference : editors)
                {
                    IWorkbenchPart editorPart = editorReference.getPart(false);
                    if(editorPart instanceof ITextEditor)
                    {
                        attach((ITextEditor) editorPart);
                    }
                }
            }
        }
    }

    public static void attach(ITextEditor editor)
    {
        IDocumentProvider provider = editor.getDocumentProvider();
        if(provider == null)
        {
            return;
        }
        IAnnotationModel model = provider.getAnnotationModel(editor.getEditorInput());
        if(! (model instanceof IAnnotationModelExtension))
        {
            return;
        }

        IAnnotationModelExtension modelExtension = (IAnnotationModelExtension) model;
        IDocument document = provider.getDocument(editor.getEditorInput());

        MoreUnitAnnotationModel annotationModel = (MoreUnitAnnotationModel) modelExtension.getAnnotationModel(MODEL_KEY);

        if(annotationModel == null)
        {
            annotationModel = new MoreUnitAnnotationModel(document, editor);
            modelExtension.addAnnotationModel(MODEL_KEY, annotationModel);
        }
    }

    public static void detach(ITextEditor editor)
    {
        IDocumentProvider provider = editor.getDocumentProvider();
        if(provider == null)
        {
            return;
        }

        IAnnotationModel model = provider.getAnnotationModel(editor.getEditorInput());
        if(! (model instanceof IAnnotationModelExtension))
        {
            return;
        }
        IAnnotationModelExtension modelExtension = (IAnnotationModelExtension) model;
        model = modelExtension.removeAnnotationModel(MODEL_KEY);
        if(model instanceof MoreUnitAnnotationModel && ((MoreUnitAnnotationModel) model).updateJob != null)
        {
            ((MoreUnitAnnotationModel) model).updateJob.cancel();
        }
    }

    private void clear(AnnotationModelEvent event)
    {
        synchronized (annotations)
        {
            for (MoreUnitAnnotation annotation : annotations)
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
                    if(monitor.isCanceled())
                    {
                        return Status.CANCEL_STATUS;
                    }
                    AnnotationModelEvent event = new AnnotationModelEvent(modelInstance);
                    clear(event);
                    try
                    {
                        EditorPartFacade editorPartFacade = new EditorPartFacade(textEditor);
                        if(! editorPartFacade.isJavaLikeFile())
                        {
                            return Status.OK_STATUS;
                        }
                        ICompilationUnit compilationUnit = editorPartFacade.getCompilationUnit();
                        if(TypeFacade.isTestCase(compilationUnit))
                        {
                            return Status.OK_STATUS;
                        }
                        ClassTypeFacade classTypeFacade = new ClassTypeFacade(compilationUnit);
                        IType type = classTypeFacade.getType();
                        if(type == null)
                        {
                            return Status.OK_STATUS; // this could happen if the
                                                     // resource is out of sync
                                                     // with
                                                     // the file system
                        }
                        annotateTestedMethods(type, classTypeFacade, event, monitor);
                    }
                    catch (Exception exc)
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
        TestAnnotationMode testAnnotationMode = Preferences.forProject(type.getJavaProject()).getTestAnnotationMode();
        if(testAnnotationMode == TestAnnotationMode.OFF)
        {
            return;
        }
        monitor.beginTask("Processing type \"" + type.getElementName() + "\"", type.getMethods().length);
        for (IMethod method : type.getMethods())
        {
            if(monitor.isCanceled())
            {
                return;
            }
            // never search by call, as it causes a lot of issues, the
            // CallHierarchy singleton's state being shared between different
            // search tasks
            Collection<IMethod> testMethods = classTypeFacade.getCorrespondingTestMethods(method, testAnnotationMode.getMethodSearchMode());

            boolean hasIgnoredTest = false;
            for (IMethod testMethod : testMethods)
            {
                // Using getAnnotation(IGNORE_ANNOTATION_NAME).exists() seems to
                // give back true "for a while" after removing an annotation,
                // that is why I am using this loop
                IAnnotation[] allAnnotations = testMethod.getAnnotations();
                for (IAnnotation annotation : allAnnotations)
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
                ISourceRange range = method.getNameRange();
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

    public void addAnnotation(Annotation annotation, Position position)
    {
        throw new UnsupportedOperationException();
    }

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
            for (IAnnotationModelListener listener : annotationModelListeners)
            {
                if(listener instanceof IAnnotationModelListenerExtension)
                {
                    ((IAnnotationModelListenerExtension) listener).modelChanged(event);
                }
                else
                {
                    listener.modelChanged(this);
                }
            }
        }
    }

    public void connect(IDocument document)
    {
        if(this.document != document)
        {
            throw new RuntimeException("Can not connect");
        }

        for (MoreUnitAnnotation annotation : copyAnnotations())
        {
            try
            {
                document.addPosition(annotation.getPosition());
            }
            catch (BadLocationException exc)
            {
                LogHandler.getInstance().handleExceptionLog(exc);
            }
        }
    }

    public void disconnect(IDocument document)
    {
        if(this.document != document)
        {
            throw new RuntimeException("Can not connect");
        }

        for (MoreUnitAnnotation annotation : copyAnnotations())
        {
            document.removePosition(annotation.getPosition());
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Iterator<Annotation> getAnnotationIterator()
    {
        return new ArrayList(copyAnnotations()).iterator();
    }

    public Position getPosition(Annotation annotation)
    {
        if(annotation instanceof MoreUnitAnnotation)
        {
            return ((MoreUnitAnnotation) annotation).getPosition();
        }
        return null;
    }

    public void removeAnnotation(Annotation annotation)
    {
        throw new UnsupportedOperationException();
    }

    public void removeAnnotationModelListener(IAnnotationModelListener listener)
    {
        annotationModelListeners.remove(listener);
    }

    private List<MoreUnitAnnotation> copyAnnotations()
    {
        synchronized (annotations)
        {
            return new ArrayList<MoreUnitAnnotation>(annotations);
        }
    }
}
