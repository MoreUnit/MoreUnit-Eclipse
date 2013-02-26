package org.moreunit.core.matching;

import java.util.Collection;
import java.util.regex.Pattern;

import org.moreunit.core.resources.Resource;
import org.moreunit.core.resources.SrcFile;

public class FileMatcher
{
    private final SrcFile file;
    private final SearchEngine searchEngine;
    private final FileMatchSelector matchSelector;

    public FileMatcher(SrcFile file, SearchEngine searchEngine, FileMatchSelector matchSelector)
    {
        this.file = file;
        this.searchEngine = searchEngine;
        this.matchSelector = matchSelector;
    }

    public MatchResult match(MatchStrategy strategy) throws DoesNotMatchConfigurationException
    {
        FileNameEvaluation nameEvaluation = file.evaluateName();
        SourceFolderPath correspondingSrcFolder = file.findCorrespondingSrcFolder();

        FileMatchCollector matchCollector = strategy.createMatchCollector(correspondingSrcFolder);
        Resource searchFolder = correspondingSrcFolder.getResolvedPartAsResource();

        searchFor(nameEvaluation.getAllCorrespondingFilePatterns(), searchFolder, matchCollector);

        return new MatchResult(matchCollector, createPreferredFileName(nameEvaluation), correspondingSrcFolder, matchSelector);
    }

    private void searchFor(Collection<String> filePatterns, Resource searchFolder, FileMatchCollector matchCollector)
    {
        if(filePatterns.isEmpty())
            return;

        Pattern fileNamePattern2 = createFileNamePattern(file, filePatterns);
        searchEngine.searchFiles(searchFolder, fileNamePattern2, matchCollector);
    }

    private String createPreferredFileName(FileNameEvaluation evaluation)
    {
        return evaluation.getPreferredCorrespondingFileName() + "." + file.getExtension();
    }

    private Pattern createFileNamePattern(SrcFile file, Collection<String> correspondingFileNames)
    {
        StringBuilder sb = null;
        // creates an OR pattern with file names
        for (String fileName : correspondingFileNames)
        {
            if(sb == null)
            {
                sb = new StringBuilder("(");
            }
            else
            {
                sb.append("|");
            }
            sb.append(fileName);
        }

        sb.append(")");

        String extension = file.getExtension();

        // creates an OR pattern with the file extension: same case OR lower
        // case OR upper case (so a file having an extension with a mixed case
        // different to the one of the current file won't be found, unless we
        // discover how to specify that only a pattern part should be case
        // insensitive)
        sb.append("\\.(").append(extension) //
        .append("|").append(extension.toLowerCase()) //
        .append("|").append(extension.toUpperCase()) //
        .append(")");

        return Pattern.compile(sb.toString());
    }
}
