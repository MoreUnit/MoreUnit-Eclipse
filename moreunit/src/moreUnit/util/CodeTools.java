package moreUnit.util;

import moreUnit.log.LogHandler;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

public class CodeTools {
	
	public static void addTestCaseMethod(IMethod methodToTest, IType testCaseType) {
		try {
			String methodName = methodToTest.getElementName();
			String erstesZeichen = String.valueOf(methodName.charAt(0));
			methodName = methodName.replaceFirst(erstesZeichen, erstesZeichen.toUpperCase());
			
			String testMethodName = MagicNumbers.TEST_METHOD_PRAEFIX+methodName;
			if(isMethodeBereitsVorhanden(testCaseType, testMethodName))
				return;
			
			String methodHead = "public void "+testMethodName+"() {"+MagicNumbers.NEWLINE+"}";
			testCaseType.createMethod(methodHead, null, true, null);
		} catch (JavaModelException exc) {
			exc.printStackTrace();
		}
	}
	
	private static boolean isMethodeBereitsVorhanden(IType klassenTyp, String methodenName) {
		try {
			IMethod[] vorhandeneTests = klassenTyp.getMethods();
			for (int i = 0; i < vorhandeneTests.length; i++) {
				IMethod method = vorhandeneTests[i];
				if(methodenName.equals(method.getElementName()))
					return true;
			}
		} catch (JavaModelException exc) {
			LogHandler.getInstance().handleExceptionLog(exc);
		}
		
		return false;
	}
	
	public static IMethod getFirstMethodByName(IType classType, String methodName) {
		try {
			IMethod[] methodsOfType = classType.getMethods();
			for(int i=0; i<methodsOfType.length; i++) {
				IMethod method = methodsOfType[i];
				if(methodName.equals(method.getElementName()))
					return method;
			}
		} catch (JavaModelException exc) {
			LogHandler.getInstance().handleExceptionLog(exc);
		}
		
		return null;
	}
}