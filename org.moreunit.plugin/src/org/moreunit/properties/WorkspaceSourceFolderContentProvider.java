package org.moreunit.properties;

import java.util.ArrayList;
import java.util.Comparator;
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

    private final List<IPackageFragmentRoot> selectedUnitSourceFolderFromPreferences = new ArrayList<>();

    public WorkspaceSourceFolderContentProvider(List<SourceFolderMapping> selectedUnitSourceFolderFromPreferences)
    {
        for (final SourceFolderMapping mapping : selectedUnitSourceFolderFromPreferences)
        {
            this.selectedUnitSourceFolderFromPreferences.add(mapping.getTestFolder());
        }
    }

    @Override
    public Object[] getElements(Object inputElement)
    {
        if(inputElement instanceof IJavaProject)
            return getChildren(inputElement);

        return getRelevantJavaProjectsInWorkspace().toArray();
    }

    @Override
    public void dispose()
    {

    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
    }

    @Override
    public Object[] getChildren(Object parentElement)
    {
        if(parentElement instanceof final IJavaProject project)
            return getRelevantSourceFolderForProject(project).toArray();

        return new Object[0];
    }

    @Override
    public Object getParent(Object element)
    {
        if(element instanceof final IPackageFragmentRoot root)
            return root.getJavaProject();

        return null;
    }

    @Override
    public boolean hasChildren(Object element)
    {
        if(element instanceof final IJavaProject project)
            return ! getRelevantSourceFolderForProject(project).isEmpty();

        return false;
    }

    /**
     * Returns all java projects from workspace which have source folder not
     * selected as test source folder for the underlying project
     */
    private List<IJavaProject> getRelevantJavaProjectsInWorkspace()
    {
        final List<IJavaProject> allJavaProjectsInWorkspace = new ArrayList<>();

        final IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
        for (final IProject aProject : projects)
        {
            try
            {
                if(aProject.isAccessible() && aProject.hasNature(JavaCore.NATURE_ID))
                {
                    final IJavaProject javaProject = JavaCore.create(aProject);
                    if(hasChildren(javaProject))
                        allJavaProjectsInWorkspace.add(JavaCore.create(aProject));
                }
            }
            catch (final CoreException e)
            {
                LogHandler.getInstance().handleExceptionLog(e);
            }
        }

        return allJavaProjectsInWorkspace.stream().sorted(Comparator.comparing((IJavaProject project) -> project.getElementName(), String.CASE_INSENSITIVE_ORDER)).toList();
    }

    /**
     * Returns all sourcefolder from <code>javaProject</code> and filters
     * sourcefolder which are already configured as test source folder for the
     * underlying project.
     */
    private List<IPackageFragmentRoot> getRelevantSourceFolderForProject(IJavaProject javaProject)
    {
        final List<IPackageFragmentRoot> resultList = new ArrayList<>();

        if(javaProject == null)
            return resultList;

        try
        {
            for (final IPackageFragmentRoot fragmentRoot : javaProject.getPackageFragmentRoots())
            {
                if(! fragmentRoot.isArchive() && ! selectedUnitSourceFolderFromPreferences.contains(fragmentRoot))
                    resultList.add(fragmentRoot);
            }
        }
        catch (final CoreException e)
        {
            LogHandler.getInstance().handleExceptionLog(e);
        }
        return resultList;
    }
}
