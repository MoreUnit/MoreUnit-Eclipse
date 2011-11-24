package org.moreunit.test.workspace;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.moreunit.test.workspace.ProjectHandler.ProjectAssertions;

public class ProjectHandler implements ElementHandler<IJavaProject, ProjectAssertions>
{
    private final WorkspaceHandler workspaceHandler;
    private final String projectName;
    private final Map<String, SourceFolderHandler> sourceFolders = newHashMap();
    private IJavaProject project;
    private SourceFolderHandler mainSrcFolderHandler;
    private SourceFolderHandler testSrcFolderHandler;

    public ProjectHandler(WorkspaceHandler workspaceHandler, String projectName)
    {
        this.workspaceHandler = workspaceHandler;
        this.projectName = projectName;
    }

    public IJavaProject get()
    {
        if(project == null)
        {
            try
            {
                project = WorkspaceHelper.createJavaProject(projectName);
            }
            catch (CoreException e)
            {
                throw new RuntimeException(e);
            }
        }
        return project;
    }

    public WorkspaceHandler getWorkspaceHandler()
    {
        return workspaceHandler;
    }

    public ProjectAssertions assertThat()
    {
        return new ProjectAssertions();
    }

    public SourceFolderHandler getMainSrcFolderHandler()
    {
        return mainSrcFolderHandler;
    }

    public void setMainSrcFolderHandler(SourceFolderHandler srcFolderHandler)
    {
        this.mainSrcFolderHandler = srcFolderHandler;
        sourceFolders.put(srcFolderHandler.getName(), srcFolderHandler);
    }

    /**
     * Returns a handler on the test source folder associated to this project
     * (whether the source folder belongs to this project or not).
     */
    public SourceFolderHandler getTestSrcFolderHandler()
    {
        return testSrcFolderHandler;
    }

    public void setTestSrcFolderHandler(SourceFolderHandler srcFolderHandler)
    {
        this.testSrcFolderHandler = srcFolderHandler;
        sourceFolders.put(srcFolderHandler.getName(), srcFolderHandler);
    }

    public static class ProjectAssertions
    {
        public void noAssertionsImplementedYet()
        {
            throw new UnsupportedOperationException("no assertions implemented yet");
        }
    }

    public CompilationUnitHandler findCompilationUnit(String cuName)
    {
        for (SourceFolderHandler srcFolderHandler : sourceFolders.values())
        {
            CompilationUnitHandler cuHandler = srcFolderHandler.findCompilationUnit(cuName);
            if(cuHandler != null)
            {
                return cuHandler;
            }
        }

        IType type = findType(cuName);

        if(type == null)
        {
            return null;
        }

        ICompilationUnit cu = type.getCompilationUnit();
        String srcFolderName = cu.getParent().getElementName();

        final SourceFolderHandler srcFolderHandler;
        if(sourceFolders.containsKey(srcFolderName))
        {
            srcFolderHandler = sourceFolders.get(srcFolderName);
        }
        else
        {
            srcFolderHandler = new SourceFolderHandler(this, srcFolderName);
        }

        return srcFolderHandler.createHandlerFor(cu);
    }

    protected IType findType(String typeName)
    {
        try
        {
            return get().findType(typeName);
        }
        catch (JavaModelException e)
        {
            throw new RuntimeException(e);
        }
    }
}
