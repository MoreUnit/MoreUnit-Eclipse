package org.moreunit.core.resources;

public interface Resource
{
    void create();

    void delete();

    boolean exists();

    String getName();

    Path getPath();

    ResourceContainer getParent();
}
