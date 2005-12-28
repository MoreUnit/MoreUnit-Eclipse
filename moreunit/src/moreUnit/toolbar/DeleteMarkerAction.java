package moreUnit.toolbar;

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

public class DeleteMarkerAction implements IWorkbenchWindowActionDelegate{

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
	}

	public void run(IAction action) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot workspaceRoot = workspace.getRoot();
		IProject[] projects = workspaceRoot.getProjects();

		for(int i=0; i<projects.length; i++) {
			IProject project = (IProject)projects[i];
			IJavaProject javaProject = JavaCore.create(project);
			MarkerTools.deleteTestCaseMarkers(javaProject);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}
}