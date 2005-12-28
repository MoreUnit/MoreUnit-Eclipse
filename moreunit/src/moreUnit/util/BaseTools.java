/*
 * Created on 20.02.2005 20:03:01
 * Created by Vera
 */
package moreUnit.util;

import org.eclipse.core.resources.IFile;


public class BaseTools {

	public static String getTestedClass(String testCaseClass) {
		if(!testCaseClass.endsWith(MagicNumbers.TEST_CASE_SUFFIX))
			return null;

		return testCaseClass.substring(0, testCaseClass.length()-4);
	}
	
	public static String getTestedMethod(String testMethodName) {
		if(!testMethodName.startsWith(MagicNumbers.TEST_METHOD_PRAEFIX))
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