package org.moreunit.elements;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.IEditorPart;
import org.moreunit.log.LogHandler;
import org.moreunit.preferences.Preferences;
import org.moreunit.ui.MarkerUpdateRunnable;
import org.moreunit.util.BaseTools;
import org.moreunit.util.SearchTools;

/**
 * ClassTypeFacade offers easy access to a simple java file within eclipse. The
 * file represented by this instance is not a testcase.
 * 
 * @author vera
 * 
 * 23.05.2006 20:29:57
 */
public class TestCaseTypeFacade extends TypeFacade {

	public TestCaseTypeFacade(ICompilationUnit compilationUnit) {
		super(compilationUnit);
	}

	public TestCaseTypeFacade(IEditorPart editorPart) {
		super(editorPart);
	}

	public TestCaseTypeFacade(IFile file) {
		super(file);
	}

	public IType getCorrespondingClassUnderTest() {
		Preferences preferences = Preferences.getInstance();
		List<String> testedClasses =
				BaseTools.getTestedClass(
						getType().getFullyQualifiedName(),
						preferences.getPrefixes(this.javaProjectFacade.getJavaProject()),
						preferences.getSuffixes(this.javaProjectFacade.getJavaProject()),
						preferences.getTestPackagePrefix(this.javaProjectFacade.getJavaProject()),
						preferences.getTestPackageSuffix(this.javaProjectFacade.getJavaProject()));
		if (testedClasses.isEmpty()) {
			return null;
		}

		try {
			List<String> typeNames = BaseTools.getListOfUnqualifiedTypeNames(testedClasses);
			Set<IType> searchResults = new HashSet<IType>();
			for (String typeName : typeNames) {
				Set<IType> searchFor = SearchTools.searchFor(typeName, compilationUnit);
				if (searchFor != null && searchFor.size() > 0) {
					searchResults.addAll(searchFor);
				}
			}

			return searchResults.size() > 0 ? searchResults.iterator().next() : null;
		} catch (Exception exc) {
			LogHandler.getInstance().handleExceptionLog(exc);
		}

		return null;
	}

	public IMethod getCorrespondingTestedMethod(IMethod testMethod, IType classUnderTest) {
		try {
			String testedMethodName = BaseTools.getTestedMethod(testMethod.getElementName());
			if (testedMethodName != null) {
				IMethod[] foundTestMethods = classUnderTest.getMethods();
				for (IMethod method : foundTestMethods) {
					if (testedMethodName.startsWith(method.getElementName()) && method.exists()) {
						return method;
					}
				}
			}
		} catch (JavaModelException exc) {
			LogHandler.getInstance().handleExceptionLog(exc);
		}

		return null;
	}

	/**
	 * Creates a testmethod for the method that should be tested.
	 * 
	 * @param methodToTest
	 *            The method that should be tested.
	 * @return <code>true</code> if the method was successfully created,
	 *         <code>false</code> if the method already existed or the method
	 *         creation threw an exception.
	 */
	/*
	 * public boolean createTestMethodForMethod(IMethod methodToTest) { try {
	 * String methodName = methodToTest.getElementName(); methodName =
	 * BaseTools.firstCharToUpperCase(methodName);
	 * 
	 * String testMethodName = MagicNumbers.TEST_METHOD_PRAEFIX + methodName; if
	 * (doesMethodExist(testMethodName)) return false;
	 * 
	 * compilationUnit.findPrimaryType().createMethod(getTestMethodString(testMethodName),
	 * null, true, null);
	 * 
	 * if (Preferences.instance().shouldUseJunit4Type()) {
	 * compilationUnit.createImport("org.junit.Test", null, null); } return
	 * true; } catch (JavaModelException exc) {
	 * LogHandler.getInstance().handleExceptionLog(exc); }
	 * 
	 * return false; }
	 */

	/**
	 * Creates another testmethod for the given method aTestMethod
	 * 
	 * @param aTestMethod
	 * @return
	 */
	/*
	 * public IMethod createAnotherTestMethod(IMethod aTestMethod) { try {
	 * String testedMethodName =
	 * BaseTools.getTestedMethod(aTestMethod.getElementName()); IMethod
	 * testedMethod =
	 * BaseTools.getFirstMethodWithSameNamePrefix(getCorrespondingClassUnderTest().getMethods(),
	 * testedMethodName); if(testedMethod != null) { String testMethodName =
	 * MagicNumbers.TEST_METHOD_PRAEFIX +
	 * BaseTools.firstCharToUpperCase(testedMethod.getElementName()); if
	 * (doesMethodExist(testMethodName)) testMethodName =
	 * testMethodName.concat(MagicNumbers.SUFFIX_NAME);
	 * 
	 * IMethod newTestMethod =
	 * compilationUnit.findPrimaryType().createMethod(getTestMethodString(testMethodName),
	 * null, true, null);
	 * 
	 * return newTestMethod; } } catch (JavaModelException e) {
	 * LogHandler.getInstance().handleExceptionLog(e); }
	 * 
	 * return null; }
	 */

	/*
	 * private String getTestMethodString(String testMethodName) { if
	 * (Preferences.instance().shouldUseJunit4Type()) { StringBuffer result =
	 * new StringBuffer(); result.append("@Test").append(MagicNumbers.NEWLINE);
	 * result.append("public void ").append(testMethodName).append("()
	 * {").append(MagicNumbers.NEWLINE).append("}");
	 * 
	 * return result.toString(); } else { return "public void " + testMethodName +
	 * "() {" + MagicNumbers.NEWLINE + "}"; } }
	 */

	public void createMarkerForTestedClass() throws CoreException {
		if (!compilationUnit.exists()) {
			return;
		}
		IResource resource = this.compilationUnit.getUnderlyingResource();
		if (resource == null) {
			return;
		}

		if (!Flags.isAbstract(getType().getFlags())) {

			IType testedClass = getCorrespondingClassUnderTest();
			if (testedClass == null) {
				return;
			}

			new MarkerUpdateRunnable(testedClass, getType()).schedule();
		}
	}
}

//$Log: not supported by cvs2svn $
//Revision 1.14  2008/01/29 07:57:30  channingwalton
//under some circumstances after a rename, the compilation unit does not seem to exist. this fix checks for that and prevents a java model exception being thrown.
//
//Revision 1.13  2007/11/19 20:51:57  gianasista
//Patch from Bjoern: project specific settings
//
//Revision 1.11  2007/08/12 17:09:32  gianasista
//Refactoring: Test method creation
//
//Revision 1.10  2007/03/02 22:14:07  channingwalton
//[ 1667386 ] Jump to test can miss some testcases
//
//Fixed
//
//Revision 1.9  2007/01/25 08:34:25  hannosti
//Some comments. Removed dead code.
//
//Revision 1.8  2007/01/24 20:11:50  gianasista
//Bugfix: flexible testcase matching
//
//Revision 1.7  2007/01/12 21:55:54  gianasista
//Better matching for testcases [1575497]
//
//Revision 1.6  2006/12/22 19:03:50  gianasista
//changed textselection after creation of another testmethod
//
//Revision 1.5  2006/11/25 14:58:56  gianasista
//Create second testmethod
//
//Revision 1.4  2006/10/08 17:26:27  gianasista
//Suffix preference
//
//Revision 1.3  2006/09/18 20:00:10  channingwalton
//the CVS substitions broke with my last check in because I put newlines in them
//
//Revision 1.2  2006/09/18 19:56:07  channingwalton
//Fixed bug [ 1537839 ] moreunit cannot find test class if it is in wrong package. Also found a classcast exception in UnitDecorator whicj I've guarded for.Fixed the Class wizard icon
//
//