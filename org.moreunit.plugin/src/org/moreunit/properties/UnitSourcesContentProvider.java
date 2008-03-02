package org.moreunit.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.moreunit.log.LogHandler;

/**
 * @author vera
 *
 * 02.03.2008 10:04:43
 */
public class UnitSourcesContentProvider implements ITreeContentProvider {
	
	private IJavaProject javaProjectInput;
	private List<IPackageFragmentRoot> listOfUnitSourceFolder;
	
	public UnitSourcesContentProvider(IJavaProject javaProject) {
		this.javaProjectInput = javaProject;
		listOfUnitSourceFolder = getListOfSourceFoldersFromPreferences();
	}

	public Object[] getElements(Object inputElement) {
		return listOfUnitSourceFolder.toArray();
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.javaProjectInput = (IJavaProject) newInput;
		listOfUnitSourceFolder = getListOfSourceFoldersFromPreferences();
	}
	
	// TODO get Sourcefolder from preferences
	private List<IPackageFragmentRoot> getListOfSourceFoldersFromPreferences() {
		List<IPackageFragmentRoot> resultList = new ArrayList<IPackageFragmentRoot>();
		
		if(javaProjectInput == null)
			return resultList;
		
		try {
			for (IPackageFragmentRoot fragmentRoot : javaProjectInput.getPackageFragmentRoots()) {
				if(!fragmentRoot.isArchive())
					resultList.add(fragmentRoot);
			}
		} catch (CoreException e) {
			LogHandler.getInstance().handleExceptionLog(e);
		}
		return resultList;
	}

	public Object[] getChildren(Object parentElement) {
		return getListOfSourceFoldersFromPreferences().toArray();
	}

	public Object getParent(Object element) {
		return null;
	}

	public boolean hasChildren(Object element) {
		return false;
	}
	
	public void add(IPackageFragmentRoot soureFolder) {
		listOfUnitSourceFolder.add(soureFolder);
	}
	
	public void add(List<IPackageFragmentRoot> listOfSourceFolder) {
		listOfUnitSourceFolder.addAll(listOfSourceFolder);
	}
	
	public boolean remove(IPackageFragmentRoot sourceFolder) {
		return listOfUnitSourceFolder.remove(sourceFolder);
	}

	public List<IPackageFragmentRoot> getListOfUnitSourceFolder() {
		return listOfUnitSourceFolder;
	}
}
