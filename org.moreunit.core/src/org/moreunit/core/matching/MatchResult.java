package org.moreunit.core.matching;

import java.util.Set;

import org.eclipse.core.resources.IFile;

public class MatchResult
{
    private final FileMatchCollector matchCollector;
    private final String preferredFileName;
    private final SourceFolderPath correspondingSrcFolder;
    private final FileMatchSelector matchSelector;

    public MatchResult(FileMatchCollector matchCollector, String preferredFileName, SourceFolderPath correspondingSrcFolder, FileMatchSelector matchSelector)
    {
        this.matchCollector = matchCollector;
        this.preferredFileName = preferredFileName;
        this.correspondingSrcFolder = correspondingSrcFolder;
        this.matchSelector = matchSelector;
    }

    public MatchingFile getUniqueMatchingFile()
    {
        Set<IFile> results = matchCollector.getResults();
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

    public boolean matchFound()
    {
        return ! matchCollector.getResults().isEmpty();
    }
}
