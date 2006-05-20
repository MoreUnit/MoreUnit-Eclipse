package moreUnit.util;

import junit.framework.TestCase;

public class BaseToolsTest extends TestCase {

	public void testGetTestmethodNameFromMethodName() {
		String methodName = "getValue";
		assertEquals("testGetValue", BaseTools.getTestmethodNameFromMethodName(methodName));

		methodName = null;
		assertEquals("", BaseTools.getTestmethodNameFromMethodName(methodName));
		
		methodName = "";
		assertEquals("", BaseTools.getTestmethodNameFromMethodName(methodName));
	}

	public void testGetTestedClass() {
		String className = "Eins";
		String[] prefixes = new String[] { "Test" };
		assertNull(BaseTools.getTestedClass(className, prefixes, new String[0]));
		
		className = "EinsTest";
		String[] suffixes = new String[] { "Test" };
		assertEquals("Eins", BaseTools.getTestedClass(className, new String[0], suffixes));
		
		className = null;
		assertNull(BaseTools.getTestedClass(className, new String[0], new String[0]));
		
		className = "ABC";
		assertNull(BaseTools.getTestedClass(className, new String[0], new String[0]));
		assertNull(BaseTools.getTestedClass(className, null, null));
	}

	public void testGetTestedMethod() {
		String methodName = "getValue";
		assertNull(BaseTools.getTestedMethod(methodName));
		
		methodName = "testGetValue";
		assertEquals("getValue", BaseTools.getTestedMethod(methodName));
		
		methodName = null;
		assertNull(BaseTools.getTestedMethod(methodName));
	}

	public void testGetTestMethodNameAfterRename() {
		String methodNameBeforeRename = "countMembers";
		String methodNameAfterRename = "countAllMembers";
		String testMethodName = "testCountMembersSpecialCase";
		
		assertEquals("testCountAllMembersSpecialCase", BaseTools.getTestMethodNameAfterRename(methodNameBeforeRename, methodNameAfterRename, testMethodName));
		
		testMethodName = "testCountMembers";
		assertEquals("testCountAllMembers", BaseTools.getTestMethodNameAfterRename(methodNameBeforeRename, methodNameAfterRename, testMethodName));
	}

	public void testGetStringWithFirstCharToUpperCase() {
		String testString = "hello";
		assertEquals("Hello", BaseTools.getStringWithFirstCharToUpperCase(testString));
		
		assertEquals(null, BaseTools.getStringWithFirstCharToUpperCase(null));
		assertEquals("", BaseTools.getStringWithFirstCharToUpperCase(""));
	}
}