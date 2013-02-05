package org.moreunit.core.resources;

import static java.util.Arrays.asList;

import java.util.Iterator;

import org.eclipse.core.runtime.IPath;

public class EclipsePath implements Path
{
    private IPath path;

    EclipsePath(IPath path)
    {
        this.path = path.removeTrailingSeparator();
    }

    @Override
    public boolean equals(Object other)
    {
        if(other == this)
            return true;
        if(! (other instanceof Path))
            return false;
        return ((Path) other).toString().equals(toString());
    }

    @Override
    public String getBaseName()
    {
        if(isEmpty())
            return "";

        String lastSegment = path.lastSegment();
        return lastSegment == null ? "/" : lastSegment;
    }

    @Override
    public String getBaseNameWithoutExtension()
    {
        return path.removeFileExtension().lastSegment();
    }

    @Override
    public String getExtension()
    {
        String ext = path.getFileExtension();
        return ext == null ? "" : ext;
    }

    @Override
    public String getProjectName()
    {
        return path.segmentCount() == 0 ? "" : path.segment(0);
    }

    @Override
    public int getSegmentCount()
    {
        return path.segmentCount();
    }

    @Override
    public boolean hasExtension()
    {
        return path.getFileExtension() != null;
    }

    @Override
    public int hashCode()
    {
        return toString().hashCode();
    }

    @Override
    public boolean isAbsolute()
    {
        return path.isAbsolute();
    }

    @Override
    public boolean isEmpty()
    {
        return path.isEmpty();
    }

    @Override
    public boolean isPrefixOf(Path otherPath)
    {
        return otherPath.toString().startsWith(toString());
    }

    @Override
    public boolean isRelative()
    {
        return ! isAbsolute();
    }

    @Override
    public Iterator<String> iterator()
    {
        return asList(path.segments()).iterator();
    }

    @Override
    public Path relativeToProject()
    {
        return new EclipsePath(path.removeFirstSegments(1));
    }

    @Override
    public String toString()
    {
        return path.toString();
    }

    @Override
    public Path uptoSegment(int segmentIndex)
    {
        if(segmentIndex > getSegmentCount())
        {
            throw new IndexOutOfBoundsException("No segment at index: " + segmentIndex);
        }
        return new EclipsePath(path.uptoSegment(segmentIndex));
    }

    @Override
    public Path withoutLastSegment()
    {
        return new EclipsePath(path.removeLastSegments(1));
    }

    @Override
    public Path withRelativePath(Path relativePath)
    {
        return new EclipsePath(path.append(((EclipsePath) relativePath).path));
    }
}
