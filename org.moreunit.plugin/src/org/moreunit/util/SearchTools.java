package org.moreunit.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.moreunit.SourceFolderContext;
import org.moreunit.elements.SourceFolderMapping;
import org.moreunit.preferences.Preferences;

/**
 * @author vera
 */
public class SearchTools {

	/*
	public static Set<IType> searchFor(String typeName, IJavaElement sourceCompilationUnit) throws JavaModelException, CoreException {
		SearchPattern pattern = SearchPattern.createPattern(typeName, IJavaSearchConstants.TYPE, IJavaSearchConstants.DECLARATIONS, SearchPattern.R_EXACT_MATCH);
		IJavaSearchScope scope = SearchTools.getSearchScope(sourceCompilationUnit);
		SearchEngine searchEngine = new SearchEngine();
		final Set<IType> matches = new TreeSet<IType>(new TypeComparator());
		SearchRequestor requestor = new SearchRequestor() {
			public void acceptSearchMatch(SearchMatch match) {
				matches.add((IType)match.getElement());
			}
		};
		searchEngine.search(pattern, new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() }, scope, requestor, null);
		return matches;
	}
	*/
	
	public static Set<IType> searchFor(String typeName, IJavaElement sourceCompilationUnit, IJavaSearchScope searchScope) throws JavaModelException, CoreException {
		SearchPattern pattern = SearchPattern.createPattern(typeName, IJavaSearchConstants.TYPE, IJavaSearchConstants.DECLARATIONS, SearchPattern.R_EXACT_MATCH);
		IJavaSearchScope scope = searchScope;
		SearchEngine searchEngine = new SearchEngine();
		final Set<IType> matches = new TreeSet<IType>(new TypeComparator());
		SearchRequestor requestor = new SearchRequestor() {
			public void acceptSearchMatch(SearchMatch match) {
				matches.add((IType)match.getElement());
			}
		};
		searchEngine.search(pattern, new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() }, scope, requestor, null);
		return matches;
	}

	/*
	private static IJavaSearchScope getSearchScope(IJavaElement compilationUnit) throws JavaModelException {
		IJavaProject javaProject = compilationUnit.getJavaProject();
		List<IPackageFragmentRoot> unitSourceFolder = Preferences.getInstance().getTestSourceFolder(javaProject);
		if(unitSourceFolder.size() > 0) {
			return SearchEngine.createJavaSearchScope(unitSourceFolder.toArray(new IPackageFragmentRoot[unitSourceFolder.size()]));
		}
		
		ArrayList<IPackageFragmentRoot> sourceFolders = SearchTools.getPackageFragmentsToSearch(javaProject);
		return SearchEngine.createJavaSearchScope(sourceFolders.toArray(new IPackageFragmentRoot[sourceFolders.size()]));
	}
	*/
	
//	public static IJavaSearchScope getSearchScope(IPackageFragmentRoot sourceFolder) throws JavaModelException {
//		List<SourceFolderMapping> mappingList = Preferences.getInstance().getSourceMappingList(sourceFolder.getJavaProject());
//		if(mappingList.size() > 0) {
//			IPackageFragmentRoot[] folderArray = new IPackageFragmentRoot[mappingList.size()];
//			for(int i=0; i<folderArray.length; i++)
//				folderArray[i] = mappingList.get(i).getTestFolder();
//			return SearchEngine.createJavaSearchScope(folderArray);
//		}
//		
//		ArrayList<IPackageFragmentRoot> sourceFolders = SearchTools.getPackageFragmentsToSearch(sourceFolder.getJavaProject());
//		return SearchEngine.createJavaSearchScope(sourceFolders.toArray(new IPackageFragmentRoot[sourceFolders.size()]));
		
		// TODO inline this method
//		List<IPackageFragmentRoot> sourceFolderToSearch = SourceFolderContext.getInstance().getSourceFolderToSearch(sourceFolder);
//		return SearchEngine.createJavaSearchScope(sourceFolderToSearch.toArray(new IPackageFragmentRoot[sourceFolderToSearch.size()]));
//	}

//	private static ArrayList<IPackageFragmentRoot> getPackageFragmentsToSearch(IJavaProject javaProject) throws JavaModelException {
//		IClasspathEntry[] entries = javaProject.getResolvedClasspath(true);
//		ArrayList<IPackageFragmentRoot> sourceFolders = new ArrayList<IPackageFragmentRoot>();
//		for (int i = 0; i < entries.length; i++) {
//			IClasspathEntry entry = entries[i];
//			if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
//				sourceFolders.addAll(Arrays.asList(javaProject.findPackageFragmentRoots(entry)));
//			}
//		}
//		return sourceFolders;
//	}

}
