package org.moreunit.elements;

import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.moreunit.util.PluginTools;

/**
 * @author vera 15.03.2008 12:06:30
 */
public class SourceFolderMapping
{

    private IJavaProject javaProject;
    private IPackageFragmentRoot sourceFolder;
    private IPackageFragmentRoot testFolder;

    public SourceFolderMapping(IJavaProject javaProject, IPackageFragmentRoot sourceFolder, IPackageFragmentRoot testFolder)
    {
        this.javaProject = javaProject;
        this.sourceFolder = sourceFolder;
        this.testFolder = testFolder;
    }

    public SourceFolderMapping(IJavaProject javaProject, IPackageFragmentRoot testFolder)
    {
        this.javaProject = javaProject;
        this.testFolder = testFolder;
        this.sourceFolder = getPreferredSourceFolder();
    }

    private IPackageFragmentRoot getPreferredSourceFolder()
    {
        List<IPackageFragmentRoot> packageFragmentRoots = PluginTools.getAllSourceFolderFromProject(javaProject);
        if(hasProjectsNoSourceFolders(packageFragmentRoots))
            return null;
        if(hasProjectsOnlyOneSourceFolder(packageFragmentRoots))
            return packageFragmentRoots.get(0);

        // if there are more than one sourcefolder in the project the user has
        // to choose manually
        return packageFragmentRoots.get(0); // TODO hack
    }

    private boolean hasProjectsNoSourceFolders(List<IPackageFragmentRoot> packageFragmentRoots)
    {
        return packageFragmentRoots == null || packageFragmentRoots.size() == 0;
    }

    private boolean hasProjectsOnlyOneSourceFolder(List<IPackageFragmentRoot> packageFragmentRoots)
    {
        return packageFragmentRoots.size() == 1;
    }

    public void setSourceFolder(IPackageFragmentRoot sourceFolder)
    {
        this.sourceFolder = sourceFolder;
    }

    public IJavaProject getJavaProject()
    {
        return javaProject;
    }

    public IPackageFragmentRoot getSourceFolder()
    {
        return sourceFolder;
    }

    public IPackageFragmentRoot getTestFolder()
    {
        return testFolder;
    }

    @Override
    public String toString()
    {
        return String.format("%s(%s:%s => %s:%s)", SourceFolderMapping.class.getSimpleName(), sourceFolder.getJavaProject().getElementName(), sourceFolder.getElementName(), testFolder.getJavaProject().getElementName(), testFolder.getElementName());
    }
}
