package org.moreunit.core.matching;

import org.eclipse.core.resources.IFile;

public class MatchingFile
{
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

    private MatchingFile(IFile file)
    {
        this.file = file;
        this.srcFolderToCreate = null;
        this.fileToCreate = null;
    }

    private MatchingFile(SourceFolderPath srcFolderToCreate, String fileToCreate)
    {
        this.file = null;
        this.srcFolderToCreate = srcFolderToCreate;
        this.fileToCreate = fileToCreate;
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
}
