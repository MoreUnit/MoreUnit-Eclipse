package org.moreunit.annotation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
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
import org.moreunit.elements.TypeFacade.MethodSearchMode;
import org.moreunit.log.LogHandler;

/**
 * @author vera 01.02.2009 14:27:06
 */
public class MoreUnitAnnotationModel implements IAnnotationModel, IDocumentListener
{

    private static final String MODEL_KEY = "org.moreunit.model_key";

    private final List<MoreUnitAnnotation> annotations = new ArrayList<MoreUnitAnnotation>();
    private final List<IAnnotationModelListener> annotationModelListeners = new ArrayList<IAnnotationModelListener>(2);
    private final IDocument document;
    private final ITextEditor textEditor;

    private int openConnections = 0;

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
        modelExtension.removeAnnotationModel(MODEL_KEY);
    }

    private void clear(AnnotationModelEvent event)
    {
        Iterator<MoreUnitAnnotation> iterator = getAnnotationIterator();
        while (iterator.hasNext())
        {
            MoreUnitAnnotation annotation = iterator.next();
            event.annotationRemoved(annotation, annotation.getPosition());
        }
        annotations.clear();
    }

    private void updateAnnotations()
    {
        // long startTime = System.currentTimeMillis();
        AnnotationModelEvent event = new AnnotationModelEvent(this);
        clear(event);

        try
        {
            EditorPartFacade editorPartFacade = new EditorPartFacade(textEditor);
            if(! editorPartFacade.isJavaFile())
            {
                return;
            }
            
            ICompilationUnit compilationUnit = editorPartFacade.getCompilationUnit();
            if(TypeFacade.isTestCase(compilationUnit))
            {
                return;
            }

            ClassTypeFacade classTypeFacade = new ClassTypeFacade(compilationUnit);
            IType type = classTypeFacade.getType();
            if(type == null)
            {
                return; // this could happen if the resource is out of sync with the file system
            }

            annotateTestedMethods(type, classTypeFacade, event);
        }
        catch (Exception exc)
        {
            LogHandler.getInstance().handleExceptionLog(exc);
        }
        
        fireModelChanged(event);

        // System.err.println(String.format("updateAnnotations(): %sms", System.currentTimeMillis() - startTime));
    }

    private void annotateTestedMethods(IType type, ClassTypeFacade classTypeFacade, AnnotationModelEvent event) throws JavaModelException
    {
        // TODO Nicolas: only uncomment the following for testing, since performances are far too bad for production.
        // I let it here for now, just for it to be checked in once into CVS, and I will remove it then unless I can improve it.
        // boolean extendedSearch = Preferences.getInstance().shouldUseTestMethodExtendedSearch(type.getJavaProject());
        // MethodSearchMode searchMode = extendedSearch ? MethodSearchMode.BY_CALL : MethodSearchMode.BY_NAME;
        MethodSearchMode searchMode = MethodSearchMode.BY_NAME;

        for (IMethod method : type.getMethods())
        {
            if(classTypeFacade.hasTestMethod(method, searchMode))
            {
                ISourceRange range = method.getNameRange();
                MoreUnitAnnotation annotation = new MoreUnitAnnotation(range.getOffset(), range.getLength());
                annotations.add(annotation);
                event.annotationAdded(annotation);
            }
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

        for (MoreUnitAnnotation annotation : annotations)
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

        if(openConnections++ == 0)
        {
            document.addDocumentListener(this);
        }
    }

    public void disconnect(IDocument document)
    {
        if(this.document != document)
        {
            throw new RuntimeException("Can not connect");
        }

        for (MoreUnitAnnotation annotation : annotations)
        {
            document.removePosition(annotation.getPosition());
        }

        if(--openConnections == 0)
        {
            document.removeDocumentListener(this);
        }
    }

    public Iterator<MoreUnitAnnotation> getAnnotationIterator()
    {
        return annotations.iterator();
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

    public void documentChanged(DocumentEvent event)
    {
        // TODO Nicolas: do we really need to do this on each and every single character change?
        // maybe it would be preferable to do this only on save, or to define a minimum delay after change
        updateAnnotations();
    }

    public void documentAboutToBeChanged(DocumentEvent event)
    {
        // void: event ignored
    }
}
