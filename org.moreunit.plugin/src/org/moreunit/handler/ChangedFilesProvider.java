package org.moreunit.handler;

import java.nio.file.Path;
import java.util.Collection;

import org.eclipse.jdt.core.IJavaProject;

/**
 * Provides the Java files of a project which changed in its version control
 * working tree.
 */
public interface ChangedFilesProvider
{
    /**
     * @param project the project to inspect
     * @return the absolute paths of the changed Java files, or an empty
     *         collection if there is none or if the project could not be
     *         inspected
     */
    Collection<Path> changedJavaFiles(IJavaProject project);
}
