package org.moreunit.core.matching;

import org.eclipse.core.resources.IFile;

public final class MatchSelection
{
    public static MatchSelection file(IFile file)
    {
        return new MatchSelection(true, file);
    }

    public static MatchSelection none()
    {
        return new MatchSelection(false, null);
    }

    private final boolean selection;
    private final IFile file;

    private MatchSelection(boolean selection, IFile file)
    {
        this.selection = selection;
        this.file = file;
    }

    public boolean exists()
    {
        return selection;
    }

    public IFile get()
    {
        return file;
    }
}
