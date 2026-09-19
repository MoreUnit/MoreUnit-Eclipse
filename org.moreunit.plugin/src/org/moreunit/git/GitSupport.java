package org.moreunit.git;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

/**
 * Tells whether the Eclipse Git implementation (JGit, and its EGit
 * integration) is available at runtime. MoreUnit does not require Git: the
 * commands which use it are hidden when it is not installed.
 */
public final class GitSupport
{
    public static final String JGIT_BUNDLE_ID = "org.eclipse.jgit";
    public static final String EGIT_CORE_BUNDLE_ID = "org.eclipse.egit.core";

    private GitSupport()
    {
    }

    /**
     * @return true when JGit is installed, i.e. when MoreUnit can inspect Git
     *         repositories
     */
    public static boolean isAvailable()
    {
        return isAvailable(JGIT_BUNDLE_ID);
    }

    /**
     * @return true when EGit is installed, i.e. when MoreUnit can rely on the
     *         repositories EGit knows about
     */
    public static boolean isEgitAvailable()
    {
        return isAvailable(EGIT_CORE_BUNDLE_ID);
    }

    /**
     * @param bundleId a bundle symbolic name
     * @return true when the bundle is installed in the running Eclipse
     */
    public static boolean isAvailable(String bundleId)
    {
        if(Platform.getBundle(bundleId) != null)
        {
            return true;
        }
        // The bundle may not be in the bundle context yet (e.g. while the
        // workbench starts): fall back to the installed bundles.
        final Bundle[] bundles = Platform.getBundles(bundleId, null);
        return bundles != null && bundles.length > 0;
    }
}
