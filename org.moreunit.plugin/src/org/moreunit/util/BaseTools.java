/*
 * Created on 20.02.2005 20:03:01
 * Created by Vera
 */
package org.moreunit.util;


import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.ui.JavaUI;

import org.eclipse.jdt.core.IJavaElement;
import org.moreunit.log.LogHandler;


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
	 * @param packageSuffix
	 * @return name of the class under test
	 */
	public static String getTestedClass(String testCaseClass, String[] prefixes, String[] suffixes, String packagePrefix, String packageSuffix) {
		if(testCaseClass == null || testCaseClass.length() <= 1)
			return null;
		
		if (packagePrefix != null && packagePrefix.length() > 0) {
			testCaseClass = testCaseClass.replaceFirst(packagePrefix + "\\.", "");
		}
		if(packageSuffix != null && packageSuffix.length() > 0) {
			testCaseClass = removeSuffixFromTestCase(testCaseClass, packageSuffix);
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
	
	protected static String removeSuffixFromTestCase(String testClassName, String packageSuffix) {
		String[] pathElements = testClassName.split("\\.");
		int theLastButOne = pathElements.length - 2;
		if(theLastButOne < 0)
			return testClassName;
		if(pathElements[theLastButOne].equals(packageSuffix)) {
			pathElements[theLastButOne] = MagicNumbers.EMPTY_STRING;
			
			StringBuffer result = new StringBuffer();
			for(int i=0; i<theLastButOne; i++) {
				result.append(pathElements[i]).append(MagicNumbers.STRING_DOT);
			}
			return result.append(pathElements[pathElements.length-1]).toString();
		}
		
		return testClassName;
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
	
	/**
	 * Returns the first method which has the same beginning.<br>
	 * Example:<br>
	 * methods[0] = some()<br>
	 * methods[1] = foo()<br>
	 * methodName = "fooSome"<br>
	 * returns foo()<br>
	 * If no method is found in the array, this method returns <code>null</code>.
	 * 
	 * @param methods
	 * @param methodName
	 * @return
	 */
	public static IMethod getFirstMethodWithSameNamePrefix(IMethod[] methods, String methodName) {
		if(methodName != null) {
			for(int i=0; i<methods.length; i++) {
				IMethod method = methods[i];
				if(methodName.startsWith(method.getElementName()) && method.exists()) {
					return method;
				}
			}
		}
		
		return null;
	}
	
	public static String firstCharToUpperCase(String aString) {
		if(aString == null || aString.length() == 0)
			return aString;
		
		String firstChar = String.valueOf(aString.charAt(0));
		return aString.replaceFirst(firstChar, firstChar.toUpperCase());
	}
}

// $Log: not supported by cvs2svn $
// Revision 1.7  2006/11/25 15:00:51  gianasista
// new method
//
// Revision 1.6  2006/10/08 17:28:50  gianasista
// Suffix preference
//
// Revision 1.5  2006/10/01 13:02:43  channingwalton
// Implementation for [ 1556583 ] Extend testcase matching across whole workspace
//
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