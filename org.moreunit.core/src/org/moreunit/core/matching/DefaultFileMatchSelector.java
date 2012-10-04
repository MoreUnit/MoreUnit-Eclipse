package org.moreunit.core.matching;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.moreunit.core.log.Logger;
import org.moreunit.core.ui.FileContentProvider;
import org.moreunit.core.ui.FileMatchSelectionDialog;

public class DefaultFileMatchSelector implements FileMatchSelector
{
    private final Logger logger;

    public DefaultFileMatchSelector(Logger logger)
    {
        this.logger = logger;
    }

    public MatchSelection select(Collection<IFile> files, IFile preferredFile)
    {
        FileContentProvider contentProvider = new FileContentProvider(files, preferredFile);
        FileMatchSelectionDialog<IFile> dialog = new FileMatchSelectionDialog<IFile>("Jump to...", contentProvider, logger);
        IFile choice = dialog.getChoice();
        return choice == null ? MatchSelection.none() : MatchSelection.file(choice);
    }
}
