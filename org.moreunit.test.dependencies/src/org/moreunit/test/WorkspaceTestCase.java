package org.moreunit.test;

import org.eclipse.jdt.core.IJavaProject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.moreunit.test.workspace.WorkspaceHelper;

/**
 * @author vera 29.11.2008 14:14:06
 */
public abstract class WorkspaceTestCase
{
    private static final String NAME_OF_WORKSPACE_TEST_PROJECT = "WorkspaceTestProject";
    protected static IJavaProject workspaceTestProject;

    @BeforeAll
    public static void setUpWorkspace() throws Exception
    {
        workspaceTestProject = WorkspaceHelper.createJavaProject(NAME_OF_WORKSPACE_TEST_PROJECT);
    }

    @AfterAll
    public static void tearDownWorkspace() throws Exception
    {
        WorkspaceHelper.deleteProject(workspaceTestProject);
    }
}
