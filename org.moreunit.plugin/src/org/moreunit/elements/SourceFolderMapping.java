package org.moreunit.elements;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.moreunit.core.util.StringConstants;
import org.moreunit.core.util.Strings;
import org.moreunit.util.PluginTools;

/**
 * @author vera 15.03.2008 12:06:30
 */
public class SourceFolderMapping
{
    private IJavaProject javaProject;
    private List<IPackageFragmentRoot> sourceFolderList = new ArrayList<IPackageFragmentRoot>();
    private IPackageFragmentRoot testFolder;

    public SourceFolderMapping(IJavaProject javaProject, IPackageFragmentRoot sourceFolder, IPackageFragmentRoot testFolder)
    {
        this.javaProject = javaProject;
        this.sourceFolderList.add(sourceFolder);
        this.testFolder = testFolder;
    }

    public SourceFolderMapping(IJavaProject javaProject, IPackageFragmentRoot testFolder)
    {
        this.javaProject = javaProject;
        this.testFolder = testFolder;
        this.sourceFolderList.add(PluginTools.guessSourceFolderCorrespondingToTestFolder(javaProject, testFolder));
    }

    public void setSourceFolderList(List<IPackageFragmentRoot> sourceFolderList)
    {
        this.sourceFolderList = sourceFolderList;
    }

    public IJavaProject getJavaProject()
    {
        return javaProject;
    }

    public List<IPackageFragmentRoot> getSourceFolderList()
    {
        return sourceFolderList;
    }

    public IPackageFragmentRoot getTestFolder()
    {
        return testFolder;
    }

    @Override
    public String toString()
    {
        List<String> toStringParts = new ArrayList<String>();
        toStringParts.add(SourceFolderMapping.class.getSimpleName());
        for (IPackageFragmentRoot sourceFolder : sourceFolderList)
        {
            toStringParts.add(String.format(" (%s:%s => %s:%s)", sourceFolder.getJavaProject().getElementName(), sourceFolder.getElementName(), testFolder.getJavaProject().getElementName(), testFolder.getElementName()));
        }
        return Strings.join(StringConstants.EMPTY_STRING, toStringParts);
    }
}
