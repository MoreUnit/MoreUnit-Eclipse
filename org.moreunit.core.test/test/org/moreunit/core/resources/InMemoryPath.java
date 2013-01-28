package org.moreunit.core.resources;

import static java.util.Collections.unmodifiableList;
import static org.moreunit.core.util.Preconditions.checkArgument;
import static org.moreunit.core.util.Strings.splitAsList;

import java.util.Iterator;
import java.util.List;

import org.moreunit.core.util.Strings;

public class InMemoryPath implements Path
{
    public static final InMemoryPath ROOT = new InMemoryPath("/");

    private final String path;
    private final List<String> segments;

    public InMemoryPath(String path)
    {
        if(path.equals("/"))
        {
            this.path = path;
        }
        else
        {
            this.path = path.replaceFirst("/$", "");
        }
        segments = unmodifiableList(splitAsList(path, "/"));
    }

    @Override
    public final boolean equals(Object other)
    {
        if(other == this)
            return true;
        if(! (other instanceof Path))
            return false;
        return other.toString().equals(toString());
    }

    @Override
    public String getBaseName()
    {
        if(isEmpty())
            return "";

        return segments.isEmpty() ? "/" : segments.get(segments.size() - 1);
    }

    @Override
    public String getBaseNameWithoutExtension()
    {
        String baseName = getBaseName();
        int lastDotIdx = baseName.lastIndexOf('.');
        if(lastDotIdx != - 1)
        {
            return baseName.substring(0, lastDotIdx);
        }
        return baseName;
    }

    @Override
    public String getExtension()
    {
        String baseName = getBaseName();
        int lastDotIdx = baseName.lastIndexOf(".");
        if(lastDotIdx != - 1)
        {
            return baseName.substring(lastDotIdx + 1);
        }
        return "";
    }

    @Override
    public String getProjectName()
    {
        return segments.isEmpty() ? "" : segments.get(0);
    }

    @Override
    public int getSegmentCount()
    {
        return segments.size();
    }

    @Override
    public boolean hasExtension()
    {
        return ! getExtension().isEmpty();
    }

    @Override
    public final int hashCode()
    {
        return toString().hashCode();
    }

    @Override
    public boolean isAbsolute()
    {
        return path.startsWith("/");
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
        return segments.iterator();
    }

    @Override
    public InMemoryPath relativeToProject()
    {
        return new InMemoryPath(Strings.join("/", segments.subList(1, segments.size())));
    }

    @Override
    public final String toString()
    {
        return path;
    }

    @Override
    public InMemoryPath withoutLastSegment()
    {
        int lastSlashIdx = path.lastIndexOf("/");
        if(lastSlashIdx == - 1)
            return new InMemoryPath("");
        if(lastSlashIdx == 0)
            return new InMemoryPath("/");
        return new InMemoryPath(path.substring(0, lastSlashIdx));
    }

    @Override
    public InMemoryPath withRelativePath(Path relativePath)
    {
        checkArgument(relativePath.isRelative(), "not a relative path");
        return new InMemoryPath(path + "/" + relativePath);
    }
}
