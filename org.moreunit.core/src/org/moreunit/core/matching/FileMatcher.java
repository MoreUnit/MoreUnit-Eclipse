package org.moreunit.core.matching;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.search.core.text.TextSearchEngine;
import org.eclipse.search.core.text.TextSearchRequestor;
import org.eclipse.search.core.text.TextSearchScope;
import org.moreunit.core.log.Logger;
import org.moreunit.core.preferences.LanguagePreferencesReader;
import org.moreunit.core.preferences.Preferences;

public class FileMatcher
{
    private static final Pattern ANY_CONTENT = Pattern.compile("");

    private final TextSearchEngine searchEngine;
    private final Preferences preferences;
    private final Logger logger;
    private final FileMatchSelector matchSelector;

    public FileMatcher(TextSearchEngine searchEngine, Preferences preferences, FileMatchSelector matchSelector, final Logger logger)
    {
        this.searchEngine = searchEngine;
        this.preferences = preferences;
        this.logger = logger;
        this.matchSelector = matchSelector;
    }

    public MatchingFile match(IFile file) throws DoesNotMatchConfigurationException
    {
        FileNameEvaluation evaluation = evaluateFileName(file);
        SourceFolderPath correspondingSrcFolder = findSrcFolder(file, evaluation);

        ResultCollector rc = new ResultCollector(correspondingSrcFolder);

        IResource searchFolder = correspondingSrcFolder.getResolvedPartAsResource();

        TextSearchScope scope = createSearchScope(file, evaluation.getPreferredCorrespondingFilePatterns(), searchFolder);
        search(scope, rc);

        if(! evaluation.getOtherCorrespondingFilePatterns().isEmpty())
        {
            scope = createSearchScope(file, evaluation.getOtherCorrespondingFilePatterns(), searchFolder);
            search(scope, rc);
        }

        if(rc.results.isEmpty())
        {
            return MatchingFile.notFound(correspondingSrcFolder, evaluation.getPreferredCorrespondingFileName() + "." + file.getFileExtension());
        }
        if(rc.results.size() == 1)
        {
            return MatchingFile.found(rc.results.iterator().next());
        }

        MatchSelection selection = matchSelector.select(rc.results, null);
        if(selection.exists())
        {
            return MatchingFile.found(selection.get());
        }

        return MatchingFile.searchCancelled();
    }

    private SourceFolderPath findSrcFolder(IFile file, FileNameEvaluation evaluation) throws DoesNotMatchConfigurationException
    {
        TestFolderPathPattern folderPathPattern = getPreferencesFor(file).getTestFolderPathPattern();
        IPath folderPath = file.getFullPath().removeLastSegments(1);

        if(evaluation.isTestFile())
        {
            return folderPathPattern.getSrcPathFor(folderPath);
        }
        else
        {
            return folderPathPattern.getTestPathFor(folderPath);
        }
    }

    private TextSearchScope createSearchScope(IFile file, Collection<String> correspondingFileNames, IResource rootResource) throws DoesNotMatchConfigurationException
    {
        Pattern fileNamePattern = createFileNamePattern(file, correspondingFileNames);
        return TextSearchScope.newSearchScope(new IResource[] { rootResource }, fileNamePattern, false);
    }

    private Pattern createFileNamePattern(IFile file, Collection<String> correspondingFileNames)
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

        String extension = file.getFileExtension();

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

    private FileNameEvaluation evaluateFileName(IFile file)
    {
        TestFileNamePattern testFilePattern = getPreferencesFor(file).getTestFileNamePattern();

        String basename = file.getFullPath().removeFileExtension().lastSegment();

        return testFilePattern.evaluate(basename);
    }

    private LanguagePreferencesReader getPreferencesFor(IFile file)
    {
        return preferences.get(file.getProject()).readerForLanguage(file.getFileExtension().toLowerCase());
    }

    private void search(TextSearchScope scope, TextSearchRequestor requestor)
    {
        try
        {
            IStatus searchStatus = searchEngine.search(scope, requestor, ANY_CONTENT, null);

            if(searchStatus.getCode() != IStatus.OK)
            {
                logger.warn("Search failed with status: " + searchStatus);
            }
        }
        catch (Exception e)
        {
            logger.error("Search failed", e);
        }
    }

    private static class ResultCollector extends TextSearchRequestor
    {
        private final Set<IFile> results = new LinkedHashSet<IFile>();
        private final SourceFolderPath correspondingSrcFolder;
        private final boolean checkFolder;

        public ResultCollector(SourceFolderPath correspondingSrcFolder)
        {
            this.correspondingSrcFolder = correspondingSrcFolder;
            checkFolder = ! correspondingSrcFolder.isResolved();
        }

        @Override
        public boolean acceptFile(IFile file) throws CoreException
        {
            if(! checkFolder || correspondingSrcFolder.matches(file))
            {
                results.add(file);
            }
            return false;
        }
    }
}
