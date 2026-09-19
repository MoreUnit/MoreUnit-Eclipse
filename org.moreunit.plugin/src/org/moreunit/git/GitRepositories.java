package org.moreunit.git;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.core.runtime.IPath;
import org.eclipse.egit.core.info.GitInfo;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

/**
 * Locates the Git repository which contains a workspace resource.
 * <p>
 * The closest repository is looked up from the location of the resource with
 * JGit: this is the repository the user is working in, and it is always up to
 * date. When no repository can be found that way (for instance when the
 * project has been connected to a repository which is not one of its parent
 * directories), the Eclipse Git integration is asked through the public
 * {@link GitInfo} adapter: EGit knows the repositories of the workspace,
 * including their specific layout, such as linked working trees or submodules.
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
        final IPath location = resource.getLocation();
        if(location != null)
        {
            final Optional<GitRepository> closestRepository = repositoryOf(location.toFile());
            if(closestRepository.isPresent())
            {
                return closestRepository;
            }
        }

        final GitInfo gitInfo = Adapters.adapt(resource, GitInfo.class);
        if(gitInfo != null && gitInfo.getRepository() != null)
        {
            // the repository belongs to EGit: it must not be closed here
            return Optional.of(new GitRepository(gitInfo.getRepository(), false));
        }
        return Optional.empty();
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
}
