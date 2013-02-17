package org.moreunit.core.resources;

import static org.moreunit.core.config.CoreModule.$;
import static org.moreunit.core.util.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.moreunit.core.preferences.Preferences;

public class EclipseWorkspace extends EclipseResourceContainer implements Workspace
{
    private static class InstanceHolder
    {
        static final EclipseWorkspace INSTANCE = new EclipseWorkspace();
    }

    public static Workspace get()
    {
        return InstanceHolder.INSTANCE;
    }

    private IWorkspaceRoot workspaceRoot;

    private EclipseWorkspace()
    {
        super(ResourcesPlugin.getWorkspace().getRoot());
        this.workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
    }

    @Override
    public void create()
    {
        // nothing to do, workspace always exists
    }

    @Override
    public void delete()
    {
        // nothing to do, workspace always exists
    }

    @Override
    public boolean exists()
    {
        // nothing to do, workspace always exists
        return true;
    }

    @Override
    public File getFile(Path filePath)
    {
        checkArgument(filePath.getSegmentCount() > 1, "not a file path");
        return getProject(filePath.getProjectName()).getFile(filePath.relativeToProject());
    }

    @Override
    public Folder getFolder(Path folderPath)
    {
        checkArgument(folderPath.getSegmentCount() > 1, "not a folder path");
        return getProject(folderPath.getProjectName()).getFolder(folderPath.relativeToProject());
    }

    @Override
    public Preferences getPreferences()
    {
        return $().getPreferences();
    }

    @Override
    public Project getProject(String projectName)
    {
        checkArgument(! projectName.contains("/"), "Illegal project name: " + projectName);

        return new EclipseProject(workspaceRoot.getProject(projectName));
    }

    @Override
    public List<Project> listProjects()
    {
        IProject[] projects = workspaceRoot.getProjects();
        List<Project> result = new ArrayList<Project>(projects.length);
        for (IProject p : projects)
        {
            result.add(new EclipseProject(p));
        }
        return result;
    }

    @Override
    public Path path(String path)
    {
        return new EclipsePath(new org.eclipse.core.runtime.Path(path));
    }

    @Override
    public File toFile(IFile platformFile)
    {
        return new EclipseFile(platformFile);
    }

    @Override
    public SrcFile toSrcFile(IFile platformFile)
    {
        return new ConcreteSrcFile(toFile(platformFile));
    }
}
