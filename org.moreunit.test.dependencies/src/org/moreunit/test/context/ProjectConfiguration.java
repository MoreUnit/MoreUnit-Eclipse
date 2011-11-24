package org.moreunit.test.context;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.Collections;

import org.moreunit.test.workspace.JavaType;

class ProjectConfiguration
{
    private final String projectName;
    private PropertiesConfiguration propertiesConfig;
    private TestProjectConfiguration testProjectConfig;
    private final Collection<JavaType> mainTypes = newArrayList();
    private final Collection<String> mainSources = newArrayList();
    private final Collection<JavaType> testTypes = newArrayList();
    private final Collection<String> testSources = newArrayList();
    private String mainSrcFolder;
    private String testSrcFolder;

    public Collection<JavaType> getMainTypes()
    {
        return mainTypes;
    }

    public void setMainTypes(Collection<JavaType> types)
    {
        if(types != null)
        {
            mainTypes.addAll(types);
        }
    }

    public Collection<String> getMainSources()
    {
        return mainSources;
    }

    public void setMainSources(String[] sources)
    {
        if(sources != null)
        {
            Collections.addAll(mainSources, sources);
        }
    }

    public Collection<JavaType> getTestTypes()
    {
        return testTypes;
    }

    public void setTestTypes(Collection<JavaType> types)
    {
        if(types != null)
        {
            testTypes.addAll(types);
        }
    }

    public Collection<String> getTestSources()
    {
        return testSources;
    }

    public void setTestSources(String[] sources)
    {
        if(sources != null)
        {
            Collections.addAll(testSources, sources);
        }
    }

    public ProjectConfiguration(String projectName)
    {
        this.projectName = projectName;
    }

    public String getProjectName()
    {
        return projectName;
    }

    public PropertiesConfiguration getPropertiesConfig()
    {
        return propertiesConfig;
    }

    public void setPropertiesConfig(PropertiesConfiguration propertiesConfig)
    {
        this.propertiesConfig = propertiesConfig;
    }

    public TestProjectConfiguration getTestProjectConfig()
    {
        return testProjectConfig;
    }

    public void setTestProjectConfig(TestProjectConfiguration testProjectConfig)
    {
        this.testProjectConfig = testProjectConfig;
    }

    public String getMainSourceFolder()
    {
        return mainSrcFolder;
    }

    public void setMainSourceFolder(String mainSrcFolder)
    {
        this.mainSrcFolder = mainSrcFolder;
    }

    public String getTestSourceFolder()
    {
        return testSrcFolder;
    }

    public void setTestSourceFolder(String testSrcFolder)
    {
        this.testSrcFolder = testSrcFolder;
    }
}
