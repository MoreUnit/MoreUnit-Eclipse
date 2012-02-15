package org.moreunit.util;

import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.junit.Test;
import org.moreunit.test.workspace.WorkspaceHelper;

public class PluginToolsTest
{
    @Test
    public void getJavaProjectsFromWorkspace() throws Exception
    {
        WorkspaceHelper.createJavaProject("FirstProject");
        WorkspaceHelper.createJavaProject("SecondProject");

        List<IJavaProject> javaProjectsFromWorkspace = PluginTools.getJavaProjectsFromWorkspace();
        assertThat(javaProjectsFromWorkspace).hasSize(2);
    }
}
