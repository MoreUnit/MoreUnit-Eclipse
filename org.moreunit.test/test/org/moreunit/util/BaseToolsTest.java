package org.moreunit.util;

import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class BaseToolsTest
{

    @Test
    public void getTestedClass_should_return_empty_list_when_called_without_prefix()
    {
        String className = "Eins";
        String[] prefixes = new String[] { "Test" };
        assertThat(BaseTools.getTestedClass(className, prefixes, new String[0], null, null)).isEmpty();
    }
    
    @Test
    public void getTestedClass_should_return_name_without_prefix()
    {
        String className = "EinsTest";
        String[] suffixes = new String[] { "Test" };

        assertThat(BaseTools.getTestedClass(className, new String[0], suffixes, null, null)).containsExactly("Eins");
        
        className = "TestCaseDivinerTest";
        suffixes = new String[] { "Test" };
        assertThat(BaseTools.getTestedClass(className, new String[0], suffixes, null, null)).containsExactly("TestCaseDiviner");
    }
    
    @Test
    public void getTestedClass_should_return_empty_list_when_called_with_null_as_classname()
    {
        String className = null;
        assertThat(BaseTools.getTestedClass(className, new String[0], new String[0], null, null)).isEmpty();
    }
    
    @Test
    public void getTestedClass_should_return_empty_list_when_called_with_empty_prefix_and_suffix_lists()
    {
        String className = "ABC";
        assertThat(BaseTools.getTestedClass(className, new String[0], new String[0], null, null)).isEmpty();
    }
    
    @Test
    public void getTestedClass_should_return_empty_list_when_called_with_null_as_prefix_and_suffix_lists()
    {
        String className = "ABC";
        assertThat(BaseTools.getTestedClass(className, null, null, null, null)).isEmpty();
    }
    
    @Test
    public void getTestedClass_should_handle_more_than_one_suffix()
    {
        String[] suffixes = new String[] { "SystemTest", "Test" };
        String className = "EinsTest";
        assertThat(BaseTools.getTestedClass(className, new String[0], suffixes, null, null)).containsExactly("Eins");
        className = "EinsSystemTest";
        assertThat(BaseTools.getTestedClass(className, new String[0], suffixes, null, null)).containsExactly("Eins", "EinsSystem");
    }

    @Test
    public void getTestedClass_should_handle_package_prefix()
    {
        String className = "test.EinsTest";
        String[] suffixes = new String[] { "Test" };
        String packagePrefix = "test";
        assertThat(BaseTools.getTestedClass(className, new String[0], suffixes, packagePrefix, null)).containsExactly("Eins");

        className = "EinsTest";
        assertThat(BaseTools.getTestedClass(className, new String[0], suffixes, packagePrefix, null)).containsExactly("Eins");
    }

    @Test
    public void getTestedClass_should_handle_package_suffix() throws Exception
    {
        String className = "test.EinsTest";
        String[] suffixes = new String[] { "Test" };
        String packageSuffix = "test";
        assertThat(BaseTools.getTestedClass(className, new String[0], suffixes, null, packageSuffix)).containsExactly("Eins");

        className = "EinsTest";
        assertThat(BaseTools.getTestedClass(className, new String[0], suffixes, null, packageSuffix)).containsExactly("Eins");
    }

    @Test
    public void removeSuffixFromTestCase()
    {
        String testClassName = "com.my.test.MyTest";
        String packageSuffix = "test";

        assertThat(BaseTools.removeSuffixFromTestCase(testClassName, packageSuffix)).isEqualTo("com.my.MyTest");

        testClassName = "test.MyTest";
        assertThat(BaseTools.removeSuffixFromTestCase(testClassName, packageSuffix)).isEqualTo("MyTest");
    }

    @Test
    public void getListOfUnqualifiedTypeNames()
    {
        String testString = "One";
        List<String> result = BaseTools.getListOfUnqualifiedTypeNames(testString);
        assertThat(result).containsExactly("One");

        testString = "OneTwo";
        result = BaseTools.getListOfUnqualifiedTypeNames(testString);
        assertThat(result).isEqualTo(Arrays.asList("One", "OneTwo"));

        testString = "OneTwoThree";
        result = BaseTools.getListOfUnqualifiedTypeNames(testString);
        assertThat(result).isEqualTo(Arrays.asList("One", "OneTwo", "OneTwoThree"));

        testString = "oneTwo";
        result = BaseTools.getListOfUnqualifiedTypeNames(testString);
        assertThat(result).isEqualTo(Arrays.asList("one", "oneTwo"));
    }

    @Test
    public void getListOfUnqualifiedTypeNames_should_return_list_sorted_by_raw_length() throws Exception
    {
        ArrayList<String> testedClasses = new ArrayList<String>();
        testedClasses.add("EinsZweiDrei");
        testedClasses.add("OneTwoThree");
        List<String> result = BaseTools.getListOfUnqualifiedTypeNames(testedClasses);
        assertThat(result).startsWith("EinsZweiDrei", "OneTwoThree");
        assertThat(result).endsWith("One");
    }
}
