package org.moreunit.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
		assertEmpty(BaseTools.getTestedClass(className, prefixes, new String[0], null, null));

		className = "EinsTest";
		String[] suffixes = new String[] { "Test" };
		String[] items = { "Eins" };
		assertEquals(Arrays.asList(items), BaseTools.getTestedClass(className, new String[0], suffixes, null, null));

		className = "TestCaseDivinerTest";
		suffixes = new String[] { "Test" };
		String[] items1 = { "TestCaseDiviner" };
		assertEquals(Arrays.asList(items1), BaseTools.getTestedClass(className, new String[0], suffixes, null, null));

		className = null;
		assertEmpty(BaseTools.getTestedClass(className, new String[0], new String[0], null, null));

		className = "ABC";
		assertEmpty(BaseTools.getTestedClass(className, new String[0], new String[0], null, null));
		assertEmpty(BaseTools.getTestedClass(className, null, null, null, null));
	}

	private void assertEmpty(Collection<?> collection) {
		assertTrue(collection.isEmpty());
	}

	public void testGetTestedClassWithMultipleSuffixes() {
		String[] suffixes = new String[] { "SystemTest", "Test" };
		String className = "EinsTest";
		String[] items = { "Eins" };
		assertEquals(Arrays.asList(items), BaseTools.getTestedClass(className, new String[0], suffixes, null, null));
		className = "EinsSystemTest";
		String[] items1 = { "Eins", "EinsSystem" };
		assertEquals(Arrays.asList(items1), BaseTools.getTestedClass(className, new String[0], suffixes, null, null));
	}

	public void testGetTestedClassWithPackagePrefix() throws Exception {
		String className = "test.EinsTest";
		String[] suffixes = new String[] { "Test" };
		String packagePrefix = "test";
		String[] items = { "Eins" };
		assertEquals("Test with prefix", Arrays.asList(items), BaseTools.getTestedClass(className, new String[0], suffixes, packagePrefix, null));

		className = "EinsTest";
		String[] items1 = { "Eins" };
		assertEquals("Test without prefix but package prefix set", Arrays.asList(items1), BaseTools.getTestedClass(className, new String[0], suffixes, packagePrefix, null));
	}

	public void testGetTestedClassWithPackageSuffix() throws Exception {
		String className = "test.EinsTest";
		String[] suffixes = new String[] { "Test" };
		String packageSuffix = "test";
		String[] items = { "Eins" };
		assertEquals("Test with prefix", Arrays.asList(items), BaseTools.getTestedClass(className, new String[0], suffixes, null, packageSuffix));

		className = "EinsTest";
		String[] items1 = { "Eins" };
		assertEquals("Test without suffix but package prefix set", Arrays.asList(items1), BaseTools.getTestedClass(className, new String[0], suffixes, null, packageSuffix));
	}

	public void testGetTestedMethod() {
		String methodName = "getValue";
		assertNull(BaseTools.getTestedMethod(methodName));

		methodName = "testGetValue";
		assertEquals("getValue", BaseTools.getTestedMethod(methodName));

		methodName = null;
		assertNull(BaseTools.getTestedMethod(methodName));

		methodName = "test";
		assertEquals(null, BaseTools.getTestedMethod(methodName));
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

	public void testRemoveSuffixFromTestCase() {
		String testClassName = "com.my.test.MyTest";
		String packageSuffix = "test";

		assertEquals("com.my.MyTest", BaseTools.removeSuffixFromTestCase(testClassName, packageSuffix));

		testClassName = "test.MyTest";
		assertEquals("MyTest", BaseTools.removeSuffixFromTestCase(testClassName, packageSuffix));
	}

	public void testFirstCharToUpperCase() {
		assertEquals("Test", BaseTools.firstCharToUpperCase("test"));
		assertEquals("Test", BaseTools.firstCharToUpperCase("Test"));
		assertEquals("T", BaseTools.firstCharToUpperCase("t"));
		assertEquals("T", BaseTools.firstCharToUpperCase("T"));
		assertEquals(null, BaseTools.firstCharToUpperCase(null));
		assertEquals("", BaseTools.firstCharToUpperCase(""));

	}

	public void testGetListOfUnqualifiedTypeNames() {
		String testString = "One";
		List<String> result = BaseTools.getListOfUnqualifiedTypeNames(testString);
		assertEquals(1, result.size());
		assertEquals("One", result.get(0));

		testString = "OneTwo";
		result = BaseTools.getListOfUnqualifiedTypeNames(testString);
		assertEquals(2, result.size());
		assertEquals("One", result.get(0));
		assertEquals("OneTwo", result.get(1));

		testString = "OneTwoThree";
		result = BaseTools.getListOfUnqualifiedTypeNames(testString);
		assertEquals(3, result.size());
		assertEquals("One", result.get(0));
		assertEquals("OneTwo", result.get(1));
		assertEquals("OneTwoThree", result.get(2));
	}
}