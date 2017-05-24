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

        searchFor(nameEvaluation.getAllCorrespondingFilePatterns(), searchFolder, matchCollector, nameEvaluation.getCorrespondingExtenstion());

        return new MatchResult(matchCollector, createPreferredFileName(nameEvaluation), correspondingSrcFolder, matchSelector);
    }

    private void searchFor(Collection<String> filePatterns, Resource searchFolder, FileMatchCollector matchCollector, String ext)
    {
        if(filePatterns.isEmpty())
            return;
        
        if(0 == ext.length())
        {
            ext = file.getExtension();
        }
        
        Pattern fileNamePattern2 = createFileNamePattern(ext, filePatterns);
        searchEngine.searchFiles(searchFolder, fileNamePattern2, matchCollector);
    }

    private String createPreferredFileName(FileNameEvaluation evaluation)
    {
        String prefferedFileName = "";
        
        if(0 != evaluation.getCorrespondingExtenstion().length())
        {
            prefferedFileName = evaluation.getPreferredCorrespondingFileName() + "." + evaluation.getCorrespondingExtenstion();
        }
        else
        {
            prefferedFileName = evaluation.getPreferredCorrespondingFileName() + "." + file.getExtension();
        }
        
        return prefferedFileName;
    }

    private Pattern createFileNamePattern(String extension, Collection<String> correspondingFileNames)
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
