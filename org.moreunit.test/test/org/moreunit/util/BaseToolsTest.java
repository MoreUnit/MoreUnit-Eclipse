package org.moreunit.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

public class BaseToolsTest extends TestCase {

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

	public void testRemoveSuffixFromTestCase() {
		String testClassName = "com.my.test.MyTest";
		String packageSuffix = "test";

		assertEquals("com.my.MyTest", BaseTools.removeSuffixFromTestCase(testClassName, packageSuffix));

		testClassName = "test.MyTest";
		assertEquals("MyTest", BaseTools.removeSuffixFromTestCase(testClassName, packageSuffix));
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
		
		testString = "oneTwo";
		result = BaseTools.getListOfUnqualifiedTypeNames(testString);
		assertEquals(2, result.size());
		assertEquals("one", result.get(0));
		assertEquals("oneTwo", result.get(1));
	}
	
	public void testReturnsListOfUnqualifiedTypeNamesSortedByRawLength() throws Exception {
		ArrayList<String> testedClasses = new ArrayList<String>(); 
		testedClasses.add("EinsZweiDrei");
		testedClasses.add("OneTwoThree");
		List<String> result = BaseTools.getListOfUnqualifiedTypeNames(testedClasses);
		assertEquals("EinsZweiDrei", result.get(0));
		assertEquals("OneTwoThree", result.get(1));
		//Yadda, yadda. Just make sure the last one's right, nobody really cares.
		assertEquals("One", result.get(5));
	}
}