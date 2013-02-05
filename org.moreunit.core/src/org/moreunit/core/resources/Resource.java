package org.moreunit.core.resources;

import org.eclipse.core.resources.IResource;

public interface Resource
{
    void create();

    void delete();

    boolean exists();

    String getName();

    Path getPath();

    ResourceContainer getParent();

    IResource getUnderlyingPlatformResource();
}
