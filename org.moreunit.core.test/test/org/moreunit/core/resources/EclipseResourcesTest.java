package org.moreunit.core.resources;

import static com.google.common.collect.Lists.asList;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.junit.After;

public class EclipseResourcesTest extends ResourcesTest
{
    private IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();

    @After
    public void cleanWorkspace() throws Exception
    {
        workspaceRoot.delete(true, true, null);
    }

    @Override
    protected Workspace getWorkspaceToTest() throws Exception
    {
        return EclipseWorkspace.get();
    }

    @Override
    protected void assertContainsFiles(Project project, String... fileNames)
    {
        super.assertContainsFiles(project, asList(".project", fileNames).toArray(new String[fileNames.length + 1]));
    }
}
