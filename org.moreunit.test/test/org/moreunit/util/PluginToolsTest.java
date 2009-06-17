package org.moreunit.util;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.junit.Test;
import org.moreunit.WorkspaceHelper;

public class PluginToolsTest
{
    @Test
    public void testGetJavaProjectsFromWorkspace() throws CoreException
    {
        WorkspaceHelper.createJavaProject("FirstProject");
        WorkspaceHelper.createJavaProject("SecondProject");

        List<IJavaProject> javaProjectsFromWorkspace = PluginTools.getJavaProjectsFromWorkspace();
        assertEquals(2, javaProjectsFromWorkspace.size());
    }
}
