package org.moreunit.annotation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.moreunit.elements.EditorPartFacade;
import org.moreunit.util.PluginTools;

/**
 * @author vera 23.02.2008 17:30:33
 */
public class AnnotationUpdateListener implements IPartListener, IResourceChangeListener
{
    public AnnotationUpdateListener()
    {
        ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
    }

    @Override
    public void partActivated(IWorkbenchPart part)
    {
        if(part instanceof final ITextEditor editor)
        {
            MoreUnitAnnotationModel.updateAnnotations(editor);
        }
    }

    @Override
    public void partBroughtToTop(IWorkbenchPart part)
    {
        if(part instanceof final ITextEditor editor)
        {
            MoreUnitAnnotationModel.updateAnnotations(editor);
        }
    }

    @Override
    public void partClosed(IWorkbenchPart part)
    {
        if(part instanceof final ITextEditor editor)
        {
            MoreUnitAnnotationModel.detach(editor);
        }
    }

    @Override
    public void partDeactivated(IWorkbenchPart part)
    {
    }

    @Override
    public void partOpened(IWorkbenchPart part)
    {
        if(part instanceof final ITextEditor editor)
        {
            MoreUnitAnnotationModel.attach(editor);
        }
    }

    public void dispose()
    {
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
    }

    @Override
    public void resourceChanged(IResourceChangeEvent event)
    {
        final IEditorPart openEditorPart = PluginTools.getOpenEditorPart();
        if(openEditorPart instanceof final ITextEditor editor)
        {
            if(PluginTools.isJavaFile(openEditorPart))
            {
                final EditorPartFacade editorPartFacade = new EditorPartFacade(openEditorPart);
                final IFile file = editorPartFacade.getFile();
                if(file != null)
                {
                    final IResourceDelta delta = event.getDelta();
                    if(delta != null)
                    {
                        final IResourceDelta member = delta.findMember(file.getFullPath());
                        if(member != null)
                            MoreUnitAnnotationModel.updateAnnotations(editor);
                    }
                }
            }
        }
    }
}
