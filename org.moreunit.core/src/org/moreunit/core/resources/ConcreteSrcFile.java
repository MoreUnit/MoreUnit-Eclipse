package org.moreunit.core.resources;

import static org.moreunit.core.config.CoreModule.$;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.moreunit.core.extension.LanguageExtensionManager;
import org.moreunit.core.matching.DoesNotMatchConfigurationException;
import org.moreunit.core.matching.FileMatcher;
import org.moreunit.core.matching.FileNameEvaluation;
import org.moreunit.core.matching.MatchStrategy;
import org.moreunit.core.matching.MatchingFile;
import org.moreunit.core.matching.SourceFolderPath;
import org.moreunit.core.matching.TestFileNamePattern;
import org.moreunit.core.matching.TestFolderPathPattern;
import org.moreunit.core.preferences.LanguagePreferencesReader;
import org.moreunit.core.preferences.ProjectPreferences;

public class ConcreteSrcFile implements SrcFile
{
    private final File file;
    private final FileMatcher fileMatcher;
    private FileNameEvaluation nameEvaluation;
    private LanguageExtensionManager languageExtensionManager;

    public ConcreteSrcFile(File file)
    {
        this.file = file;
        this.fileMatcher = $().createFileMatcherFor(this);
        this.languageExtensionManager = $().getLanguageExtensionManager();
    }

    @Override
    public void create()
    {
        file.create();
    }

    @Override
    public void delete()
    {
        file.delete();
    }

    @Override
    public FileNameEvaluation evaluateName()
    {
        if(nameEvaluation == null)
        {
            TestFileNamePattern testFilePattern = getLanguagePreferences().getTestFileNamePattern();
            String basename = file.getPath().getBaseNameWithoutExtension();
            nameEvaluation = testFilePattern.evaluate(basename);
        }
        return nameEvaluation;
    }

    @Override
    public boolean exists()
    {
        return file.exists();
    }

    @Override
    public SourceFolderPath findCorrespondingSrcFolder() throws DoesNotMatchConfigurationException
    {
        TestFolderPathPattern folderPathPattern = getLanguagePreferences().getTestFolderPathPattern();
        Path folderPath = getParent().getPath();

        if(isTestFile())
        {
            return folderPathPattern.getSrcPathFor(folderPath);
        }
        else
        {
            return folderPathPattern.getTestPathFor(folderPath);
        }
    }

    @Override
    public MatchingFile findUniqueMatch() throws DoesNotMatchConfigurationException
    {
        return fileMatcher.match(MatchStrategy.ALL_MATCHES).getUniqueMatchingFile();
    }

    @Override
    public String getBaseNameWithoutExtension()
    {
        return file.getBaseNameWithoutExtension();
    }

    @Override
    public String getExtension()
    {
        return file.getExtension();
    }

    private LanguagePreferencesReader getLanguagePreferences()
    {
        return file.getProjectPreferences().readerForLanguage(getExtension().toLowerCase());
    }

    @Override
    public String getName()
    {
        return file.getName();
    }

    @Override
    public ResourceContainer getParent()
    {
        return file.getParent();
    }

    @Override
    public Path getPath()
    {
        return file.getPath();
    }

    @Override
    public Project getProject()
    {
        return file.getProject();
    }

    @Override
    public ProjectPreferences getProjectPreferences()
    {
        return file.getProjectPreferences();
    }

    @Override
    public IFile getUnderlyingPlatformFile()
    {
        return file.getUnderlyingPlatformFile();
    }

    @Override
    public IResource getUnderlyingPlatformResource()
    {
        return file.getUnderlyingPlatformResource();
    }

    @Override
    public boolean hasCorrespondingFiles() throws DoesNotMatchConfigurationException
    {
        return fileMatcher.match(MatchStrategy.ANY_MATCH).matchFound();
    }

    @Override
    public boolean hasDefaultSupport()
    {
        return isSupported() && ! languageExtensionManager.extensionExistsForLanguage(getExtension().toLowerCase());
    }

    @Override
    public boolean hasExtension()
    {
        return file.hasExtension();
    }

    @Override
    public boolean isSupported()
    {
        return file.hasExtension();
    }

    @Override
    public boolean isTestFile()
    {
        return evaluateName().isTestFile();
    }
}
