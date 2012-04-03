package org.moreunit.core.matching;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.search.core.text.TextSearchEngine;
import org.eclipse.search.core.text.TextSearchRequestor;
import org.eclipse.search.core.text.TextSearchScope;
import org.moreunit.core.Logger;
import org.moreunit.core.Preferences;
import org.moreunit.core.ui.FileContentProvider;
import org.moreunit.core.ui.FileMatchSelectionDialog;

public class FileMatcher
{
    private static final Pattern ANY_CONTENT = Pattern.compile("");

    private final TextSearchEngine searchEngine;
    private final Preferences preferences;
    private final Logger logger;
    private final FileMatchSelector matchSelector;

    public FileMatcher(TextSearchEngine searchEngine, Preferences preferences, final Logger logger)
    {
        this(searchEngine, preferences, new DefaultFileMatchSelector(logger), logger);
    }

    public FileMatcher(TextSearchEngine searchEngine, Preferences preferences, FileMatchSelector matchSelector, final Logger logger)
    {
        this.searchEngine = searchEngine;
        this.preferences = preferences;
        this.logger = logger;
        this.matchSelector = matchSelector;
    }

    public IFile match(IFile file)
    {
        FileNameEvaluation evaluation = evaluate(file);

        ResultCollector rc = new ResultCollector();
        search(createSearchScope(file, evaluation.getPreferredCorrespondingFilePattern()), rc);

        for (String fileNamePattern : evaluation.getOtherCorrespondingFileNames())
        {
            search(createSearchScope(file, fileNamePattern), rc);
        }

        if(rc.results.size() > 1)
        {
            return matchSelector.select(rc.results, null);
        }
        return rc.results.isEmpty() ? null : rc.results.iterator().next();
    }

    private FileNameEvaluation evaluate(IFile file)
    {
        TestFileNamePattern testFilePattern = preferences.get(file.getProject()).getTestFileNamePattern();

        String basename = file.getFullPath().removeFileExtension().lastSegment();

        return testFilePattern.evaluate(basename);
    }

    private TextSearchScope createSearchScope(IFile file, String correspondingFileNamePattern)
    {
        Pattern searchedFilePattern = Pattern.compile(correspondingFileNamePattern + "\\." + file.getFileExtension());

        IResource[] rootRessources = { file.getProject() };

        return TextSearchScope.newSearchScope(rootRessources, searchedFilePattern, false);
    }

    private void search(TextSearchScope scope, TextSearchRequestor requestor)
    {
        try
        {
            IStatus searchStatus = searchEngine.search(scope, requestor, ANY_CONTENT, null);

            if(searchStatus.getCode() != IStatus.OK)
            {
                logger.warning("Search failed with status: " + searchStatus);
            }
        }
        catch (Exception e)
        {
            logger.error("Search failed", e);
        }
    }

    private static class DefaultFileMatchSelector implements FileMatchSelector
    {
        private final Logger logger;

        private DefaultFileMatchSelector(Logger logger)
        {
            this.logger = logger;
        }

        public IFile select(Collection<IFile> files, IFile preferredFile)
        {
            FileContentProvider contentProvider = new FileContentProvider(files, preferredFile);
            FileMatchSelectionDialog<IFile> dialog = new FileMatchSelectionDialog<IFile>("Jump to...", contentProvider, logger);
            return dialog.getChoice();
        }
    }

    private static class ResultCollector extends TextSearchRequestor
    {
        private final Set<IFile> results = new LinkedHashSet<IFile>();

        @Override
        public boolean acceptFile(IFile file) throws CoreException
        {
            return results.add(file);
        }
    }
}
