package org.moreunit.core.matching;

import java.util.Collection;

public class FileNameEvaluation
{
    private final boolean testFile;
    private final Collection<String> otherCorrespondingFileNames;
    private final String preferredCorrespondingFilePattern;

    public FileNameEvaluation(boolean testFile, String preferredCorrespondingFilePattern, Collection<String> otherCorrespondingFileNames)
    {
        this.testFile = testFile;
        this.preferredCorrespondingFilePattern = preferredCorrespondingFilePattern;
        this.otherCorrespondingFileNames = otherCorrespondingFileNames;
    }

    public boolean isTestFile()
    {
        return testFile;
    }

    public String getPreferredCorrespondingFilePattern()
    {
        return preferredCorrespondingFilePattern;
    }

    public Collection<String> getOtherCorrespondingFileNames()
    {
        return otherCorrespondingFileNames;
    }
}
