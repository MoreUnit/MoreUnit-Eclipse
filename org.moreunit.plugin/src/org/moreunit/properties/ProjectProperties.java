package org.moreunit.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.moreunit.log.LogHandler;

public class ProjectProperties {

	public static final QualifiedName	JUMP_TARGETS_PROPERTY	= new QualifiedName("org.moreunit", "jumpTargets");

	private static ProjectProperties	INSTANCE;

	public static synchronized ProjectProperties instance() {
		if (INSTANCE == null) {
			INSTANCE = new ProjectProperties();
		}
		return INSTANCE;
	}

	private ProjectProperties() {
	}

	public List<IJavaProject> getJumpTargets(IJavaProject javaProject) {
		List<IJavaProject> projects = new ArrayList<IJavaProject>();
		try {
			projects.addAll(getJumpTargetsFrom(javaProject));
			projects.addAll(getJumpTargetsTo(javaProject));
		} catch (CoreException e) {
			LogHandler.getInstance().handleExceptionLog(e);
		}
		return projects;
	}

	private List<IJavaProject> getJumpTargetsTo(IJavaProject javaProject) throws CoreException {
		List<IJavaProject> projects = new ArrayList<IJavaProject>();
		IProject[] allProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		
		for (int i = 0; i < allProjects.length; i++) {
			if(allProjects[i].isAccessible()) {
				if (allProjects[i].hasNature(JavaCore.NATURE_ID)) {
					IJavaProject candidate = JavaCore.create(allProjects[i]);
					if (getJumpTargetsFrom(candidate).contains(javaProject)) {
						projects.add(candidate);
					}
				}
			}
		}
		return projects;
	}

	public boolean setTestProjects(IProject project, IJavaProject[] projects) {
		try {
			project.setPersistentProperty(JUMP_TARGETS_PROPERTY, createJumpTargetProperty(projects));
			return true;
		} catch (CoreException e) {
			LogHandler.getInstance().handleExceptionLog(e);
			return false;
		}
	}

	private List<IJavaProject> getJumpTargetsFrom(IJavaProject javaProject) throws CoreException {
		String jumpTargetProperty = javaProject.getProject().getPersistentProperty(ProjectProperties.JUMP_TARGETS_PROPERTY);
		if (jumpTargetProperty != null && jumpTargetProperty.length() > 0) {
			return getJavaProjectsFromJumpTargetProperty(jumpTargetProperty);
		}
		return new ArrayList<IJavaProject>();
	}

	private List<IJavaProject> getJavaProjectsFromJumpTargetProperty(String jumpTargetProperty) throws CoreException {
		String[] jumpTargets = jumpTargetProperty.split(",");
		List<IJavaProject> javaProjects = new ArrayList<IJavaProject>();
		for (int i = 0; i < jumpTargets.length; i++) {
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(jumpTargets[i]);
			if (project.exists() && project.hasNature(JavaCore.NATURE_ID)) {
				javaProjects.add(JavaCore.create(project));
			}
		}
		return javaProjects;
	}
	
	private String createJumpTargetProperty(IJavaProject[] projects) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < projects.length; i++) {
			buffer.append(projects[i].getProject().getName());
			if (i < projects.length - 1) {
				buffer.append(",");
			}
		}
		return buffer.toString();
	}

}

// $Log: not supported by cvs2svn $
// Revision 1.1  2006/10/01 13:02:44  channingwalton
// Implementation for [ 1556583 ] Extend testcase matching across whole workspace
//
//

