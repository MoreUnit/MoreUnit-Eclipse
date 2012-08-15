package org.moreunit.matching;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Collection;

import org.eclipse.jdt.core.IType;
import org.junit.Test;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.Project;

/**
 * @author giana 13.05.2006 13:49:29
 */
@Preferences(testClassSuffixes = "Test", testSrcFolder = "test")
public class CorrespondingTypeSearcherTest extends ContextTestCase
{
    @Preferences(testClassSuffixes = "Test", testSrcFolder = "test")
    @Project(mainCls = "Foo", testCls = "FooTest; FooTestNG")
    @Test
    public void getMatches_should_return_class_which_matches_suffix() throws Exception
    {
        CorrespondingTypeSearcher testCaseDiviner = new CorrespondingTypeSearcher(context.getCompilationUnit("Foo"), getPreferences());
        Collection<IType> matches = testCaseDiviner.getMatches(false);

        assertThat(matches).hasSize(1);
        assertThat(matches.iterator().next().getElementName()).isEqualTo("FooTest");
    }

    @Preferences(testClassSuffixes = "Test,TestNG", testSrcFolder = "test")
    @Project(mainCls = "Foo", testCls = "FooTest; FooTestNG")
    @Test
    public void getMatches_should_find_all_tests_which_match_all_suffixes() throws Exception
    {
        CorrespondingTypeSearcher testCaseDiviner = new CorrespondingTypeSearcher(context.getCompilationUnit("Foo"), getPreferences());
        Collection<IType> matches = testCaseDiviner.getMatches(false);

        assertThat(matches).hasSize(2).onProperty("elementName").contains("FooTest", "FooTestNG");
    }

    @Preferences(testClassPrefixes = "Test", testSrcFolder = "test")
    @Project(mainCls = "Foo", testCls = "TestFoo; BFooTest")
    @Test
    public void getMatches_should_return_class_which_matches_prefix() throws Exception
    {
        CorrespondingTypeSearcher testCaseDiviner = new CorrespondingTypeSearcher(context.getCompilationUnit("Foo"), getPreferences());
        Collection<IType> matches = testCaseDiviner.getMatches(false);

        assertThat(matches).hasSize(1);
        assertThat(matches.iterator().next().getElementName()).isEqualTo("TestFoo");
    }

    @Project(mainCls = "com:Foo", testCls = "org:FooTest; com:FooTest")
    @Test
    public void getMatches_should_find_matches_when_package_name_differs_if_so_requested() throws Exception
    {
        CorrespondingTypeSearcher testCaseDiviner = new CorrespondingTypeSearcher(context.getCompilationUnit("com.Foo"), getPreferences());

        IType perfectMatch = context.getPrimaryTypeHandler("com.FooTest").get();
        IType likelyMatch = context.getPrimaryTypeHandler("org.FooTest").get();

        Collection<IType> matches = testCaseDiviner.getMatches(false);
        assertThat(matches).containsOnly(perfectMatch);

        matches = testCaseDiviner.getMatches(true);
        assertThat(matches).hasSize(2).contains(perfectMatch, likelyMatch);
    }

    // Test for #2881409 (Switching in enums)
    @Project(mainCls = "com: enum SomeEnum", testCls = "com:SomeEnumTest")
    @Test
    public void getSource_should_not_throw_exception_for_enums() throws Exception
    {
        CorrespondingTypeSearcher testCaseDiviner = new CorrespondingTypeSearcher(context.getCompilationUnit("com.SomeEnum"), getPreferences());
        Collection<IType> matches = testCaseDiviner.getMatches(false);

        assertThat(matches).hasSize(1);
        assertThat(matches.iterator().next().getElementName()).isEqualTo("SomeEnumTest");
    }
}
