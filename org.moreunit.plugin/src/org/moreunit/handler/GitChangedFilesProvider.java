package org.moreunit.handler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.moreunit.core.git.GitWorkingTree;
import org.moreunit.log.LogHandler;

/**
 * Provides the Java files of a project which changed in the Git working tree
 * containing the project.
 */
public class GitChangedFilesProvider implements ChangedFilesProvider
{
    private static final String JAVA_EXTENSION = ".java";

    @Override
    public Collection<Path> changedJavaFiles(IJavaProject project)
    {
        final IPath projectLocation = project.getProject().getLocation();
        if(projectLocation == null)
        {
            return Collections.emptyList();
        }
        final Path projectDirectory = projectLocation.toFile().toPath().toAbsolutePath().normalize();

        final Optional<GitWorkingTree> workingTree = GitWorkingTree.locate(projectDirectory);
        if(workingTree.isEmpty())
        {
            LogHandler.getInstance().handleWarnLog("Project " + project.getElementName() + " is not in a Git working tree");
            return Collections.emptyList();
        }

        try
        {
            final Path root = workingTree.get().root();
            return workingTree.get().changedFiles().stream() //
                    .map(root::resolve) //
                    .filter(path -> path.normalize().startsWith(projectDirectory)) //
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
