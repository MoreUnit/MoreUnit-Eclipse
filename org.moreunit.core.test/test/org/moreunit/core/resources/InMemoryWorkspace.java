package org.moreunit.core.resources;

import static org.moreunit.core.util.Preconditions.checkArgument;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.resources.IFile;
import org.moreunit.core.preferences.Preferences;

public final class InMemoryWorkspace extends InMemoryResourceContainer implements Workspace
{
    private final Map<String, InMemoryProject> projects = new TreeMap<String, InMemoryProject>();

    public InMemoryWorkspace()
    {
        super(InMemoryPath.ROOT, null);
    }

    void add(InMemoryProject project)
    {
        projects.put(project.getName(), project);
    }

    @Override
    protected void addToParent(InMemoryResourceContainer parent)
    {
        // nothing to do
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
    public InMemoryFile getFile(Path filePath)
    {
        checkArgument(filePath.getSegmentCount() > 1, "not a file path");
        return getProject(filePath.getProjectName()).getFile(filePath.relativeToProject());
    }

    @Override
    public InMemoryFolder getFolder(Path folderPath)
    {
        checkArgument(folderPath.getSegmentCount() > 1, "not a folder path");
        return getProject(folderPath.getProjectName()).getFolder(folderPath.relativeToProject());
    }

    @Override
    public final InMemoryResourceContainer getParent()
    {
        return this;
    }

    @Override
    public InMemoryProject getProject(String projectName)
    {
        checkArgument(! projectName.contains("/"), "Illegal project name: " + projectName);

        InMemoryProject project = projects.get(projectName);
        if(project == null)
        {
            project = new InMemoryProject(projectName, this);
            projects.put(projectName, project);
        }
        return project;
    }

    @Override
    public Preferences getPreferences()
    {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Project> listProjects()
    {
        return (List<Project>) keepIfExists(projects.values());
    }

    @Override
    public Path path(String path)
    {
        return new InMemoryPath(path);
    }

    @Override
    public File toFile(IFile platformFile)
    {
        return getFile(platformFile.getFullPath().toString());
    }

    @Override
    public SrcFile toSrcFile(IFile platformFile)
    {
        return new ConcreteSrcFile(toFile(platformFile));
    }
}
