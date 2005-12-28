/*
 * Created on 08.01.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package moreUnit.toolbar;

import moreUnit.log.LogHandler;
import moreUnit.util.MarkerTools;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * @author Vera
 */
public class AddMarkerAction implements IWorkbenchWindowActionDelegate {
	
	ISelection selection;

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
	}

	public void run(IAction action) {
		LogHandler.getInstance().handleInfoLog("AddMarkerAction.run()");
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot workspaceRoot = workspace.getRoot();
		IProject[] projects = workspaceRoot.getProjects();

		for(int i=0; i<projects.length; i++) {
			IProject project = (IProject)projects[i];
			IJavaProject javaProject = JavaCore.create(project);
			if(javaProject.isOpen()) {
				MarkerTools.deleteTestCaseMarkers(javaProject);
				MarkerTools.addTestCaseMarkers(javaProject);
			}
		}
		
		/*
		LogHandler.getInstance().handleInfoLog("Informiere Decorator");
		try {
			IDecoratorManager decoratorManager = MoreUnitPlugin.getDefault().getWorkbench().getDecoratorManager();
			decoratorManager.setEnabled(MagicNumbers.TEST_CASE_DECORATOR, true);
		} catch (CoreException exc) {
			LogHandler.getInstance().handleExceptionLog(exc);
		}
		
		UnitDecorator unitDecorator = UnitDecorator.getUnitDecorator();
		if(unitDecorator != null)
			unitDecorator.refreshAll();
		else
			LogHandler.getInstance().handleInfoLog("unitDecorator ist null");
		*/
		
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}
}
