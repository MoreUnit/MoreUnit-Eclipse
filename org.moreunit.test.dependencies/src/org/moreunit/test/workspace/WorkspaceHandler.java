package org.moreunit.test.workspace;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;

public class WorkspaceHandler
{
    private final Map<String, ProjectHandler> projectHandlers = newHashMap();
    private final String projectPrefix;
    private final Class< ? > loadingClass;

    public WorkspaceHandler(Class< ? > loadingClass, String projectPrefix)
    {
        this.loadingClass = loadingClass;
        this.projectPrefix = projectPrefix;
    }

    public ProjectHandler addProject(String projectName)
    {
        ProjectHandler projectHandler = newProjectHandler(this, projectName);
        projectHandlers.put(projectName, projectHandler);
        return projectHandler;
    }

    protected ProjectHandler newProjectHandler(WorkspaceHandler workspaceHandler, String projectName)
    {
        return new ProjectHandler(workspaceHandler, projectName, projectPrefix);
    }

    Class< ? > getLoadingClass()
    {
        return loadingClass;
    }

    public CompilationUnitHandler getCompilationUnitHandler(String cuName)
    {
        try
        {
            CompilationUnitHandler cuHandler = findInWorkspace(cuName);
            if(cuHandler != null)
            {
                return cuHandler;
            }
            throw new IllegalArgumentException("No compilation unit defined with name: " + cuName);
        }
        catch (CoreException e)
        {
            throw new RuntimeException("Could not load compilation unit: " + cuName, e);
        }
    }

    protected CompilationUnitHandler findInWorkspace(String cuName) throws CoreException
    {
        for (ProjectHandler projectHandler : projectHandlers.values())
        {
            CompilationUnitHandler cuHandler = projectHandler.findCompilationUnit(cuName);
            if(cuHandler != null)
            {
                return cuHandler;
            }
        }
        return null;
    }

    public ProjectHandler getProjectHandler(String projectName)
    {
        return projectHandlers.get(projectName);
    }

    public void clearWorkspace()
    {
        for (ProjectHandler projectHandler : projectHandlers.values())
        {
            try
            {
                WorkspaceHelper.deleteProject(projectHandler.get());
            }
            catch (CoreException e)
            {
                e.printStackTrace();
            }
        }
    }
}
