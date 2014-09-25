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

    public void partActivated(IWorkbenchPart part)
    {
        if(part instanceof ITextEditor)
        {
            MoreUnitAnnotationModel.updateAnnotations((ITextEditor) part);
        }
    }

    public void partBroughtToTop(IWorkbenchPart part)
    {
        if(part instanceof ITextEditor)
        {
            MoreUnitAnnotationModel.updateAnnotations((ITextEditor) part);
        }
    }

    public void partClosed(IWorkbenchPart part)
    {
        if(part instanceof ITextEditor)
        {
            MoreUnitAnnotationModel.detach((ITextEditor) part);
        }
    }

    public void partDeactivated(IWorkbenchPart part)
    {
    }

    public void partOpened(IWorkbenchPart part)
    {
        if(part instanceof ITextEditor)
        {
            MoreUnitAnnotationModel.attach((ITextEditor) part);
        }
    }

    public void dispose()
    {
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
    }

    public void resourceChanged(IResourceChangeEvent event)
    {
        IEditorPart openEditorPart = PluginTools.getOpenEditorPart();
        if(openEditorPart instanceof ITextEditor)
        {
            if(PluginTools.isJavaFile(openEditorPart))
            {
                EditorPartFacade editorPartFacade = new EditorPartFacade(openEditorPart);
                IFile file = editorPartFacade.getFile();
                if(file != null)
                {
                    IResourceDelta delta = event.getDelta();
                    if(delta != null)
                    {
                        IResourceDelta member = delta.findMember(file.getFullPath());
                        if(member != null)
                            MoreUnitAnnotationModel.updateAnnotations((ITextEditor) openEditorPart);
                    }
                }
            }
        }
    }
}
