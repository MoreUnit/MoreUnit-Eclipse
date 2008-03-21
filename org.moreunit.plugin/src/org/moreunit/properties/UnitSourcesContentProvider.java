package org.moreunit.properties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.moreunit.elements.SourceFolderMapping;
import org.moreunit.preferences.Preferences;

/**
 * @author vera
 *
 * 02.03.2008 10:04:43
 */
public class UnitSourcesContentProvider implements ITreeContentProvider {
	
	private IJavaProject javaProjectInput;
	private List<SourceFolderMapping> listOfSourceFolderMappings;
	
	private Map<IPackageFragmentRoot, SourceFolderMapping> sourceFolderToMappingMap = new HashMap<IPackageFragmentRoot, SourceFolderMapping>();
	
	public UnitSourcesContentProvider(IJavaProject javaProject) {
		this.javaProjectInput = javaProject;
		listOfSourceFolderMappings = getListOfSourceFoldersFromPreferences();
	}

	public Object[] getElements(Object inputElement) {
		return listOfSourceFolderMappings.toArray();
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.javaProjectInput = (IJavaProject) newInput;
		listOfSourceFolderMappings = getListOfSourceFoldersFromPreferences();
	}
	
	private List<SourceFolderMapping> getListOfSourceFoldersFromPreferences() {
		return Preferences.getInstance().getSourceMappingList(javaProjectInput);
	}

	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof SourceFolderMapping) {
			return new Object[] { ((SourceFolderMapping) parentElement).getSourceFolder() };
		}
		return getListOfSourceFoldersFromPreferences().toArray();
	}

	public Object getParent(Object element) {
		if(element instanceof IPackageFragmentRoot)
			return sourceFolderToMappingMap.get(element);
		
		return null;
	}

	public boolean hasChildren(Object element) {
		return element instanceof SourceFolderMapping;
	}
	
	public void add(SourceFolderMapping mapping) {
		listOfSourceFolderMappings.add(mapping);
		
		sourceFolderToMappingMap.put(mapping.getSourceFolder(), mapping);
	}
	
	public void add(List<SourceFolderMapping> listOfSourceFolder) {
		listOfSourceFolderMappings.addAll(listOfSourceFolder);
		
		for(SourceFolderMapping mapping : listOfSourceFolder) {
			sourceFolderToMappingMap.put(mapping.getSourceFolder(), mapping);
		}
	}
	
	public boolean remove(SourceFolderMapping sourceFolder) {
		if(sourceFolderToMappingMap.containsValue(sourceFolder)) {
			for(IPackageFragmentRoot folderKey : sourceFolderToMappingMap.keySet()) {
				if(sourceFolderToMappingMap.get(folderKey).equals(sourceFolder))
					sourceFolderToMappingMap.remove(folderKey);
			}
		}
		
		return listOfSourceFolderMappings.remove(sourceFolder);
	}

	public List<SourceFolderMapping> getListOfUnitSourceFolder() {
		return listOfSourceFolderMappings;
	}
}
