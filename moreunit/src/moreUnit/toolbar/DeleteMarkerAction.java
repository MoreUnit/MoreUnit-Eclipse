package moreUnit.toolbar;

import moreUnit.elements.JavaProjectFacade;

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
			(new JavaProjectFacade(javaProject)).deleteTestCaseMarkers();
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}
}

// $Log: not supported by cvs2svn $
// Revision 1.2  2006/01/19 21:39:44  gianasista
// Added CVS-commit-logging to all java-files
//