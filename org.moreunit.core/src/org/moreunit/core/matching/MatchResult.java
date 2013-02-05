package org.moreunit.core.matching;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.search.core.text.TextSearchRequestor;

public class MatchResult extends TextSearchRequestor
{
    private final Set<IFile> results = new LinkedHashSet<IFile>();
    private final String preferredFileName;
    private final SourceFolderPath correspondingSrcFolder;
    private final FileMatchSelector matchSelector;
    private final boolean checkFolder;

    public MatchResult(String preferredFileName, SourceFolderPath correspondingSrcFolder, FileMatchSelector matchSelector)
    {
        this.preferredFileName = preferredFileName;
        this.correspondingSrcFolder = correspondingSrcFolder;
        this.matchSelector = matchSelector;
        checkFolder = ! correspondingSrcFolder.isResolved();
    }

    public MatchingFile getUniqueMatchingFile()
    {
        if(results.isEmpty())
        {
            return MatchingFile.notFound(correspondingSrcFolder, preferredFileName);
        }
        if(results.size() == 1)
        {
            return MatchingFile.found(results.iterator().next());
        }

        MatchSelection selection = matchSelector.select(results, null);
        if(selection.exists())
        {
            return MatchingFile.found(selection.get());
        }

        return MatchingFile.searchCancelled();
    }

    @Override
    public boolean acceptFile(IFile file) throws CoreException
    {
        if(! checkFolder || correspondingSrcFolder.matches(file))
        {
            results.add(file);
        }
        return false;
    }
}
