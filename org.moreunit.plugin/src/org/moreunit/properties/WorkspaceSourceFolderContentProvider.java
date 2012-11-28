package org.moreunit.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.moreunit.elements.SourceFolderMapping;
import org.moreunit.log.LogHandler;

/**
 * @author vera 02.03.2008 19:28:41
 */
public class WorkspaceSourceFolderContentProvider implements ITreeContentProvider
{

    private List<IPackageFragmentRoot> selectedUnitSourceFolderFromPreferences = new ArrayList<IPackageFragmentRoot>();

    public WorkspaceSourceFolderContentProvider(List<SourceFolderMapping> selectedUnitSourceFolderFromPreferences)
    {
        for (SourceFolderMapping mapping : selectedUnitSourceFolderFromPreferences)
        {
            this.selectedUnitSourceFolderFromPreferences.add(mapping.getTestFolder());
        }
    }

    public Object[] getElements(Object inputElement)
    {
        if(inputElement instanceof IJavaProject)
            return getChildren(inputElement);

        return getRelevantJavaProjectsInWorkspace().toArray();
    }

    public void dispose()
    {

    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
    }

    public Object[] getChildren(Object parentElement)
    {
        if(parentElement instanceof IJavaProject)
            return getRelevantSourceFolderForProject((IJavaProject) parentElement).toArray();

        return new Object[0];
    }

    public Object getParent(Object element)
    {
        if(element instanceof IPackageFragmentRoot)
            return ((IPackageFragmentRoot) element).getJavaProject();

        return null;
    }

    public boolean hasChildren(Object element)
    {
        if(element instanceof IJavaProject)
            return ! getRelevantSourceFolderForProject((IJavaProject) element).isEmpty();

        return false;
    }

    /**
     * Returns all java projects from workspace which have source folder not
     * selected as test source folder for the underlying project
     */
    private List<IJavaProject> getRelevantJavaProjectsInWorkspace()
    {
        List<IJavaProject> allJavaProjectsInWorkspace = new ArrayList<IJavaProject>();

        IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
        for (IProject aProject : projects)
        {
            try
            {
                if(aProject.isAccessible() && aProject.hasNature(JavaCore.NATURE_ID))
                {
                    IJavaProject javaProject = JavaCore.create(aProject);
                    if(hasChildren(javaProject))
                        allJavaProjectsInWorkspace.add(JavaCore.create(aProject));
                }
            }
            catch (CoreException e)
            {
                LogHandler.getInstance().handleExceptionLog(e);
            }
        }

        return allJavaProjectsInWorkspace;
    }

    /**
     * Returns all sourcefolder from <code>javaProject</code> and filters
     * sourcefolder which are already configured as test source folder for the
     * underlying project.
     */
    private List<IPackageFragmentRoot> getRelevantSourceFolderForProject(IJavaProject javaProject)
    {
        List<IPackageFragmentRoot> resultList = new ArrayList<IPackageFragmentRoot>();

        if(javaProject == null)
            return resultList;

        try
        {
            for (IPackageFragmentRoot fragmentRoot : javaProject.getPackageFragmentRoots())
            {
                if(! fragmentRoot.isArchive() && ! selectedUnitSourceFolderFromPreferences.contains(fragmentRoot))
                    resultList.add(fragmentRoot);
            }
        }
        catch (CoreException e)
        {
            LogHandler.getInstance().handleExceptionLog(e);
        }
        return resultList;
    }
}
