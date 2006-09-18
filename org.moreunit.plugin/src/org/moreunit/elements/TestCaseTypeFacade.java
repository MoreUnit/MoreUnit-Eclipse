package org.moreunit.elements;

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
import org.eclipse.ui.PlatformUI;
import org.moreunit.log.LogHandler;
import org.moreunit.preferences.Preferences;
import org.moreunit.ui.MarkerUpdateRunnable;
import org.moreunit.util.BaseTools;
import org.moreunit.util.MagicNumbers;

/**
 * ClassTypeFacade offers easy access to a simple java file within eclipse. The file represented by this instance is not
 * a testcase.
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
		String testedClassString = BaseTools.getTestedClass(getType().getFullyQualifiedName(), Preferences.instance().getPrefixes(), Preferences.instance().getSuffixes(), Preferences.instance().getTestPackagePrefix());
		if (testedClassString == null)
			return null;

		try {
			String typeName = getUnqualifiedTypeName(testedClassString);
			Set<IType> searchResults = BaseTools.searchFor(typeName, compilationUnit);
			return searchResults.size() > 0 ? searchResults.iterator().next() : null;
		} catch (Exception exc) {
			LogHandler.getInstance().handleExceptionLog(exc);
		}

		return null;
	}

	private String getUnqualifiedTypeName(String testedClassString) {
		return testedClassString.lastIndexOf('.') > 0 ? testedClassString.substring(testedClassString.lastIndexOf('.') + 1) : testedClassString;
	}

	public boolean createTestMethodForMethod(IMethod methodToTest) {
		try {
			String methodName = methodToTest.getElementName();
			String erstesZeichen = String.valueOf(methodName.charAt(0));
			methodName = methodName.replaceFirst(erstesZeichen, erstesZeichen.toUpperCase());

			String testMethodName = MagicNumbers.TEST_METHOD_PRAEFIX + methodName;
			if (doesMethodExist(testMethodName))
				return false;

			compilationUnit.findPrimaryType().createMethod(getTestMethodString(testMethodName), null, true, null);

			if (Preferences.instance().shouldUseJunit4Type()) {
				compilationUnit.createImport("org.junit.Test", null, null);
			}
			return true;
		} catch (JavaModelException exc) {
			LogHandler.getInstance().handleExceptionLog(exc);
		}

		return false;
	}

	private String getTestMethodString(String testMethodName) {
		if (Preferences.instance().shouldUseJunit4Type()) {
			StringBuffer result = new StringBuffer();
			result.append("@Test").append(MagicNumbers.NEWLINE);
			result.append("public void ").append(testMethodName).append("() {").append(MagicNumbers.NEWLINE).append("}");

			return result.toString();
		} else {
			return "public void " + testMethodName + "() {" + MagicNumbers.NEWLINE + "}";
		}
	}

	public void createMarkerForTestedClass() throws CoreException {
		IResource resource = compilationUnit.getUnderlyingResource();
		if (resource == null)
			return;

		if (!Flags.isAbstract(getType().getFlags())) {

			IType testedClass = getCorrespondingClassUnderTest();
			if (testedClass == null)
				return;

			PlatformUI.getWorkbench().getDisplay().asyncExec(new MarkerUpdateRunnable(testedClass, getType()));
		}
	}
}

//$Log: not supported by cvs2svn $
//Revision 1.2  2006/09/18 19:56:07  channingwalton
//Fixed bug [ 1537839 ] moreunit cannot find test class if it is in wrong package. Also found a classcast exception in UnitDecorator whicj I've guarded for.Fixed the Class wizard icon
//
//