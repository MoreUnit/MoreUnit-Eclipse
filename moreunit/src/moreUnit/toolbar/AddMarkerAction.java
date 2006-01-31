/*
 * Created on 08.01.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package moreUnit.toolbar;

import moreUnit.elements.JavaProjectFacade;
import moreUnit.log.LogHandler;

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
		IProject[] projects = getProjects();

		for(int i=0; i<projects.length; i++) {
			IProject project = (IProject)projects[i];
			IJavaProject javaProject = JavaCore.create(project);
			if(javaProject.isOpen()) {
				JavaProjectFacade javaProjectFacade = new JavaProjectFacade(javaProject);
				(javaProjectFacade).deleteTestCaseMarkers();
				javaProjectFacade.addTestCaseMarkers();
			}
		}
	}
	
	private IProject[] getProjects() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot workspaceRoot = workspace.getRoot();
		return workspaceRoot.getProjects();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}
}

// $Log: not supported by cvs2svn $
// Revision 1.2  2006/01/19 21:39:44  gianasista
// Added CVS-commit-logging to all java-files
//
