package org.moreunit.core.resources;

import static org.moreunit.core.util.Preconditions.checkArgument;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;

abstract class EclipseResourceContainer extends EclipseResource implements ResourceContainer
{
    private IContainer container;

    protected EclipseResourceContainer(IContainer container)
    {
        super(container);
        this.container = container;
    }

    @Override
    public ContainerCreationRecord createWithRecord()
    {
        return new ContainerCreation(this).execute();
    }

    @Override
    public File getFile(String fileRelativePath)
    {
        return getFile(EclipseWorkspace.get().path(fileRelativePath));
    }

    @Override
    public File getFile(Path fileRelativePath)
    {
        checkArgument(! fileRelativePath.isEmpty(), "path must not be empty");
        checkArgument(fileRelativePath.isRelative(), "path must be relative to this resource");

        org.eclipse.core.runtime.Path platformPath = new org.eclipse.core.runtime.Path(fileRelativePath.toString());

        if(container.getFolder(platformPath).exists())
        {
            throw new ResourceException("a folder already exists at this path");
        }

        return new EclipseFile(container.getFile(platformPath));
    }

    @Override
    public Folder getFolder(String folderRelativePath)
    {
        return getFolder(EclipseWorkspace.get().path(folderRelativePath));
    }

    @Override
    public Folder getFolder(Path folderRelativePath)
    {
        checkArgument(! folderRelativePath.isEmpty(), "path must not be empty");
        checkArgument(folderRelativePath.isRelative(), "path must be relative to this resource");

        org.eclipse.core.runtime.Path platformPath = new org.eclipse.core.runtime.Path(folderRelativePath.toString());

        if(container.getFile(platformPath).exists())
        {
            throw new ResourceException("a file already exists at this path");
        }

        return new EclipseFolder(container.getFolder(platformPath));
    }

    @Override
    public boolean isParentOf(Resource resource)
    {
        return getPath().isPrefixOf(resource.getPath());
    }

    @Override
    public List<File> listFiles()
    {
        return listResourcesOfType(IFile.class, EclipseFile.class);
    }

    @Override
    public List<Folder> listFolders()
    {
        return listResourcesOfType(IFolder.class, EclipseFolder.class);
    }

    private <T, U extends T> List<T> listResourcesOfType(Class< ? extends IResource> resourceType, Class<U> outputType)
    {
        try
        {
            Constructor<U> outputTypeConstructor = outputType.getConstructor(resourceType);

            IResource[] members = container.members();
            List<T> result = new ArrayList<T>(members.length);

            for (IResource m : members)
            {
                if(resourceType.isInstance(m))
                {
                    result.add(outputTypeConstructor.newInstance(m));
                }
            }
            return result;
        }
        catch (Exception e)
        {
            throw new ResourceException(e);
        }
    }
}
