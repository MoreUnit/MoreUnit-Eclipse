package org.moreunit.core.matching;

import static org.moreunit.core.config.CoreModule.$;

import org.eclipse.core.resources.IFile;
import org.moreunit.core.resources.ContainerCreationRecord;
import org.moreunit.core.resources.Folder;
import org.moreunit.core.resources.Path;
import org.moreunit.core.resources.Resource;
import org.moreunit.core.resources.Workspace;

public class SourceFolderPath
{
    private final Path path;
    private final Workspace workspace;

    public SourceFolderPath(String path)
    {
        this(path, $().getWorkspace());
    }

    public SourceFolderPath(String path, Workspace workspace)
    {
        this.path = workspace.path(path);
        this.workspace = workspace;
    }

    public Path asPath()
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

    public Path getResolvedPart()
    {
        if(isResolved())
        {
            return path;
        }
        int i = 0;
        for (String s : path)
        {
            if(s.endsWith("[^") || s.contains("*"))
            {
                break;
            }
            i++;
        }
        return path.uptoSegment(i);
    }

    public Resource getResolvedPartAsResource()
    {
        Path part = getResolvedPart();
        if(part.getSegmentCount() == 1)
        {
            return workspace.getProject(part.getProjectName());
        }
        return workspace.getFolder(part);
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

    public ContainerCreationRecord createResolvedPartIfItDoesNotExist()
    {
        Resource resolvedPart = getResolvedPartAsResource();
        if(resolvedPart instanceof Folder && ! resolvedPart.exists())
        {
            return ((Folder) resolvedPart).createWithRecord();
        }
        return new ContainerCreationRecord();
    }
}
