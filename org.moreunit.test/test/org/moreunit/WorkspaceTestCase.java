package org.moreunit;

import org.eclipse.jdt.core.IJavaProject;

import junit.framework.TestCase;

/**
 * @author vera
 *
 * 29.11.2008 14:14:06
 */
public abstract class WorkspaceTestCase extends TestCase 
{
	private static final String NAME_OF_WORKSPACE_TEST_PROJECT = "WorkspaceTestProject";
	protected IJavaProject workspaceTestProject;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		workspaceTestProject = WorkspaceHelper.createJavaProject(NAME_OF_WORKSPACE_TEST_PROJECT);
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		WorkspaceHelper.deleteProject(workspaceTestProject);
	}
}
