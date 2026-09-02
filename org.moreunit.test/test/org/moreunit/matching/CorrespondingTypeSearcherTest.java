package org.moreunit.matching;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.jdt.core.IType;
import org.junit.jupiter.api.Test;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.workspace.TypeHandler;
import org.moreunit.test.context.Project;

/**
 * @author giana 13.05.2006 13:49:29
 */
@Preferences(testClassNameTemplate = "${srcFile}Test", testSrcFolder = "test")
public class CorrespondingTypeSearcherTest extends ContextTestCase
{
    @Preferences(testClassNameTemplate = "${srcFile}Test", testSrcFolder = "test")
    @Project(mainCls = "Foo", testCls = "FooTest; FooTestNG")
    @Test
    public void getMatches_should_return_class_which_matches_suffix() throws Exception
    {
        CorrespondingTypeSearcher testCaseDiviner = new CorrespondingTypeSearcher(context.getCompilationUnit("Foo"), getPreferences());
        Collection<IType> matches = testCaseDiviner.getMatches(false);

        assertEquals(1, matches.size());
        assertEquals(matches.iterator().next().getElementName(), "FooTest");
    }

    @Preferences(testClassNameTemplate = "${srcFile}(Test|TestNG)", testSrcFolder = "test")
    @Project(mainCls = "Foo", testCls = "FooTest; FooTestNG")
    @Test
    public void getMatches_should_find_all_tests_which_match_all_suffixes() throws Exception
    {
        CorrespondingTypeSearcher testCaseDiviner = new CorrespondingTypeSearcher(context.getCompilationUnit("Foo"), getPreferences());
        Collection<IType> matches = testCaseDiviner.getMatches(false);

        assertEquals(2, matches.size());
    }

    @Preferences(testClassNameTemplate = "Test${srcFile}", testSrcFolder = "test")
    @Project(mainCls = "Foo", testCls = "TestFoo; BFooTest")
    @Test
    public void getMatches_should_return_class_which_matches_prefix() throws Exception
    {
        CorrespondingTypeSearcher testCaseDiviner = new CorrespondingTypeSearcher(context.getCompilationUnit("Foo"), getPreferences());
        Collection<IType> matches = testCaseDiviner.getMatches(false);

        assertEquals(1, matches.size());
        assertEquals(matches.iterator().next().getElementName(), "TestFoo");
    }

    @Project(mainCls = "com:Foo", testCls = "org:FooTest; com:FooTest")
    @Test
    public void getMatches_should_find_matches_when_package_name_differs_if_so_requested() throws Exception
    {
        CorrespondingTypeSearcher testCaseDiviner = new CorrespondingTypeSearcher(context.getCompilationUnit("com.Foo"), getPreferences());

        IType perfectMatch = context.getPrimaryTypeHandler("com.FooTest").get();
        IType likelyMatch = context.getPrimaryTypeHandler("org.FooTest").get();

        Collection<IType> matches = testCaseDiviner.getMatches(false);
        assertEquals(new java.util.HashSet<>(Arrays.asList(perfectMatch)), new java.util.HashSet<>((matches)));

        matches = testCaseDiviner.getMatches(true);
        assertEquals(2, matches.size());
    }

    // Test for #2881409 (Switching in enums)
    @Project(mainCls = "com: enum SomeEnum", testCls = "com:SomeEnumTest")
    @Test
    public void getSource_should_not_throw_exception_for_enums() throws Exception
    {
        CorrespondingTypeSearcher testCaseDiviner = new CorrespondingTypeSearcher(context.getCompilationUnit("com.SomeEnum"), getPreferences());
        Collection<IType> matches = testCaseDiviner.getMatches(false);

        assertEquals(1, matches.size());
        assertEquals(matches.iterator().next().getElementName(), "SomeEnumTest");
    }

    @Project(mainCls = "Foo")
    @Test
    public void getMatches_should_find_test_of_concrete_implementation_when_type_is_interface() throws Exception
    {
        TypeHandler interfaceHandler = context.getPrimaryTypeHandler("Foo");
        interfaceHandler.get().getCompilationUnit().getBuffer().setContents("public interface Foo {}");
        interfaceHandler.get().getCompilationUnit().save(null, true);
        interfaceHandler.createSubclass("FooImpl");
        context.getProjectHandler().getTestSrcFolderHandler().createCompilationUnit("FooImplTest", "public class FooImplTest {}");

        CorrespondingTypeSearcher searcher = new CorrespondingTypeSearcher(context.getCompilationUnit("Foo"), getPreferences());
        Collection<IType> matches = searcher.getMatches(false);

        assertEquals(1, matches.size());
        assertEquals("FooImplTest", matches.iterator().next().getElementName());
    }

