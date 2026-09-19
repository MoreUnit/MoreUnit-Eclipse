package org.moreunit.git;

import org.eclipse.jgit.lib.Repository;

/**
 * A Git repository found by {@link GitRepositories}.
 * <p>
 * The repository may belong to EGit (which keeps it open for the whole
 * workspace session) or have been opened by MoreUnit itself: only the latter
 * is closed by {@link #close()}, so that this handle can always be used in a
 * try-with-resources statement.
 * </p>
 */
public final class GitRepository implements AutoCloseable
{
    private final Repository repository;
    private final boolean ownedByMoreUnit;

    GitRepository(Repository repository, boolean ownedByMoreUnit)
    {
        this.repository = repository;
        this.ownedByMoreUnit = ownedByMoreUnit;
    }

    public Repository getRepository()
    {
        return repository;
    }

    @Override
    public void close()
    {
        if(ownedByMoreUnit)
        {
            repository.close();
        }
    }
}
