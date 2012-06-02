package org.moreunit.core.resources;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

public class CreatedPart
{
    private final IResource resource;

    public CreatedPart(IResource resource)
    {
        this.resource = resource;
    }

    public void delete() throws CoreException
    {
        if(resource != null)
        {
            resource.delete(true, null);
        }
    }

    @Override
    public String toString()
    {
        return resource == null ? "" : resource.toString();
    }
}
