package org.moreunit.properties;

import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.moreunit.elements.SourceFolderMapping;
import org.moreunit.preferences.Preferences;

/**
 * @author vera 02.03.2008 10:04:43
 */
public class UnitSourcesContentProvider implements ITreeContentProvider
{

    private IJavaProject javaProjectInput;

    private List<SourceFolderMapping> contentList;

    public UnitSourcesContentProvider(IJavaProject javaProject)
    {
        this.javaProjectInput = javaProject;
        contentList = getListOfSourceFoldersFromPreferences();
    }

    public Object[] getElements(Object inputElement)
    {
        return contentList.toArray();
    }

    public void dispose()
    {
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
        if(newInput == null)
            return;
        this.javaProjectInput = (IJavaProject) newInput;
        contentList = getListOfSourceFoldersFromPreferences();
    }

    private List<SourceFolderMapping> getListOfSourceFoldersFromPreferences()
    {
        return Preferences.getInstance().getSourceMappingList(javaProjectInput);
    }

    public Object[] getChildren(Object parentElement)
    {
        if(parentElement instanceof SourceFolderMapping)
        {
            return ((SourceFolderMapping) parentElement).getSourceFolderList().toArray();
        }
        return getListOfSourceFoldersFromPreferences().toArray();
    }

    public Object getParent(Object element)
    {
        // if(element instanceof IPackageFragmentRoot)
        // return con.get(element);
        // FIXME vom srcfolder zurueck zum SourceFolderMapping

        return null;
    }

    public boolean hasChildren(Object element)
    {
        return element instanceof SourceFolderMapping;
    }

    public void add(SourceFolderMapping mapping)
    {
        contentList.add(mapping);
    }

    public void add(List<SourceFolderMapping> listOfSourceFolder)
    {
        contentList.addAll(listOfSourceFolder);
    }

    public boolean remove(SourceFolderMapping sourceFolder)
    {
        return contentList.remove(sourceFolder);
    }

    public List<SourceFolderMapping> getListOfUnitSourceFolder()
    {
        return contentList;
    }
}
