/*
 * Created on 20.02.2005 20:03:01
 * Created by Vera
 */
package moreUnit.util;

import moreUnit.log.LogHandler;

import org.eclipse.core.resources.IFile;


public class BaseTools {
	
	public static String getTestmethodNameFromMethodName(String methodName) {
		if(methodName == null || methodName.length() == 0) {
			LogHandler.getInstance().handleWarnLog("Methodname is null or has length of 0");
			return MagicNumbers.EMPTY_STRING;
		}
		
		String erstesZeichen = String.valueOf(methodName.charAt(0));
		methodName = methodName.replaceFirst(erstesZeichen, erstesZeichen.toUpperCase());
		
		String testMethodName = MagicNumbers.TEST_METHOD_PRAEFIX+methodName;
		
		return testMethodName;
	}

	public static String getTestedClass(String testCaseClass) {
		if(testCaseClass == null || testCaseClass.length() < 4 || !testCaseClass.endsWith(MagicNumbers.TEST_CASE_SUFFIX))
			return null;

		return testCaseClass.substring(0, testCaseClass.length()-4);
	}
	
	public static String getTestedMethod(String testMethodName) {
		if(testMethodName == null || !testMethodName.startsWith(MagicNumbers.TEST_METHOD_PRAEFIX))
			return null;
		
		char erstesZeichen = testMethodName.charAt(4);
		StringBuffer result = new StringBuffer();
		result.append(Character.toLowerCase(erstesZeichen));
		result.append(testMethodName.substring(5));
		return result.toString();
	}
	
	public static String getNameOfTestCaseClass(IFile classToTest) {
		String fileNameWithoutExtension = classToTest.getName().replaceFirst(MagicNumbers.STRING_DOT+classToTest.getFileExtension(), MagicNumbers.EMPTY_STRING);
		return fileNameWithoutExtension+MagicNumbers.TEST_CASE_SUFFIX;
	}
}

// $Log: not supported by cvs2svn $
// Revision 1.3  2006/01/19 21:38:32  gianasista
// Added CVS-commit-logging to all java-files
//