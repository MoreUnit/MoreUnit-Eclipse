package org.moreunit.core.resources;

import org.eclipse.core.resources.IFolder;
import org.moreunit.core.preferences.ProjectPreferences;

public class EclipseFolder extends EclipseResourceContainer implements Folder
{
    public EclipseFolder(IFolder folder)
    {
        super(folder);
    }

    @Override
    public void create()
    {
        Resources.createFolder(getPath().toString());
    }

    @Override
    public Project getProject()
    {
        return new EclipseProject(getUnderlyingPlatformResource().getProject());
    }

    @Override
    public ProjectPreferences getProjectPreferences()
    {
        return EclipseWorkspace.get().getPreferences().get(getUnderlyingPlatformResource().getProject());
    }
}
