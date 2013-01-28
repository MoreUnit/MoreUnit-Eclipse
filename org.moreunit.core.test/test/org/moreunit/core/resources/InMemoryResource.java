package org.moreunit.core.resources;

abstract class InMemoryResource implements Resource
{
    private final InMemoryPath path;
    private final InMemoryResourceContainer parent;
    private boolean existing;

    protected InMemoryResource(InMemoryPath path, InMemoryResourceContainer parent)
    {
        this.path = path;
        this.parent = parent;
        if(parent != null)
        {
            addToParent(parent);
        }
    }

    protected abstract void addToParent(InMemoryResourceContainer parent);

    @Override
    public void create()
    {
        getParent().create();
        existing = true;
    }

    @Override
    public void delete()
    {
        existing = false;
    }

    @Override
    public boolean exists()
    {
        return existing;
    }

    @Override
    public String getName()
    {
        return path.getBaseName();
    }

    @Override
    public final InMemoryPath getPath()
    {
        return path;
    }

    @Override
    public InMemoryResourceContainer getParent()
    {
        return parent;
    }

    @Override
    public final String toString()
    {
        return path.toString();
    }

    @Override
    public final int hashCode()
    {
        return toString().hashCode();
    }

    @Override
    public final boolean equals(Object other)
    {
        if(other == this)
            return true;
        if(other == null || other.getClass() != getClass())
            return false;
        return other.toString().equals(toString());
    }
}
