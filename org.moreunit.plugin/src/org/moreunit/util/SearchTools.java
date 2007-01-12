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
import org.moreunit.properties.ProjectProperties;

/**
 * @author vera
 */
public class SearchTools {

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

	private static IJavaSearchScope getSearchScope(IJavaElement compilationUnit) throws JavaModelException {
		IJavaProject javaProject = compilationUnit.getJavaProject();
		ArrayList<IPackageFragmentRoot> sourceFolders = SearchTools.getPackageFragmentsToSearch(javaProject);
		List<IJavaProject> testProjects = ProjectProperties.instance().getJumpTargets(javaProject);
		for (IJavaProject project : testProjects) {
			sourceFolders.addAll(SearchTools.getPackageFragmentsToSearch(project));
		}
		return SearchEngine.createJavaSearchScope(sourceFolders.toArray(new IPackageFragmentRoot[sourceFolders.size()]));
	}

	private static ArrayList<IPackageFragmentRoot> getPackageFragmentsToSearch(IJavaProject javaProject) throws JavaModelException {
		IClasspathEntry[] entries = javaProject.getResolvedClasspath(true);
		ArrayList<IPackageFragmentRoot> sourceFolders = new ArrayList<IPackageFragmentRoot>();
		for (int i = 0; i < entries.length; i++) {
			IClasspathEntry entry = entries[i];
			if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
				sourceFolders.addAll(Arrays.asList(javaProject.findPackageFragmentRoots(entry)));
			}
		}
		return sourceFolders;
	}

}
