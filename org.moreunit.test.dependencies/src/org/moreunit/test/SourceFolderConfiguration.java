package org.moreunit.test;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IPackageFragmentRoot;

public class SourceFolderConfiguration
{
    private final ProjectConfiguration projectConfig;
    private final String sourceFolderName;
    private IPackageFragmentRoot sourceFolder;

    public SourceFolderConfiguration(ProjectConfiguration projectConfig, String sourceFolderName)
    {
        this.projectConfig = projectConfig;
        this.sourceFolderName = sourceFolderName;
    }

    public ProjectConfiguration getProjectConfig()
    {
        return projectConfig;
    }

    public IPackageFragmentRoot getPackageFragmentRoot() throws CoreException
    {
        if(sourceFolder == null)
        {
            sourceFolder = WorkspaceHelper.createSourceFolderInProject(projectConfig.getProject(), sourceFolderName);
        }
        return sourceFolder;
    }
}
