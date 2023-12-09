package org.moreunit.core.resources;

import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        var names = new ArrayList<>();
        names.add(".project");
        names.addAll(Arrays.asList(fileNames));
        super.assertContainsFiles(project, names.toArray(new String[0]));
    }

    @Override
    protected void assertContainsFolders(ResourceContainer container, String... folderNames)
    {
        List<String> expectedFolders = new ArrayList<>(namesOf(container.listFolders()));
        expectedFolders.removeIf(".settings"::equals);
        assertThat(expectedFolders).containsExactly((Object[]) folderNames);
    }
}
