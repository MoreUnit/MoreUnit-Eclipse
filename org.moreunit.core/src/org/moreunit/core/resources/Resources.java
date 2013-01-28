package org.moreunit.core.resources;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class Resources
{
    public static CreatedFolder createFolder(String folderPath)
    {
        return createFolder(ResourcesPlugin.getWorkspace().getRoot().getFolder(new Path(folderPath)));
    }

    public static CreatedFolder createFolder(IFolder folder)
    {
        return createFolder(folder.getProject(), folder.getFullPath().removeFirstSegments(1));
    }

    public static CreatedFolder createFolder(IProject project, IPath folderPath)
    {
        IFolder srcFolder = project.getFolder(folderPath);
        if(srcFolder.exists())
        {
            return new CreatedFolder(srcFolder);
        }

        IFolder folder = null;
        CreatedFolderPath createdFolderPath = null;

        for (String segment : folderPath.segments())
        {
            if(folder == null)
            {
                folder = project.getFolder(segment);
            }
            else
            {
                folder = folder.getFolder(segment);
            }

            if(! folder.exists())
            {
                try
                {
                    folder.create(false, true, null);
                }
                catch (CoreException e)
                {
                    throw new FolderCreationException(e, folder);
                }

                createdFolderPath = new CreatedFolderPath(createdFolderPath, folder);
            }
        }

        return new CreatedFolder(folder, createdFolderPath);
    }

    public static class CreatedFolder
    {
        private final IFolder folder;
        private final CreatedFolderPath path;

        private CreatedFolder(IFolder folder, CreatedFolderPath path)
        {
            this.folder = folder;
            this.path = path;
        }

        private CreatedFolder(IFolder folder)
        {
            this(folder, new CreatedFolderPath(null));
        }

        public IFolder get()
        {
            return folder;
        }

        public CreatedFolderPath getCreatedFolderPath()
        {
            return path;
        }
    }
}
