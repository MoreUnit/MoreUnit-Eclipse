package org.moreunit.test.workspace;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;

public class ProjectAssertions
{
    private final ProjectHandler projectHandler;

    public ProjectAssertions(ProjectHandler projectHandler)
    {
        this.projectHandler = projectHandler;
    }

    public void hasSourceFolder(String srcFolder)
    {
        try
        {
            final IPackageFragmentRoot[] roots = projectHandler.get().getPackageFragmentRoots();
            boolean found = false;
            for (final IPackageFragmentRoot root : roots) {
                if (srcFolder.equals(root.getElementName())) {
                    found = true;
                    break;
                }
            }
            assertTrue(found, "expected source folder: " + srcFolder);
        }
        catch (final JavaModelException e)
        {
            throw new RuntimeException(e);
        }
    }
}
