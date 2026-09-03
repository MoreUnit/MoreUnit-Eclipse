package org.moreunit.util;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class BaseToolsTest
{

    @Test
    public void getTestedClass_should_return_empty_list_when_called_without_prefix()
    {
        final String className = "Eins";
        final String[] prefixes = { "Test" };
        assertTrue(BaseTools.getTestedClass(className, prefixes, new String[0], null, null).isEmpty());
    }

    @Test
    public void getTestedClass_should_return_name_without_prefix()
    {
        String className = "EinsTest";
        String[] suffixes = { "Test" };

        assertEquals(Arrays.asList("Eins"), BaseTools.getTestedClass(className, new String[0], suffixes, null, null));

        className = "TestCaseDivinerTest";
        suffixes = new String[] { "Test" };
        assertEquals(Arrays.asList("TestCaseDiviner"), BaseTools.getTestedClass(className, new String[0], suffixes, null, null));
    }

    @Test
    public void getTestedClass_should_return_empty_list_when_called_with_null_as_classname()
    {
        final String className = null;
        assertTrue(BaseTools.getTestedClass(className, new String[0], new String[0], null, null).isEmpty());
    }

    @Test
    public void getTestedClass_should_return_empty_list_when_called_with_empty_prefix_and_suffix_lists()
    {
        final String className = "ABC";
        assertTrue(BaseTools.getTestedClass(className, new String[0], new String[0], null, null).isEmpty());
    }

    @Test
    public void getTestedClass_should_return_empty_list_when_called_with_null_as_prefix_and_suffix_lists()
    {
        final String className = "ABC";
        assertTrue(BaseTools.getTestedClass(className, null, null, null, null).isEmpty());
    }

    @Test
    public void getTestedClass_should_handle_more_than_one_suffix()
    {
        final String[] suffixes = { "SystemTest", "Test" };
        String className = "EinsTest";
        assertEquals(Arrays.asList("Eins"), BaseTools.getTestedClass(className, new String[0], suffixes, null, null));
        className = "EinsSystemTest";
        assertEquals(Arrays.asList("Eins", "EinsSystem"), BaseTools.getTestedClass(className, new String[0], suffixes, null, null));
    }

    @Test
    public void getTestedClass_should_handle_package_prefix()
    {
        String className = "test.EinsTest";
        final String[] suffixes = { "Test" };
        final String packagePrefix = "test";
        assertEquals(Arrays.asList("Eins"), BaseTools.getTestedClass(className, new String[0], suffixes, packagePrefix, null));

        className = "EinsTest";
        assertEquals(Arrays.asList("Eins"), BaseTools.getTestedClass(className, new String[0], suffixes, packagePrefix, null));

        className = "test.pack.EinsTest";
        assertEquals(Arrays.asList("pack.Eins"), BaseTools.getTestedClass(className, new String[0], suffixes, packagePrefix, null));

        className = "testpack.EinsTest";
        assertEquals(Arrays.asList("testpack.Eins"), BaseTools.getTestedClass(className, new String[0], suffixes, packagePrefix, null));
    }

    @Test
    public void getTestedClass_should_handle_package_suffix() throws Exception
    {
        String className = "test.EinsTest";
        final String[] suffixes = { "Test" };
        final String packageSuffix = "test";
        assertEquals(Arrays.asList("Eins"), BaseTools.getTestedClass(className, new String[0], suffixes, null, packageSuffix));

        className = "EinsTest";
        assertEquals(Arrays.asList("Eins"), BaseTools.getTestedClass(className, new String[0], suffixes, null, packageSuffix));

        className = "pack.test.EinsTest";
        assertEquals(Arrays.asList("pack.Eins"), BaseTools.getTestedClass(className, new String[0], suffixes, null, packageSuffix));

        className = "packtest.EinsTest";
        assertEquals(Arrays.asList("packtest.Eins"), BaseTools.getTestedClass(className, new String[0], suffixes, null, packageSuffix));

        className = "test.test.EinsTest";
        assertEquals(Arrays.asList("test.Eins"), BaseTools.getTestedClass(className, new String[0], suffixes, null, packageSuffix));
    }

    @Test
    public void getListOfUnqualifiedTypeNames()
    {
        String testString = "One";
        List<String> result = BaseTools.getListOfUnqualifiedTypeNames(testString);
        assertEquals(Arrays.asList("One"), result);

        testString = "OneTwo";
        result = BaseTools.getListOfUnqualifiedTypeNames(testString);
        assertEquals(result, Arrays.asList("One", "OneTwo"));

        testString = "OneTwoThree";
        result = BaseTools.getListOfUnqualifiedTypeNames(testString);
        assertEquals(result, Arrays.asList("One", "OneTwo", "OneTwoThree"));

        testString = "oneTwo";
        result = BaseTools.getListOfUnqualifiedTypeNames(testString);
        assertEquals(result, Arrays.asList("one", "oneTwo"));

        testString = "pack.age.OneTwoThree";
        result = BaseTools.getListOfUnqualifiedTypeNames(testString);
        assertEquals(result, Arrays.asList("pack.age.One", "pack.age.OneTwo", "pack.age.OneTwoThree"));
    }

    @Test
    public void getListOfUnqualifiedTypeNames_should_return_list_sorted_by_raw_length() throws Exception
    {
        final ArrayList<String> testedClasses = new ArrayList<>();
        testedClasses.add("EinsZweiDrei");
        testedClasses.add("OneTwoThree");
        final List<String> result = BaseTools.getListOfUnqualifiedTypeNames(testedClasses);
        assertEquals(result.get(0), "EinsZweiDrei");
        assertEquals(result.get(1), "OneTwoThree");
        assertEquals(result.get(5), "One");
    }

    @Test
    public void getFirstMethodWithSameNamePrefix_should_return_null_when_methodName_is_null() {
        final org.eclipse.jdt.core.IMethod[] methods = new org.eclipse.jdt.core.IMethod[0];
        assertNull(BaseTools.getFirstMethodWithSameNamePrefix(methods, null));
    }

    @Test
    public void getFirstMethodWithSameNamePrefix_should_return_null_when_no_method_matches() {
        final org.eclipse.jdt.core.IMethod method1 = org.mockito.Mockito.mock(org.eclipse.jdt.core.IMethod.class);
        org.mockito.Mockito.when(method1.getElementName()).thenReturn("foo");
        org.mockito.Mockito.when(method1.exists()).thenReturn(true);

        final org.eclipse.jdt.core.IMethod[] methods = { method1 };
        assertNull(BaseTools.getFirstMethodWithSameNamePrefix(methods, "bar"));
    }

    @Test
    public void getFirstMethodWithSameNamePrefix_should_return_matching_method() {
        final org.eclipse.jdt.core.IMethod method1 = org.mockito.Mockito.mock(org.eclipse.jdt.core.IMethod.class);
        org.mockito.Mockito.when(method1.getElementName()).thenReturn("foo");
        org.mockito.Mockito.when(method1.exists()).thenReturn(true);

        final org.eclipse.jdt.core.IMethod[] methods = { method1 };
        assertEquals(method1, BaseTools.getFirstMethodWithSameNamePrefix(methods, "fooBar"));
    }

    @Test
    public void getFirstMethodWithSameNamePrefix_should_not_return_method_that_does_not_exist() {
        final org.eclipse.jdt.core.IMethod method1 = org.mockito.Mockito.mock(org.eclipse.jdt.core.IMethod.class);
        org.mockito.Mockito.when(method1.getElementName()).thenReturn("foo");
        org.mockito.Mockito.when(method1.exists()).thenReturn(false);

        final org.eclipse.jdt.core.IMethod[] methods = { method1 };
        assertNull(BaseTools.getFirstMethodWithSameNamePrefix(methods, "fooBar"));
    }
}
