package org.moreunit.core.matching;

import java.util.Collection;
import java.util.regex.Pattern;

import org.moreunit.core.resources.Resource;
import org.moreunit.core.resources.SrcFile;
import org.moreunit.core.util.LRUCache;

public class FileMatcher
{
    /*
     * ⚡ Bolt Performance Optimization
     *
     * 💡 What: Replaced anonymous LinkedHashMap implementation with the existing LRUCache utility.
     * 🎯 Why: Anonymous inner classes create separate .class files and slightly increase classloader memory footprint. Reusing the utility avoids this overhead and adheres to codebase constraints.
     * 📊 Impact: Reduced metaspace memory usage by avoiding the generation of FileMatcher$1.class, leading to slightly faster classloading and smaller distribution size.
     * 🔬 Measurement: JVM metaspace footprint will show one less loaded class (FileMatcher$1).
     */
    private static final int MAX_CACHE_SIZE = 1000;
    private static final java.util.Map<String, Pattern> PATTERN_CACHE = java.util.Collections.synchronizedMap(
        new LRUCache<String, Pattern>(MAX_CACHE_SIZE)
    );

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

        String regex = sb.toString();
        Pattern pattern = PATTERN_CACHE.get(regex);
        if (pattern == null)
        {
            pattern = Pattern.compile(regex);
            PATTERN_CACHE.put(regex, pattern);
        }
        return pattern;
    }
}
