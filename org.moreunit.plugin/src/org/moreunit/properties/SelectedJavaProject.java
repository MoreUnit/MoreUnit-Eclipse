package org.moreunit.properties;

import org.eclipse.jdt.core.IJavaProject;

public final class SelectedJavaProject {

	private IJavaProject	javaProject;
	private boolean			selected;

	public SelectedJavaProject(IJavaProject javaProject, boolean selected) {
		this.javaProject = javaProject;
		this.selected = selected;
	}

	public IJavaProject getJavaProject() {
		return javaProject;
	}

	public void setJavaProject(IJavaProject javaProject) {
		this.javaProject = javaProject;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getProjectName() {
		return javaProject.getProject().getName();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !getClass().equals(obj.getClass())) {
			return false;
		}
		SelectedJavaProject other = (SelectedJavaProject) obj;
		return javaProject.equals(other.getJavaProject());
	}

	@Override
	public int hashCode() {
		return getJavaProject().hashCode();
	}
}

//$Log: not supported by cvs2svn $
//
