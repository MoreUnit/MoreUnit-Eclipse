package org.moreunit.core.resources;

public interface Path extends Iterable<String>
{
    String getBaseName();

    String getBaseNameWithoutExtension();

    String getExtension();

    String getProjectName();

    int getSegmentCount();

    boolean hasExtension();

    boolean isAbsolute();

    boolean isEmpty();

    boolean isPrefixOf(Path otherPath);

    boolean isRelative();

    Path relativeToProject();

    Path uptoSegment(int segmentIndex);

    Path withoutLastSegment();

    Path withRelativePath(Path relativePath);
}
