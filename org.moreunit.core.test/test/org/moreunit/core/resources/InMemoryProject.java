package org.moreunit.core.resources;

public class InMemoryProject extends InMemoryResourceContainer implements Project
{
    InMemoryProject(String name, InMemoryWorkspace parent)
    {
        super(new InMemoryPath("/" + name), parent);
    }

    @Override
    protected void addToParent(InMemoryResourceContainer parent)
    {
        ((InMemoryWorkspace) parent).add(this);
    }
}
