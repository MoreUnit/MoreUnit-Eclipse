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
import org.moreunit.log.LogHandler;

/**
 * @author vera
 *
 * 02.03.2008 19:28:41
 */
public class WorkspaceSourceFolderContentProvider implements ITreeContentProvider {

	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof IJavaProject)
			return getChildren(inputElement);
		
		return getJavaProjectsInWorkspace().toArray();
	}

	public void dispose() {

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof IJavaProject)
			return getSourceFolderForProject((IJavaProject) parentElement).toArray();
		
		return new Object[0];
	}

	public Object getParent(Object element) {
		if(element instanceof IPackageFragmentRoot)
			return ((IPackageFragmentRoot)element).getJavaProject();
		
		return null;
	}

	public boolean hasChildren(Object element) {
		if(element instanceof IJavaProject)
			return !getSourceFolderForProject((IJavaProject)element).isEmpty();
			
		return false;
	}
	
	private List<IJavaProject> getJavaProjectsInWorkspace() {
		List<IJavaProject> allJavaProjectsInWorkspace = new ArrayList<IJavaProject>();
		
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for(IProject aProject : projects) {
			try {
				if(aProject.hasNature(JavaCore.NATURE_ID))
					allJavaProjectsInWorkspace.add(JavaCore.create(aProject));
			} catch (CoreException e) {
				LogHandler.getInstance().handleExceptionLog(e);
			}
		}
		
		return allJavaProjectsInWorkspace;
	}
	
	private List<IPackageFragmentRoot> getSourceFolderForProject(IJavaProject javaProject) {
		List<IPackageFragmentRoot> resultList = new ArrayList<IPackageFragmentRoot>();
		
		if(javaProject == null)
			return resultList;
		
		try {
			for (IPackageFragmentRoot fragmentRoot : javaProject.getPackageFragmentRoots()) {
				if(!fragmentRoot.isArchive())
					resultList.add(fragmentRoot);
			}
		} catch (CoreException e) {
			LogHandler.getInstance().handleExceptionLog(e);
		}
		return resultList;
	}
}