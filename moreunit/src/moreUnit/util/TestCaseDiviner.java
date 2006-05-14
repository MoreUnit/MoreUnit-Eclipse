package moreUnit.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import moreUnit.log.LogHandler;
import moreUnit.preferences.Preferences;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
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


/**
 * Encalpsulates the implementation to find the testcases from a given
 * compilationUnit.
 * 
 * @author giana
 *
 * 13.05.2006 12:49:12
 */
public class TestCaseDiviner {
	
	private ICompilationUnit 		compilationUnit;
	private Set<IType>				matches;
	private IType 					source;
	
	private Preferences				preferences;
	
	public TestCaseDiviner(ICompilationUnit compilationUnit, Preferences preferences) {
		this.compilationUnit = compilationUnit;
		this.source = getSource();
		this.preferences = preferences;
		try {
			findPotentialTargets();
		} catch (CoreException exc) {
			LogHandler.getInstance().handleExceptionLog(exc);
		}
	}
	
	public Set<IType> getMatches() {
		return matches;
	}
	
	private void findPotentialTargets() throws CoreException {
		matches = new LinkedHashSet<IType>();
		String[] prefixes = preferences.getPrefixes();
		for (int i = 0; i < prefixes.length; i++) {
			matches.addAll(searchFor(getSearchTerm(source, prefixes[i], true)));
		}
		String[] suffixes = preferences.getSuffixes();
		for (int i = 0; i < suffixes.length; i++) {
			matches.addAll(searchFor(getSearchTerm(source, suffixes[i], false)));
		}
	}
	
	private IType getSource() {
		try {
			IType[] allTypes = compilationUnit.getAllTypes();
			if (allTypes.length > 0 && allTypes[0].isClass()) {
				return allTypes[0];
			}
		} catch (JavaModelException exc) {
			LogHandler.getInstance().handleExceptionLog(exc);
		}
		
		return null;
	}
	
	private String getSearchTerm(IType type, String qualifier, boolean prefixMatch) {
		String searchTerm = prefixMatch ? qualifier + type.getTypeQualifiedName() : type.getTypeQualifiedName() + qualifier;
		return searchTerm;
	}
	
	private Set<IType> searchFor(String typeName) throws JavaModelException, CoreException {
		SearchPattern pattern = SearchPattern.createPattern(typeName, IJavaSearchConstants.TYPE, IJavaSearchConstants.DECLARATIONS, SearchPattern.R_EXACT_MATCH);
		IJavaSearchScope scope = getSearchScope();
		SearchEngine searchEngine = new SearchEngine();
		final Set<IType> matches = new TreeSet(new ITypeNameComparator());
		SearchRequestor requestor = new SearchRequestor() {
			public void acceptSearchMatch(SearchMatch match) {
				matches.add((IType)match.getElement());
			}
		};
		searchEngine.search(pattern, new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() }, scope, requestor, null);
		return matches;
	}
	
	private IJavaSearchScope getSearchScope() throws JavaModelException {
		IJavaProject javaProject = compilationUnit.getJavaProject();
		IClasspathEntry[] entries = javaProject.getResolvedClasspath(true);
		ArrayList sourceFolders = new ArrayList();
		for (int i = 0; i < entries.length; i++) {
			IClasspathEntry entry = entries[i];
			if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
				sourceFolders.addAll(Arrays.asList(javaProject.findPackageFragmentRoots(entry)));
			}
		}
		return SearchEngine.createJavaSearchScope((IPackageFragmentRoot[]) sourceFolders.toArray(new IPackageFragmentRoot[sourceFolders.size()]));
	}

}

// $Log: not supported by cvs2svn $
// Revision 1.1  2006/05/13 18:30:24  gianasista
// Searching for testcases for a class (based on preferences) + Tests
//
