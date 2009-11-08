package org.moreunit;

import org.eclipse.jdt.core.IJavaProject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * @author vera 29.11.2008 14:14:06
 */
public abstract class WorkspaceTestCase
{
    private static final String NAME_OF_WORKSPACE_TEST_PROJECT = "WorkspaceTestProject";
    protected static IJavaProject workspaceTestProject;

    @BeforeClass
    public static void setUpWorkspace() throws Exception
    {
        workspaceTestProject = WorkspaceHelper.createJavaProject(NAME_OF_WORKSPACE_TEST_PROJECT);
    }

    @AfterClass
    public static void tearDownWorkspace() throws Exception
    {
        WorkspaceHelper.deleteProject(workspaceTestProject);
    }
}
