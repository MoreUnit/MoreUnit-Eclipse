package org.moreunit.git;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;

/**
 * The files of a Git working tree which differ from the last commit, as
 * reported by JGit (the Git implementation used by EGit).
 * <p>
 * Staged, unstaged and untracked files are all part of the result, as well as
 * deleted files and files in conflict: the callers decide which ones they can
 * work with.
 * </p>
 */
public class GitWorkingTreeChanges
{
    private final Repository repository;

    public GitWorkingTreeChanges(Repository repository)
    {
        this.repository = repository;
    }

    /**
     * @return the absolute paths of the changed files, empty when the working
     *         tree is clean
     * @throws IOException if the working tree could not be read
     */
    public Collection<Path> changedFiles() throws IOException
    {
        final Status status = status();
        final Set<String> relativePaths = new LinkedHashSet<>();
        relativePaths.addAll(status.getAdded());
        relativePaths.addAll(status.getChanged());
        relativePaths.addAll(status.getModified());
        relativePaths.addAll(status.getRemoved());
        relativePaths.addAll(status.getMissing());
        relativePaths.addAll(status.getUntracked());
        relativePaths.addAll(status.getConflicting());

        final Path workingTree = workingTree();
        final Collection<Path> changedFiles = new LinkedHashSet<>(relativePaths.size());
        for (final String relativePath : relativePaths)
        {
            changedFiles.add(workingTree.resolve(relativePath).toAbsolutePath().normalize());
        }
        return changedFiles;
    }

    /**
     * @return the root of the working tree
     * @throws IOException if the repository has no working tree (a bare
     *             repository, for instance)
     */
    public Path workingTree() throws IOException
    {
        if(repository.isBare() || repository.getWorkTree() == null)
        {
            throw new IOException("Repository " + repository.getDirectory() + " has no working tree");
        }
        return repository.getWorkTree().toPath().toAbsolutePath().normalize();
    }

    private Status status() throws IOException
    {
        try
        {
            // Git.wrap does not take ownership of the repository: closing it
            // would close a repository which may belong to EGit.
            return Git.wrap(repository).status().call();
        }
        catch (final GitAPIException e)
        {
            throw new IOException("Could not read the status of repository " + repository.getDirectory(), e);
        }
    }
}
