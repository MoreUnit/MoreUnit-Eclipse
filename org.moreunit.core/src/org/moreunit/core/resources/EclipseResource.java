package org.moreunit.core.resources;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;

abstract class EclipseResource implements Resource
{
    private final IResource resource;
    private final EclipsePath path;

    protected EclipseResource(IResource resource)
    {
        this.resource = resource;
        this.path = new EclipsePath(resource.getFullPath());
    }

    @Override
    public void delete()
    {
        try
        {
            resource.delete(true, null);
        }
        catch (CoreException e)
        {
            throw new ResourceException("Could not delete resource: " + getPath(), e);
        }
    }

    @Override
    public final boolean equals(Object other)
    {
        if(other == this)
            return true;
        if(other == null || other.getClass() != getClass())
            return false;
        return other.toString().equals(toString());
    }

    @Override
    public boolean exists()
    {
        return resource.exists();
    }

    @Override
    public String getName()
    {
        return path.getBaseName();
    }

    @Override
    public final ResourceContainer getParent()
    {
        IContainer parent = resource.getParent();
        if(parent == null || parent instanceof IWorkspaceRoot)
        {
            return EclipseWorkspace.get();
        }
        if(parent instanceof IProject)
        {
            return new EclipseProject((IProject) parent);
        }
        if(parent instanceof IFolder)
        {
            return new EclipseFolder((IFolder) parent);
        }
        throw new ResourceException("Unknown parent type: " + parent.getClass().getName());
    }

    @Override
    public final Path getPath()
    {
        return path;
    }

    @Override
    public final IResource getUnderlyingPlatformResource()
    {
        return resource;
    }

    @Override
    public final int hashCode()
    {
        return toString().hashCode();
    }

    @Override
    public final String toString()
    {
        return path.toString();
    }
}
