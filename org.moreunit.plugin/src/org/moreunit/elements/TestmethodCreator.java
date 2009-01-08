/**
 * 
 */
package org.moreunit.elements;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.moreunit.log.LogHandler;
import org.moreunit.preferences.PreferenceConstants;
import org.moreunit.util.BaseTools;
import org.moreunit.util.MoreUnitContants;
import org.moreunit.util.StringConstants;
import org.moreunit.util.TestMethodDiviner;
import org.moreunit.util.TestMethodDivinerFactory;

/**
 * @author vera
 *
 * 27.06.2007 20:59:15<br>
 * 
 * This class is responsible for creating the testmethod-stubs.
 * There are 3 different types of stubs:<br>
 * <ul>
 * <li>JUnit 3 tests</li>
 * <li>JUnit 4 tests</li>
 * <li>TestNG tests</li>
 * </ul>
 */
public class TestmethodCreator {
	
	private ICompilationUnit compilationUnit;
	private String testType;
	TestMethodDivinerFactory testMethodDivinerFactory;
	TestMethodDiviner testMethodDiviner;
	
	public TestmethodCreator(ICompilationUnit compilationUnit, String testType) {
		this.compilationUnit = compilationUnit;
		this.testType = testType;
		testMethodDivinerFactory = new TestMethodDivinerFactory(compilationUnit);
		testMethodDiviner = testMethodDivinerFactory.create();
	}
	
	public IMethod createTestMethod(IMethod method)
	{
		if(method == null)
			return null;
		
		if(TypeFacade.isTestCase(compilationUnit.findPrimaryType())){
			return createAnotherTestMethod(method);
		}
		return createFirstTestMethod(method);
	}
	
	private IMethod createFirstTestMethod(IMethod method)
	{
		ClassTypeFacade classTypeFacade = new ClassTypeFacade(compilationUnit);
		compilationUnit = classTypeFacade.getOneCorrespondingTestCase(true).getCompilationUnit();
		String testMethodName = testMethodDiviner.getTestMethodNameFromMethodName(method.getElementName());

		if (doesMethodExist(testMethodName))
			return null;
		
		//TODO move this into the testMethodDiviner
		if(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4.equals(testType))
			return createJUnit4Testmethod(testMethodName);
		else if(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_3.equals(testType))
			return createJUnit3Testmethod(testMethodName);
		else if(PreferenceConstants.TEST_TYPE_VALUE_TESTNG.equals(testType))
			return createTestNgTestMethod(testMethodName);
		
		return null;
	}
	

	private IMethod createAnotherTestMethod(IMethod testMethod)
	{
		String testedMethodName = testMethodDiviner.getMethodNameFromTestMethodName(testMethod.getElementName());
		TestCaseTypeFacade testCaseTypeFacade = new TestCaseTypeFacade(compilationUnit);
		IMethod testedMethod = null;
		try {
			testedMethod = BaseTools.getFirstMethodWithSameNamePrefix(testCaseTypeFacade.getCorrespondingClassUnderTest().getMethods(), testedMethodName);
		} catch (JavaModelException e) {
			LogHandler.getInstance().handleExceptionLog(e);
		}
		
		if(testedMethod != null) {
			String testMethodName = testMethod.getElementName();
			//TODO verify correct
			//			String testMethodName = MoreUnitContants.TEST_METHOD_PRAEFIX + BaseTools.firstCharToUpperCase(testedMethod.getElementName());
			if (doesMethodExist(testMethodName))
				testMethodName = testMethodName.concat(MoreUnitContants.SUFFIX_NAME);
			
			if(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4.equals(testType))
				return createJUnit4Testmethod(testMethodName);
			else if(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_3.equals(testType))
				return createJUnit3Testmethod(testMethodName);
			else if(PreferenceConstants.TEST_TYPE_VALUE_TESTNG.equals(testType))
				return createTestNgTestMethod(testMethodName);
		}
		
		return null;
	}
	
	protected IMethod createJUnit3Testmethod(String testMethodName) {
		return createMethod(testMethodName, getJUnit3MethodStub(testMethodName));
	}
	
	private IMethod createTestNgTestMethod(String testMethodName) {
		return createMethod(testMethodName, getTestNgMethodStub(testMethodName));
	}
	
	private String getTestNgMethodStub(String testmethodName) {
		StringBuffer methodContent = new StringBuffer();
		methodContent.append("@Test").append(StringConstants.NEWLINE);
		methodContent.append("public void ").append(testmethodName).append("() {").append(StringConstants.NEWLINE).append("}");
		
		return methodContent.toString();
	}
	
	private String getJUnit3MethodStub(String testmethodName) {
		StringBuffer methodContent = new StringBuffer();
		methodContent.append("public void ").append(testmethodName).append("() {").append(StringConstants.NEWLINE).append("}");
		
		return methodContent.toString();
	}
	
	protected IMethod createJUnit4Testmethod(String testMethodName) {
		return createMethod(testMethodName, getJUnit4MethodStub(testMethodName));
	}
	
	private String getJUnit4MethodStub(String testmethodName) {
		StringBuffer methodContent = new StringBuffer();
		methodContent.append("@Test").append(StringConstants.NEWLINE);
		methodContent.append("public void ").append(testmethodName).append("() {").append(StringConstants.NEWLINE).append("}");
		
		return methodContent.toString();
	}
	
	private IMethod createMethod(String methodName, String methodString) {
		if(doesMethodExist(methodName))
			return null;
		
		try {
			return compilationUnit.findPrimaryType().createMethod(methodString, null, true, null);
		} catch (JavaModelException exc) {
			LogHandler.getInstance().handleExceptionLog(exc);
		}
		return null;
	}
	
	protected boolean doesMethodExist(String testMethodName) {
		try {
			IMethod[] existingTests = compilationUnit.findPrimaryType().getMethods();
			for (int i = 0; i < existingTests.length; i++) {
				IMethod method = existingTests[i];
				if(testMethodName.equals(method.getElementName()))
					return true;
			}
		} catch (JavaModelException exc) {
			LogHandler.getInstance().handleExceptionLog(exc);
		}
		
		return false;
	}
}
