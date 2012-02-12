package org.moreunit.util;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IType;
import org.junit.Test;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.Project;

/**
 * @author giana 13.05.2006 13:49:29
 */
public class TestCaseDivinerTest extends ContextTestCase
{

    @Preferences(testClassSuffixes="Test", testSrcFolder="test")
    @Project(mainCls="Foo", testCls="FooTest;FooTestNG", mainSrcFolder="src", testSrcFolder="test")
    @Test
    public void getMatches_should_return_class_which_matches_suffix() throws CoreException
    {
        TestCaseDiviner testCaseDiviner = new TestCaseDiviner(context.getCompilationUnit("Foo"), org.moreunit.preferences.Preferences.getInstance());
        Set<IType> result = testCaseDiviner.getMatches();
        
        assertThat(result).onProperty("elementName").containsOnly("FooTest");
    }
    
    @Preferences(testClassSuffixes="Test,TestNG", testSrcFolder="test")
    @Project(mainCls="Foo", testCls="FooTest;FooTestNG", mainSrcFolder="src", testSrcFolder="test")
    @Test
    public void getMatches_should_find_all_tests_which_match_all_suffixes() throws CoreException
    {
        TestCaseDiviner testCaseDiviner = new TestCaseDiviner(context.getCompilationUnit("Foo"), org.moreunit.preferences.Preferences.getInstance());
        Set<IType> result = testCaseDiviner.getMatches();
        assertThat(result).onProperty("elementName").containsOnly("FooTest", "FooTestNG");
    }

    @Preferences(testClassPrefixes="Test", testSrcFolder="test")
    @Project(mainCls="Foo", testCls="TestFoo;BFooTest", mainSrcFolder="src", testSrcFolder="test")
    @Test
    public void getMatches_should_return_class_which_matches_prefix() throws CoreException
    {
        TestCaseDiviner testCaseDiviner = new TestCaseDiviner(context.getCompilationUnit("Foo"), org.moreunit.preferences.Preferences.getInstance());
        Set<IType> result = testCaseDiviner.getMatches();
        
        assertThat(result).onProperty("elementName").containsOnly("TestFoo");
    }

    @Preferences(testClassSuffixes="Test", testSrcFolder="test")
    @Project(mainCls="com:Foo", testCls="org:FooTest;org:FooTestNG", mainSrcFolder="src", testSrcFolder="test")
    @Test
    public void getMatches_should_find_matches_when_package_name_differs() throws CoreException
    {
        TestCaseDiviner testCaseDiviner = new TestCaseDiviner(context.getCompilationUnit("com.Foo"), org.moreunit.preferences.Preferences.getInstance());
        Set<IType> result = testCaseDiviner.getMatches();
        
        assertThat(result).onProperty("elementName").containsOnly("FooTest");
    }

    /**
     * Test for #2881409 (Switching in enums)
     */
    @Preferences(testClassSuffixes="Test", testSrcFolder="test")
    @Project(mainCls="com: enum SomeEnum", mainSrcFolder="src", testSrcFolder="test")
    @Test
    public void getSource_should_not_throw_exception_for_enums() throws CoreException 
    {
        TestCaseDiviner testCaseDiviner = new TestCaseDiviner(context.getCompilationUnit("com.SomeEnum"), org.moreunit.preferences.Preferences.getInstance());
        assertThat(testCaseDiviner.getSource()).isNotNull();
    }
}
