package org.moreunit.core.resources;

import org.eclipse.core.resources.IFile;
import org.moreunit.core.matching.DoesNotMatchConfigurationException;
import org.moreunit.core.matching.FileNameEvaluation;
import org.moreunit.core.matching.MatchingFile;
import org.moreunit.core.matching.SourceFolderPath;

public interface SrcFile extends File
{
    FileNameEvaluation evaluateName();

    SourceFolderPath findCorrespondingSrcFolder() throws DoesNotMatchConfigurationException;

    MatchingFile findUniqueMatch() throws DoesNotMatchConfigurationException;

    String getExtension();

    IFile getUnderlyingPlatformFile();

    boolean hasCorrespondingFiles() throws DoesNotMatchConfigurationException;

    boolean hasDefaultSupport();

    boolean isSupported();

    boolean isTestFile();

}
