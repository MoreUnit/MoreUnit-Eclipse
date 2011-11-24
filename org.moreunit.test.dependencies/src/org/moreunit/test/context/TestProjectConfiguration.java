package org.moreunit.test.context;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.Collections;

import org.moreunit.test.workspace.JavaType;

class TestProjectConfiguration
{
    private final String projectName;
    private final Collection<String> sources = newArrayList();
    private final Collection<JavaType> types = newArrayList();
    private String sourceFolder;

    public TestProjectConfiguration(String projectName)
    {
        this.projectName = projectName;
    }

    public String getProjectName()
    {
        return projectName;
    }

    public Collection<String> getSources()
    {
        return sources;
    }

    public void setSources(String[] sources)
    {
        if(sources != null)
        {
            Collections.addAll(this.sources, sources);
        }
    }

    public Collection<JavaType> getTypes()
    {
        return types;
    }

    public void setTypes(Collection<JavaType> types)
    {
        if(types != null)
        {
            this.types.addAll(types);
        }
    }

    public void setSourceFolder(String sourceFolder)
    {
        this.sourceFolder = sourceFolder;
    }

    public String getSourceFolder()
    {
        return sourceFolder;
    }
}
