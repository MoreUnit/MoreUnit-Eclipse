package org.moreunit.core.resources;

import org.moreunit.core.preferences.ProjectPreferences;

public class InMemoryFolder extends InMemoryResourceContainer implements Folder
{
    InMemoryFolder(InMemoryPath path, InMemoryResourceContainer parent)
    {
        super(path, parent);
    }

    @Override
    protected void addToParent(InMemoryResourceContainer parent)
    {
        parent.add(this);
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
}
