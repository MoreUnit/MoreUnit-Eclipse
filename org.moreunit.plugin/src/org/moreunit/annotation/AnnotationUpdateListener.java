package org.moreunit.annotation;

import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * @author vera
 *
 * 23.02.2008 17:30:33
 */
public class AnnotationUpdateListener implements IPartListener {

	public void partActivated(IWorkbenchPart part) 
	{
		if (part instanceof ITextEditor) 
		{
			MoreUnitAnnotationModel.updateAnnotations((ITextEditor) part);
		}
	}

	public void partBroughtToTop(IWorkbenchPart part) {
		if (part instanceof ITextEditor) 
		{
			MoreUnitAnnotationModel.updateAnnotations((ITextEditor) part);
		}
	}

	public void partClosed(IWorkbenchPart part) {
		if (part instanceof ITextEditor) 
		{
			MoreUnitAnnotationModel.detach((ITextEditor) part);
		}
	}

	public void partDeactivated(IWorkbenchPart part) 
	{
	}

	public void partOpened(IWorkbenchPart part) {
	    if (part instanceof ITextEditor) 
	    {
	    	MoreUnitAnnotationModel.attach((ITextEditor) part);
	    }
	}
}
