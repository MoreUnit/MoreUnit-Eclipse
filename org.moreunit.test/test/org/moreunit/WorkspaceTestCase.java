package org.moreunit;

import org.eclipse.jdt.core.IJavaProject;
import org.junit.After;
import org.junit.Before;

/**
 * @author vera 29.11.2008 14:14:06
 */
public abstract class WorkspaceTestCase
{
    private static final String NAME_OF_WORKSPACE_TEST_PROJECT = "WorkspaceTestProject";
    protected IJavaProject workspaceTestProject;

    @Before
    public void setUp() throws Exception
    {
        workspaceTestProject = WorkspaceHelper.createJavaProject(NAME_OF_WORKSPACE_TEST_PROJECT);
    }

    @After
    public void tearDown() throws Exception
    {
        WorkspaceHelper.deleteProject(workspaceTestProject);
    }
}
