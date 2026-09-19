package org.moreunit.git;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.moreunit.log.LogHandler;

/**
 * Provides the Java files of a project which changed in its Git working tree,
 * using JGit - the Git implementation of Eclipse, on which EGit is built.
 */
public class GitChangedFilesProvider implements ChangedFilesProvider
{
    private static final String JAVA_EXTENSION = ".java";

    @Override
    public Collection<Path> changedJavaFiles(IJavaProject project)
    {
        final IProject workspaceProject = project.getProject();
        final IPath projectLocation = workspaceProject.getLocation();
        if(projectLocation == null)
        {
            return Collections.emptyList();
        }
        final Path projectDirectory = projectLocation.toFile().toPath().toAbsolutePath().normalize();

        final Optional<GitRepository> repository = GitRepositories.repositoryOf(workspaceProject);
        if(repository.isEmpty())
        {
            LogHandler.getInstance().handleWarnLog("Project " + project.getElementName() + " is not in a Git repository");
            return Collections.emptyList();
        }

        try (GitRepository gitRepository = repository.get())
        {
            final GitWorkingTreeChanges changes = new GitWorkingTreeChanges(gitRepository.getRepository());
            return changes.changedFiles().stream() //
                    .filter(path -> path.startsWith(projectDirectory)) //
                    .filter(GitChangedFilesProvider::isJavaFile) //
                    .filter(Files::isRegularFile) //
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }
        catch (final IOException e)
        {
            LogHandler.getInstance().handleExceptionLog("Could not determine the files changed in project " + project.getElementName(), e);
            return Collections.emptyList();
        }
    }

    private static boolean isJavaFile(Path path)
    {
        return path.getFileName().toString().endsWith(JAVA_EXTENSION);
    }
}
