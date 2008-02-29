package org.moreunit.listener;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.EditorPart;
import org.moreunit.elements.ClassTypeFacade;
import org.moreunit.elements.EditorPartFacade;
import org.moreunit.marker.MarkerUpdater;
import org.moreunit.util.PluginTools;

/**
 * @author vera
 *
 * 23.02.2008 17:30:33
 */
public class MarkerUpdateListener implements IPartListener {

	public void partActivated(IWorkbenchPart part) {
	}

	public void partBroughtToTop(IWorkbenchPart part) {
		updateMarkerForPart(part);
	}

	public void partClosed(IWorkbenchPart part) {
	}

	public void partDeactivated(IWorkbenchPart part) {
	}

	public void partOpened(IWorkbenchPart part) {
		updateMarkerForPart(part);
	}

	private void updateMarkerForPart(IWorkbenchPart workbenchPart) {
		if (workbenchPart instanceof EditorPart && PluginTools.isJavaFile(workbenchPart)) {
			EditorPartFacade editorPartFacade = new EditorPartFacade((IEditorPart)workbenchPart);
			
			(new MarkerUpdater(new ClassTypeFacade(editorPartFacade.getCompilationUnit()))).schedule();
		}
	}
}
