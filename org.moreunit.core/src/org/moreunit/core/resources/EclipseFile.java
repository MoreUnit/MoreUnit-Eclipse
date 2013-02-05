package org.moreunit.core.resources;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.moreunit.core.preferences.ProjectPreferences;

public class EclipseFile extends EclipseResource implements File
{
    private final IFile file;

    public EclipseFile(IFile file)
    {
        super(file);
        this.file = file;
    }

    @Override
    public void create()
    {
        if(exists())
        {
            return;
        }

        getParent().create();

        try
        {
            Path path = new Path(getPath().toString());
            ResourcesPlugin.getWorkspace().getRoot().getFile(path).create(null, false, null);
        }
        catch (CoreException e)
        {
            throw new ResourceException("Could not create file: " + getPath(), e);
        }
    }

    @Override
    public String getBaseNameWithoutExtension()
    {
        return getPath().getBaseNameWithoutExtension();
    }

    @Override
    public String getExtension()
    {
        String ext = file.getFileExtension();
        return ext == null ? "" : ext;
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

    @Override
    public IFile getUnderlyingPlatformFile()
    {
        return file;
    }

    @Override
    public boolean hasExtension()
    {
        return file.getFileExtension() != null;
    }
}
