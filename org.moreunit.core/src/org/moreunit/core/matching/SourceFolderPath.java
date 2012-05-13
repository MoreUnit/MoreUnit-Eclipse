package org.moreunit.core.matching;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class SourceFolderPath
{
    private final IPath path;

    SourceFolderPath(String path)
    {
        this.path = new Path(path);
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
}
