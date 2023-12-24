package org.moreunit.test.workspace;

import static org.assertj.core.api.Assertions.assertThat;

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
            assertThat(projectHandler.get().getPackageFragmentRoots()).extracting("elementName").contains(srcFolder);
        }
        catch (JavaModelException e)
        {
            throw new RuntimeException(e);
        }
    }
}
