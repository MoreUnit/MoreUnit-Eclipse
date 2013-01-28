package org.moreunit.core.resources;

import org.moreunit.core.preferences.ProjectPreferences;

public class InMemoryFile extends InMemoryResource implements File
{
    InMemoryFile(InMemoryPath path, InMemoryResourceContainer parent)
    {
        super(path, parent);
    }

    @Override
    protected void addToParent(InMemoryResourceContainer parent)
    {
        parent.add(this);
    }

    @Override
    public String getBaseNameWithoutExtension()
    {
        return getPath().getBaseNameWithoutExtension();
    }

    @Override
    public String getExtension()
    {
        return getPath().getExtension();
    }

    @Override
    public InMemoryProject getProject()
    {
        InMemoryResourceContainer parent = getParent();
        while (! (parent instanceof InMemoryProject))
        {
            parent = getParent();
        }
        return (InMemoryProject) parent;
    }

    @Override
    public ProjectPreferences getProjectPreferences()
    {
        return null;
    }

    @Override
    public boolean hasExtension()
    {
        return getPath().hasExtension();
    }
}
