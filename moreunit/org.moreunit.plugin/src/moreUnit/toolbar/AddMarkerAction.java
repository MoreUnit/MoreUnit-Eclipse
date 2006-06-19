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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
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
		
		Job markerJob = new Job("Add marker") {
			protected IStatus run(IProgressMonitor monitor) {
				IProject[] projects = getProjects();
				
				monitor.beginTask("Update projects", projects.length);

				for(int i=0; i<projects.length; i++) {
					IProject project = (IProject)projects[i];
					monitor.subTask(project.getName());
					IJavaProject javaProject = JavaCore.create(project);
					if(javaProject.isOpen()) {
						JavaProjectFacade javaProjectFacade = new JavaProjectFacade(javaProject);
						(javaProjectFacade).deleteTestCaseMarkers();
						javaProjectFacade.addTestCaseMarkers();
					}
					monitor.worked(i+1);
				}
				return Status.OK_STATUS;
			}
		};
		markerJob.setUser(true);
		markerJob.schedule();
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
// Revision 1.4  2006/04/16 16:58:14  gianasista
// Feature: AddMarkerAction shows progress to the user via Jobs-API
//
// Revision 1.3  2006/01/31 19:06:11  gianasista
// *** empty log message ***
//
// Revision 1.2  2006/01/19 21:39:44  gianasista
// Added CVS-commit-logging to all java-files
//
