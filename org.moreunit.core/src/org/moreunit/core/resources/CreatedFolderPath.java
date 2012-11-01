package org.moreunit.core.resources;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

public class CreatedFolderPath
{
    private final CreatedFolderPath parent;
    private final IResource resource;

    public CreatedFolderPath(IResource resource)
    {
        this.parent = null;
        this.resource = resource;
    }

    public CreatedFolderPath(CreatedFolderPath parent, IResource resource)
    {
        this.parent = parent;
        this.resource = resource;
    }

    public void delete() throws CoreException
    {
        getFirstCreatedFolder().deleteResource();
    }

    private CreatedFolderPath getFirstCreatedFolder()
    {
        CreatedFolderPath currentFolder = this;
        while (currentFolder.parent != null)
        {
            currentFolder = currentFolder.parent;
        }
        return currentFolder;
    }

    private void deleteResource() throws CoreException
    {
        if(exists())
        {
            resource.delete(true, null);
        }
    }

    private boolean exists()
    {
        return resource != null;
    }

    @Override
    public String toString()
    {
        if(! exists())
        {
            return "";
        }
        return getFirstCreatedFolder() + "/**/" + resource.getFullPath().lastSegment().toString();
    }

    public void deleteFoldersThatAreNotParentOf(IResource otherResource) throws CoreException
    {
        CreatedFolderPath previousFolder = null;
        CreatedFolderPath currentFolder = this;
        while (currentFolder.exists() && ! currentFolder.resource.getFullPath().isPrefixOf(otherResource.getFullPath()))
        {
            previousFolder = currentFolder;
            currentFolder = currentFolder.parent;
        }

        if(previousFolder != null)
        {
            previousFolder.deleteResource();
        }
    }
}
