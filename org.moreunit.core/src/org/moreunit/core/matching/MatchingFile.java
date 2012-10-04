package org.moreunit.core.matching;

import org.eclipse.core.resources.IFile;

public class MatchingFile
{
    private final boolean searchCancelled;
    private final IFile file;
    private final SourceFolderPath srcFolderToCreate;
    private final String fileToCreate;

    public static MatchingFile found(IFile file)
    {
        return new MatchingFile(file);
    }

    public static MatchingFile notFound(SourceFolderPath srcFolderToCreate, String fileToCreate)
    {
        return new MatchingFile(srcFolderToCreate, fileToCreate);
    }

    public static MatchingFile searchCancelled()
    {
        return new MatchingFile(true);
    }

    private MatchingFile(IFile file)
    {
        this.searchCancelled = false;
        this.file = file;
        this.srcFolderToCreate = null;
        this.fileToCreate = null;
    }

    private MatchingFile(SourceFolderPath srcFolderToCreate, String fileToCreate)
    {
        this.searchCancelled = false;
        this.file = null;
        this.srcFolderToCreate = srcFolderToCreate;
        this.fileToCreate = fileToCreate;
    }

    private MatchingFile(boolean searchCancelled)
    {
        this.searchCancelled = searchCancelled;
        this.file = null;
        this.srcFolderToCreate = null;
        this.fileToCreate = null;
    }

    public boolean isSearchCancelled()
    {
        return searchCancelled;
    }

    public boolean isFound()
    {
        return file != null;
    }

    public IFile get()
    {
        return file;
    }

    public SourceFolderPath getSrcFolderToCreate()
    {
        return srcFolderToCreate;
    }

    public String getFileToCreate()
    {
        return fileToCreate;
    }

    @Override
    public String toString()
    {
        return String.format("%s(%s)", getClass().getSimpleName(), (isSearchCancelled() ? //
        "search cancelled" : //
        (isFound() ? //
        "found: " + file : //
        "to create: " + srcFolderToCreate + "/" + fileToCreate)));
    }
}
