package org.moreunit.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.IEditorPart;
import org.moreunit.log.LogHandler;
import org.moreunit.preferences.Preferences;
import org.moreunit.ui.TypeChoiceDialog;
import org.moreunit.util.BaseTools;
import org.moreunit.util.TestCaseDiviner;

/**
 * ClassTypeFacade offers easy access to a simple java file within eclipse.
 * The file represented by this instance is not a testcase.
 * 
 * @author vera
 *
 * 23.05.2006 20:28:52
 */
public class ClassTypeFacade extends TypeFacade {
	
	private TestCaseDiviner testCaseDiviner;

	public ClassTypeFacade(ICompilationUnit compilationUnit) {
		super(compilationUnit);
	}
	
 
	public ClassTypeFacade(IEditorPart editorPart) {
		super(editorPart);
	}

	public ClassTypeFacade(IFile file) {
		super(file);
	}
	
	/**
	 * Returns the corresponding testcase of the javaFileFacade. If there are more
	 * than one testcases the uses has to make a choice via a dialog. If no test is
	 * found <code>null</code> is returned.
	 * 
	 * @return one of the corresponding testcases
	 */
	public IType getOneCorrespondingTestCase() {
		Set<IType> testcases = getCorrespondingTestCaseList();
		IType testcaseToJump = null;
		if(testcases.size() == 1)
			testcaseToJump = (IType) testcases.toArray()[0];
		else if(testcases.size() > 1)
			testcaseToJump = new TypeChoiceDialog((IType[]) testcases.toArray(new IType[testcases.size()])).getChoice();
		
		return testcaseToJump;
	}
	
	public Set<IType> getCorrespondingTestCaseList() {
		return getTestCaseDiviner().getMatches();
	}
	
	public IMethod getCorrespondingTestMethod(IMethod method, IType testCaseType) {
		String nameOfCorrespondingTestMethod = BaseTools.getTestmethodNameFromMethodName(method.getElementName());
		
		if(testCaseType == null)
			return null;
		
		try {
			IMethod[] methodsOfType = testCaseType.getCompilationUnit().findPrimaryType().getMethods();
			for(int i=0; i<methodsOfType.length; i++) {
				IMethod testmethod = methodsOfType[i];
				if(testmethod.getElementName().startsWith(nameOfCorrespondingTestMethod))
					return testmethod;
			}
		} catch (JavaModelException exc) {
			LogHandler.getInstance().handleExceptionLog(exc);
		}
		
		return null;
	}
	
	public List<IMethod> getCorrespondingTestMethods(IMethod method) {
		List<IMethod> result = new ArrayList<IMethod>();
		
		Set<IType> allTestCases = getCorrespondingTestCaseList();
		
		for(IType testCaseType: allTestCases) {
			result.addAll(getTestMethodsForTestCase(method, testCaseType));
		}
		
		return result;
	}
	
	private List<IMethod> getTestMethodsForTestCase(IMethod method, IType testCaseType) {
		List<IMethod> result = new ArrayList<IMethod>();
		
		if(testCaseType == null)
			return result;
		
		String nameOfCorrespondingTestMethod = BaseTools.getTestmethodNameFromMethodName(method.getElementName());
		
		try {
			IMethod[] methodsOfType = testCaseType.getCompilationUnit().findPrimaryType().getMethods();
			for(int i=0; i<methodsOfType.length; i++) {
				IMethod testmethod = methodsOfType[i];
				if(testmethod.getElementName().startsWith(nameOfCorrespondingTestMethod))
					result.add(testmethod);
			}
		} catch (JavaModelException exc) {
			LogHandler.getInstance().handleExceptionLog(exc);
		}
		
		return result;
	}
	
	/**
	 * Getter uses lazy caching. 
	 */
	private TestCaseDiviner getTestCaseDiviner() {
		if(testCaseDiviner == null)
			testCaseDiviner = new TestCaseDiviner(compilationUnit, Preferences.instance());
		
		return testCaseDiviner;
	}

}
