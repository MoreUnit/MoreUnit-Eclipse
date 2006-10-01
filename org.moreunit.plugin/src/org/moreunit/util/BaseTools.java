/*
 * Created on 20.02.2005 20:03:01
 * Created by Vera
 */
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
import org.moreunit.log.LogHandler;
import org.moreunit.properties.ProjectProperties;


public class BaseTools {
	
	/**
	 * This method returns the name of the testmethod for a given
	 * <code>methodName</code>. Only a prefix is used.<br>
	 * Example:<br>
	 * foo: testFoo
	 * 
	 * @param methodName
	 * @return
	 */
	public static String getTestmethodNameFromMethodName(String methodName) {
		if(methodName == null || methodName.length() == 0) {
			LogHandler.getInstance().handleWarnLog("Methodname is null or has length of 0");
			return MagicNumbers.EMPTY_STRING;
		}
		
		String firstChar = String.valueOf(methodName.charAt(0));
		methodName = methodName.replaceFirst(firstChar, firstChar.toUpperCase());
		
		String testMethodName = MagicNumbers.TEST_METHOD_PRAEFIX+methodName;
		
		return testMethodName;
	}
	
	/**
	 * Example:<br>
	 * methodNameBeforeRename: countMembers<br>
	 * methodNameAfterRename: countAllMembers<br>
	 * testMethodName: testCountMemberSpecialCase<br>
	 * returns testCountAllMemberSpecialCase
	 * 
	 * @param methodNameBeforeRename
	 * @param methodNameAfterRename
	 * @param testMethodName
	 * @return Name of testmethod performed with the same rename as the tested method
	 */
	public static String getTestMethodNameAfterRename(String methodNameBeforeRename, String methodNameAfterRename, String testMethodName) {
		String[] prefixAndSuffix = testMethodName.split(getStringWithFirstCharToUpperCase(methodNameBeforeRename));
		
		if(prefixAndSuffix.length == 0)
			return null;
		
		String prefix = prefixAndSuffix[0];
		String suffix = MagicNumbers.EMPTY_STRING;
		
		if(prefixAndSuffix.length > 1)
			suffix = prefixAndSuffix[1];
		
		return prefix+getStringWithFirstCharToUpperCase(methodNameAfterRename)+suffix;
	}
	
	/**
	 * Returns the same string and ensures that the first char of the {@link String}
	 * is an uppercase letter.<br>
	 * Example:<br>
	 * test: Test
	 * 
	 * @param string
	 * @return
	 */
	public static String getStringWithFirstCharToUpperCase(String string) {
		if(string == null || string.length() == 0)
			return string;
		
		char firstChar = string.charAt(0);
		StringBuffer result = new StringBuffer();
		result.append(Character.toUpperCase(firstChar));
		result.append(string.substring(1));
		
		return result.toString();
	}

	/**
	 * This method tries to find out the name of the class which is under test
	 * by the name of a given testcase.
	 * 
	 * @param testCaseClass name of the testcase
	 * @param prefixes		possible prefixes of the testcase
	 * @param suffixes		possible suffixes of the testcase
	 * @param packagePrefix 
	 * @return name of the class under test
	 */
	public static String getTestedClass(String testCaseClass, String[] prefixes, String[] suffixes, String packagePrefix) {
		if(testCaseClass == null || testCaseClass.length() <= 1)
			return null;
		
		if (packagePrefix != null && packagePrefix.length() > 0) {
			testCaseClass = testCaseClass.replaceFirst(packagePrefix + "\\.", "");
		}
		
		if(suffixes != null) {
			for(String suffix: suffixes) {
				if(testCaseClass.endsWith(suffix))
					return testCaseClass.substring(0, testCaseClass.length()-suffix.length());
			}
		}
		
		if(prefixes != null) {
			for(String prefix: prefixes) {
				if(testCaseClass.startsWith(prefix))
					return testCaseClass.replaceFirst(prefix, MagicNumbers.EMPTY_STRING);
			}
		}
		
		return null;
	}
	
	/**
	 * This method identifies the name of the tested method for a given testmethod.<br>
	 * Example:<br>
	 * testFoo: foo
	 * testFooSuffix: fooSuffix
	 * 
	 * @param testMethodName name of the testmethod
	 * @return name of the method which is tested
	 */
	public static String getTestedMethod(String testMethodName) {
		if(testMethodName == null || !testMethodName.startsWith(MagicNumbers.TEST_METHOD_PRAEFIX) || testMethodName.length() <= 4)
			return null;
		
		char erstesZeichen = testMethodName.charAt(4);
		StringBuffer result = new StringBuffer();
		result.append(Character.toLowerCase(erstesZeichen));
		result.append(testMethodName.substring(5));
		return result.toString();
	}
	
	public static Set<IType> searchFor(String typeName, IJavaElement sourceCompilationUnit) throws JavaModelException, CoreException {
		SearchPattern pattern = SearchPattern.createPattern(typeName, IJavaSearchConstants.TYPE, IJavaSearchConstants.DECLARATIONS, SearchPattern.R_EXACT_MATCH);
		IJavaSearchScope scope = getSearchScope(sourceCompilationUnit);
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
		ArrayList<IPackageFragmentRoot> sourceFolders = getPackageFragmentsToSearch(javaProject);
		List<IJavaProject> testProjects = ProjectProperties.instance().getJumpTargets(javaProject);
		for (IJavaProject project : testProjects) {
			sourceFolders.addAll(getPackageFragmentsToSearch(project));
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

// $Log: not supported by cvs2svn $
// Revision 1.4  2006/09/18 20:00:05  channingwalton
// the CVS substitions broke with my last check in because I put newlines in them
//
// Revision 1.3  2006/09/18 19:56:03  channingwalton
// Fixed bug [ 1537839 ] moreunit cannot find test class if it is in wrong package.Also found a classcast exception in UnitDecorator whicj I've guarded for.Fixed the Class wizard icon
//
// Revision 1.2  2006/08/28 19:33:08  gianasista
// Bugfix in getTestedMethod
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
// Revision 1.10  2006/06/10 20:32:44  gianasista
// Bugfix: handle package prefix as empty string
//
// Revision 1.9  2006/06/10 09:42:40  channingwalton
// fix for jumping from a test case to its class under test when the test packages have a prefix
//
// Revision 1.8  2006/05/20 16:13:20  gianasista
// Integration of switchunit preferences
//
// Revision 1.7  2006/05/12 17:54:40  gianasista
// added comments
//
// Revision 1.6  2006/04/14 17:11:11  gianasista
// Suffix for testcasename ist configurable (+Tests)
//
// Revision 1.5  2006/02/19 21:48:29  gianasista
// New method
//
// Revision 1.4  2006/01/31 19:35:35  gianasista
// Methods are now null-save and have tests.
//
// Revision 1.3  2006/01/19 21:38:32  gianasista
// Added CVS-commit-logging to all java-files
//