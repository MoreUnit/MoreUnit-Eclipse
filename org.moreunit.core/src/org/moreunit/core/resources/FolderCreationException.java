package org.moreunit.core.resources;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;

public class FolderCreationException extends ResourceException
{
    private static final long serialVersionUID = - 1772490604895269295L;

    private final IFolder folder;

    public FolderCreationException(CoreException cause, IFolder folder)
    {
        super(cause);
        this.folder = folder;
    }

    public IFolder getFolder()
    {
        return folder;
    }
}
