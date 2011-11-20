package org.moreunit.test;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;

public class ProjectConfiguration
{
    private final String projectName;
    private IJavaProject project;

    public ProjectConfiguration(String projectName)
    {
        this.projectName = projectName;
    }

    public IJavaProject getProject() throws CoreException
    {
        if(project == null)
        {
            project = WorkspaceHelper.createJavaProject(projectName);
        }
        return project;
    }
}
