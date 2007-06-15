package org.moreunit.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.moreunit.log.LogHandler;

public class SelectedJavaProjectProvider implements IStructuredContentProvider {


	private final IJavaProject	project;

	public SelectedJavaProjectProvider(IJavaProject javaProject) {
		this.project = javaProject;
	}

	public Object[] getElements(Object inputElement) {
		return getElements();
	}

	public SelectedJavaProject[] getElements() {
		List<IJavaProject> jumpTargets = ProjectProperties.instance().getJumpTargets(project);
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();

		List<SelectedJavaProject> rows = new ArrayList<SelectedJavaProject>();
		for (IProject project : projects) {
			if (project.isAccessible() && isJavaProject(project) && !this.project.getProject().equals(project)) {
				IJavaProject javaProject = JavaCore.create(project);
				rows.add(new SelectedJavaProject(javaProject, jumpTargets.contains(javaProject)));
			}
		}
		return rows.toArray(new SelectedJavaProject[rows.size()]);
	}

	private boolean isJavaProject(IProject project) {
		try {
			return project.hasNature(JavaCore.NATURE_ID);
		} catch (CoreException e) {
			LogHandler.getInstance().handleExceptionLog(e);
			return false;
		}
	}


	public Object[] getCheckedElements() {
		List<SelectedJavaProject> checked = new ArrayList<SelectedJavaProject>();
		SelectedJavaProject[] elements = getElements();
		for (SelectedJavaProject element : elements) {
			if (element.isSelected()) {
				checked.add(element);
			}
		}
		return checked.toArray(new SelectedJavaProject[checked.size()]);
	}

	public void dispose() {

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

}

// $Log: not supported by cvs2svn $
// Revision 1.4  2007/03/02 22:14:08  channingwalton
// [ 1667386 ] Jump to test can miss some testcases
//
// Fixed
//
// Revision 1.3  2006/10/19 19:17:41  gianasista
// Bugfixing: Problems with closed projects solved
//
// Revision 1.2  2006/10/02 18:22:23  channingwalton
// added actions for jumping from views. added some tests for project properties. improved some of the text
//
// Revision 1.1  2006/10/01 13:02:44  channingwalton
// Implementation for [ 1556583 ] Extend testcase matching across whole workspace
//
//
