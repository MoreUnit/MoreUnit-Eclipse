package org.moreunit.git;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

/**
 * Locates the Git repository which contains a workspace resource, using the
 * Eclipse Git integration when it is available.
 * <p>
 * EGit is asked first: it already knows the repositories of the workspace
 * (including their specific layout, such as linked working trees or
 * submodules) and it already has them open. When EGit is not installed, or
 * does not know the resource, the repository is looked up directly with JGit.
 * </p>
 */
public final class GitRepositories
{
    private GitRepositories()
    {
    }

    /**
     * @param resource a workspace resource
     * @return the repository containing the resource, or empty when the
     *         resource is not in a Git repository
     */
    public static Optional<GitRepository> repositoryOf(IResource resource)
    {
        final Repository egitRepository = repositoryKnownToEgit(resource);
        if(egitRepository != null)
        {
            // the repository belongs to EGit: it must not be closed here
            return Optional.of(new GitRepository(egitRepository, false));
        }
        final IPath location = resource.getLocation();
        if(location == null)
        {
            return Optional.empty();
        }
        return repositoryOf(location.toFile());
    }

    /**
     * @param directory a directory
     * @return the repository containing the directory, or empty when the
     *         directory is not in a Git repository
     */
    public static Optional<GitRepository> repositoryOf(Path directory)
    {
        return repositoryOf(directory.toFile());
    }

    private static Optional<GitRepository> repositoryOf(File directory)
    {
        final FileRepositoryBuilder builder = new FileRepositoryBuilder();
        builder.findGitDir(directory);
        if(builder.getGitDir() == null)
        {
            return Optional.empty();
        }
        try
        {
            // the repository has been opened by MoreUnit: it is closed by the handle
            return Optional.of(new GitRepository(builder.build(), true));
        }
        catch (final IOException e)
        {
            // the ".git" entry is not a valid repository
            return Optional.empty();
        }
    }

    private static Repository repositoryKnownToEgit(IResource resource)
    {
        if(! GitSupport.isEgitAvailable())
        {
            return null;
        }
        try
        {
            return EgitRepositories.repositoryOf(resource);
        }
        catch (final NoClassDefFoundError e)
        {
            // EGit disappeared between the check and the call
            return null;
        }
    }
}
