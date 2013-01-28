package org.moreunit.core.resources;

public class InMemoryResourcesTest extends ResourcesTest
{
    @Override
    protected Workspace getWorkspaceToTest() throws Exception
    {
        return new InMemoryWorkspace();
    }
}
