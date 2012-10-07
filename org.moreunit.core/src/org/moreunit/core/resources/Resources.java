package org.moreunit.core.resources;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

public class Resources
{
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
        CreatedPart createdPart = null;

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

                if(createdPart == null)
                {
                    createdPart = new CreatedPart(folder);
                }
            }
        }

        return new CreatedFolder(folder, createdPart);
    }

    public static class CreatedFolder
    {
        private final IFolder folder;
        private final CreatedPart createdPart;

        private CreatedFolder(IFolder folder, CreatedPart createdPart)
        {
            this.folder = folder;
            if(createdPart != null)
            {
                this.createdPart = createdPart;
            }
            else
            {
                this.createdPart = new CreatedPart(null);
            }
        }

        private CreatedFolder(IFolder folder)
        {
            this(folder, null);
        }

        public IFolder get()
        {
            return folder;
        }

        public CreatedPart getCreatedPart()
        {
            return createdPart;
        }
    }
}
