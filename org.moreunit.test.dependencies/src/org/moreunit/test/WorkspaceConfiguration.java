package org.moreunit.test;

public class WorkspaceConfiguration
{
    private final SourceFolderConfiguration productionSourceConfig;
    private final SourceFolderConfiguration testSourceConfig;

    public WorkspaceConfiguration()
    {
        // TODO load this configuration from @Context
        ProjectConfiguration projectConfig = new ProjectConfiguration("moreunit-test-project");
        productionSourceConfig = new SourceFolderConfiguration(projectConfig, "src");
        testSourceConfig = new SourceFolderConfiguration(projectConfig, "junit");
    }

    public SourceFolderConfiguration getProductionSourceConfig()
    {
        return productionSourceConfig;
    }

    public SourceFolderConfiguration getTestSourceConfig()
    {
        return testSourceConfig;
    }
}