    @Project(mainCls = "AbsFoo")
    @Test
    public void getMatches_should_find_test_of_concrete_implementation_when_type_is_abstract() throws Exception
    {
        TypeHandler abstractHandler = context.getPrimaryTypeHandler("AbsFoo");
        abstractHandler.get().getCompilationUnit().getBuffer().setContents("public abstract class AbsFoo {}");
        abstractHandler.get().getCompilationUnit().save(null, true);
        abstractHandler.createSubclass("AbsFooImpl");
        context.getProjectHandler().getTestSrcFolderHandler().createCompilationUnit("AbsFooImplTest", "public class AbsFooImplTest {}");

        CorrespondingTypeSearcher searcher = new CorrespondingTypeSearcher(context.getCompilationUnit("AbsFoo"), getPreferences());
        Collection<IType> matches = searcher.getMatches(false);

        assertEquals(1, matches.size());
        assertEquals("AbsFooImplTest", matches.iterator().next().getElementName());
    }

    @Project(mainCls = "Foo", testCls = "FooTest; FooImplTest")
    @Test
    public void getMatches_should_replace_interface_without_implementation_by_its_concrete_implementation() throws Exception
    {
        TypeHandler interfaceHandler = context.getPrimaryTypeHandler("Foo");
        interfaceHandler.get().getCompilationUnit().getBuffer().setContents("public interface Foo {}");
        interfaceHandler.get().getCompilationUnit().save(null, true);
        interfaceHandler.createSubclass("FooImpl");

        CorrespondingTypeSearcher searcher = new CorrespondingTypeSearcher(context.getCompilationUnit("FooTest"), getPreferences());
        Collection<IType> matches = searcher.getMatches(false);

        assertEquals(1, matches.size());
        assertEquals("FooImpl", matches.iterator().next().getElementName());
    }

    @Project(mainCls = "Foo", testCls = "FooTest; FooImplTest")
    @Test
    public void getMatches_should_keep_interface_having_a_default_method() throws Exception
    {
        TypeHandler interfaceHandler = context.getPrimaryTypeHandler("Foo");
        interfaceHandler.get().getCompilationUnit().getBuffer().setContents("public interface Foo { default int size() { return 0; } }");
        interfaceHandler.get().getCompilationUnit().save(null, true);
        interfaceHandler.createSubclass("FooImpl");

        CorrespondingTypeSearcher searcher = new CorrespondingTypeSearcher(context.getCompilationUnit("FooTest"), getPreferences());
        Collection<IType> matches = searcher.getMatches(false);

        assertEquals(2, matches.size());
    }

    @Project(mainCls = "Foo2")
    @Test
    public void getMatches_should_cache_matches_per_likely_flag() throws Exception
    {
        TypeHandler interfaceHandler = context.getPrimaryTypeHandler("Foo2");
        interfaceHandler.get().getCompilationUnit().getBuffer().setContents("public interface Foo2 {}");
        interfaceHandler.get().getCompilationUnit().save(null, true);
        interfaceHandler.createSubclass("Foo2Impl");
        context.getProjectHandler().getTestSrcFolderHandler().createCompilationUnit("Foo2ImplTest", "public class Foo2ImplTest {}");

        CorrespondingTypeSearcher searcher = new CorrespondingTypeSearcher(context.getCompilationUnit("Foo2"), getPreferences());
        Collection<IType> perfectMatches = searcher.getMatches(false);
        // second call must return the cached result
        Collection<IType> perfectMatchesAgain = searcher.getMatches(false);
        Collection<IType> likelyMatches = searcher.getMatches(true);
        Collection<IType> likelyMatchesAgain = searcher.getMatches(true);

        assertEquals(perfectMatches, perfectMatchesAgain);
        assertEquals(1, perfectMatches.size());
        assertEquals(likelyMatches, likelyMatchesAgain);
        assertEquals(1, likelyMatches.size());
    }
}
