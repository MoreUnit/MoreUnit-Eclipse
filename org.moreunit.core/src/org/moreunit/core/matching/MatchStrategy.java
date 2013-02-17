package org.moreunit.core.matching;

import org.eclipse.core.resources.IFile;

public enum MatchStrategy
{
    ALL_MATCHES
    {
        @Override
        public FileMatchCollector createMatchCollector(SourceFolderPath correspondingSrcFolder)
        {
            return new FileMatchCollector(correspondingSrcFolder)
            {
                @Override
                protected boolean searchIsOver()
                {
                    return false;
                }
            };
        }
    },
    ANY_MATCH
    {
        @Override
        public FileMatchCollector createMatchCollector(SourceFolderPath correspondingSrcFolder)
        {
            return new FileMatchCollector(correspondingSrcFolder)
            {
                private volatile boolean oneMatchFound;

                @Override
                protected boolean searchIsOver()
                {
                    return oneMatchFound;
                }

                protected void matchFound(IFile file)
                {
                    oneMatchFound = true;
                }
            };
        }
    };

    public abstract FileMatchCollector createMatchCollector(SourceFolderPath correspondingSrcFolder);
}
