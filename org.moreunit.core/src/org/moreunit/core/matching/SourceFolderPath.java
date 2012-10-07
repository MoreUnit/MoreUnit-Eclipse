package org.moreunit.core.matching;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.moreunit.core.resources.CreatedPart;
import org.moreunit.core.resources.Resources;

public class SourceFolderPath
{
    private final IPath path;

    public SourceFolderPath(String path)
    {
        this.path = new Path(path).removeTrailingSeparator();
    }

    public IPath asPath()
    {
        return path;
    }

    public boolean isResolved()
    {
        return ! toString().contains("*");
    }

    @Override
    public String toString()
    {
        return path.toString();
    }

    public IPath getResolvedPart()
    {
        if(isResolved())
        {
            return path;
        }
        int i = 0;
        for (String s : path.segments())
        {
            if(s.endsWith("[^") || s.contains("*"))
            {
                break;
            }
            i++;
        }
        return path.uptoSegment(i);
    }

    public IResource getResolvedPartAsResource()
    {
        IPath part = getResolvedPart();
        if(part.segmentCount() == 1)
        {
            return ResourcesPlugin.getWorkspace().getRoot().getProject(part.segment(0));
        }
        return ResourcesPlugin.getWorkspace().getRoot().getFolder(part);
    }

    public boolean matches(IFile file)
    {
        String folder = file.getFullPath().removeLastSegments(1).removeTrailingSeparator().toString();
        if(folder.startsWith("/"))
        {
            folder = folder.substring(1);
        }
        return folder.matches(path.toString());
    }

    public CreatedPart createResolvedPartIfItDoesNotExist()
    {
        IResource resolvedPart = getResolvedPartAsResource();
        if(resolvedPart instanceof IFolder && ! resolvedPart.exists())
        {
            return Resources.createFolder((IFolder) resolvedPart).getCreatedPart();
        }

        return new CreatedPart(null);
    }
}
