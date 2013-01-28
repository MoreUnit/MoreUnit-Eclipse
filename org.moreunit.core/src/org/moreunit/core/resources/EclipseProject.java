package org.moreunit.core.resources;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

public class EclipseProject extends EclipseResourceContainer implements Project
{
    private IProject project;

    EclipseProject(IProject project)
    {
        super(project);
        this.project = project;
    }

    @Override
    public void create()
    {
        try
        {
            if(! project.exists())
                project.create(null);
            if(! project.isOpen())
                project.open(null);
        }
        catch (CoreException e)
        {
            throw new ResourceException(e);
        }
    }

    @Override
    public boolean exists()
    {
        return project.exists() && project.isOpen();
    }
}
