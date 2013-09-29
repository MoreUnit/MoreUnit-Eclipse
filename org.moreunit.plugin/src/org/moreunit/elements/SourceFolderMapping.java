package org.moreunit.elements;

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
        this.sourceFolder = PluginTools.guessSourceFolderCorrespondingToTestFolder(javaProject, testFolder);
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
