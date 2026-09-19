package org.moreunit.git;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.egit.core.info.GitInfo;
import org.eclipse.jgit.lib.Repository;

/**
 * Asks EGit for the repository of a workspace resource.
 * <p>
 * This class references EGit types: it must only be loaded when the EGit Core
 * bundle is installed (see {@link GitSupport#isEgitAvailable()}).
 * </p>
 */
final class EgitRepositories
{
    private EgitRepositories()
    {
    }

    /**
     * {@code GitInfo} is the public adapter EGit registers for
     * {@code IResource}: it gives access to the repository EGit has already
     * opened for the resource, whatever the layout of the working tree
     * (including linked working trees and submodules).
     *
     * @param resource a workspace resource
     * @return the repository EGit knows for the resource, or <code>null</code>
     *         when EGit does not know it
     */
    static Repository repositoryOf(IResource resource)
    {
        final GitInfo gitInfo = Adapters.adapt(resource, GitInfo.class);
        return gitInfo == null ? null : gitInfo.getRepository();
    }
}
