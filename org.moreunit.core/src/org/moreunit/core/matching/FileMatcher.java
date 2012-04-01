package org.moreunit.core.matching;

import java.util.HashSet;
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

public class FileMatcher
{

    private static final Pattern ANY_CONTENT = Pattern.compile("");

    private final TextSearchEngine searchEngine;
    private final Preferences preferences;
    private final Logger logger;

    public FileMatcher(TextSearchEngine searchEngine, Preferences preferences, Logger logger)
    {
        this.searchEngine = searchEngine;
        this.preferences = preferences;
        this.logger = logger;
    }

    public IFile match(IFile file)
    {
        FileNameEvaluation evaluation = evaluate(file);

        TextSearchScope firstSearchScope = createSearchScope(file, evaluation);

        // TextSearchScope secondSearchScope = createSearchScope(file,
        // evaluation);

        ResultCollector resultCollector = new ResultCollector();

        search(firstSearchScope, resultCollector);

        if(resultCollector.hasResults())
        {
            return resultCollector.firstResult();
        }
        return null;
    }

    private FileNameEvaluation evaluate(IFile file)
    {
        TestFileNamePattern testFilePattern = preferences.get(file.getProject()).getTestFileNamePattern();

        String basename = file.getFullPath().removeFileExtension().lastSegment();

        return testFilePattern.evaluate(basename);
    }

    private TextSearchScope createSearchScope(IFile file, FileNameEvaluation evaluation)
    {
        String correspondingFileName = evaluation.getPreferredCorrespondingFilePattern();

        // Collection<String> correspondingFileNames =
        // testFilePattern.evaluate(selectedFileBasename).getOtherCorrespondingFileNames();
        // correspondingFileName = correspondingFileNames.iterator().next();

        Pattern searchedFilePattern = Pattern.compile(correspondingFileName + "\\.js");

        IResource[] rootRessources = { file.getProject() };

        // to extend search to the whole workspace:
        // IProject[] projects =
        // ResourcesPlugin.getWorkspace().getRoot().getProjects();

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

    private static class ResultCollector extends TextSearchRequestor
    {

        private final Set<IFile> files = new HashSet<IFile>();

        @Override
        public boolean acceptFile(IFile file) throws CoreException
        {
            return files.add(file);
        }

        public IFile firstResult()
        {
            return files.iterator().next();
        }

        public boolean hasResults()
        {
            return ! files.isEmpty();
        }
    }
}
