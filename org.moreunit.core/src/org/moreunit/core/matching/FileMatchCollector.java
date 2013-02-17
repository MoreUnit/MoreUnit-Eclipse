package org.moreunit.core.matching;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.search.core.text.TextSearchRequestor;

public abstract class FileMatchCollector extends TextSearchRequestor
{
    private final Set<IFile> results = new LinkedHashSet<IFile>();

    private final SourceFolderPath correspondingSrcFolder;
    private final boolean checkFolder;

    protected FileMatchCollector(SourceFolderPath correspondingSrcFolder)
    {
        this.correspondingSrcFolder = correspondingSrcFolder;
        checkFolder = ! correspondingSrcFolder.isResolved();
    }

    @Override
    public boolean acceptFile(IFile file) throws CoreException
    {
        if(searchIsOver())
        {
            return true;
        }

        if(matches(file))
        {
            matchFound(file);
            results.add(file);
        }
        return false;
    }

    protected abstract boolean searchIsOver();

    private boolean matches(IFile file)
    {
        return ! checkFolder || correspondingSrcFolder.matches(file);
    }

    /**
     * To be overridden when needed.
     */
    protected void matchFound(IFile file)
    {
    }

    public Set<IFile> getResults()
    {
        return results;
    }
}
