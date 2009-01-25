package org.moreunit.util;

import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.moreunit.WorkspaceHelper;

public class PluginToolsTest extends TestCase 
{
	public void testGetJavaProjectsFromWorkspace() throws CoreException 
	{
		WorkspaceHelper.createJavaProject("FirstProject");
		WorkspaceHelper.createJavaProject("SecondProject");
		
		List<IJavaProject> javaProjectsFromWorkspace = PluginTools.getJavaProjectsFromWorkspace();
		assertEquals(2, javaProjectsFromWorkspace.size());
	}
}