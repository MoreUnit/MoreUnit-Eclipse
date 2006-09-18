package org.moreunit.util;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.moreunit.log.LogHandler;
import org.moreunit.preferences.Preferences;


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
	private Set<IType>				matches = new LinkedHashSet<IType>();;
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
		if(source == null)
			return;
		
		matches = new LinkedHashSet<IType>();
		String[] prefixes = preferences.getPrefixes();
		for (int i = 0; i < prefixes.length; i++) {
			matches.addAll(BaseTools.searchFor(getSearchTerm(source, prefixes[i], true), compilationUnit));
		}
		String[] suffixes = preferences.getSuffixes();
		for (int i = 0; i < suffixes.length; i++) {
			matches.addAll(BaseTools.searchFor(getSearchTerm(source, suffixes[i], false), compilationUnit));
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
		return prefixMatch ? qualifier + type.getTypeQualifiedName() : type.getTypeQualifiedName() + qualifier;
	}
	

}

// $Log: not supported by cvs2svn $
// Revision 1.2  2006/08/29 19:35:40  gianasista
// Bugfix to avoid NPE
//
// Revision 1.1.1.1  2006/08/13 14:31:16  gianasista
// initial
//
// Revision 1.1  2006/06/22 20:22:28  gianasista
// package rename
//
// Revision 1.1  2006/06/19 20:08:48  gianasista
// CVS Refactoring
//
// Revision 1.4  2006/06/03 16:50:26  gianasista
// findPotentialTargets wasn't null-safe
//
// Revision 1.3  2006/05/14 22:27:10  channingwalton
// made use of generics to remove some warnings
//
// Revision 1.2  2006/05/14 19:10:58  gianasista
// Smaller enhancements
//
// Revision 1.1  2006/05/13 18:30:24  gianasista
// Searching for testcases for a class (based on preferences) + Tests
//
