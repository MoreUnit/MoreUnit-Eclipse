package org.moreunit.matching;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.Test;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.Project;

@Preferences(testClassNameTemplate = "${srcFile}Test", testSrcFolder = "test")
public class InterfaceCorrespondingTypeSearcherTest extends ContextTestCase
{
    @Project(mainCls = "class Foo", testCls = "FooTest")
    @Test
    public void getMatches_should_return_test_for_class()
    {
        CorrespondingTypeSearcher searcher = new CorrespondingTypeSearcher(context.getCompilationUnit("Foo"), getPreferences());
        Collection<IType> matches = searcher.getMatches(false);

        assertThat(matches).extracting("elementName").containsExactly("FooTest");
    }

    @Project(mainCls = "interface Foo", testCls = "FooTest")
    @Test
    public void getMatches_should_return_test_for_interface()
    {
        context.getPrimaryTypeHandler("Foo").createSubclass("FooImpl");

        CorrespondingTypeSearcher searcher = new CorrespondingTypeSearcher(context.getCompilationUnit("Foo"), getPreferences());
        Collection<IType> matches = searcher.getMatches(false);

        assertThat(matches).extracting("elementName").containsExactly("FooTest");
    }

    @Project(mainCls = "interface Foo", testCls = "FooTest")
    @Test
    public void getMatches_should_return_implementation_for_interface_test_and_exclude_interface()
    {
        context.getPrimaryTypeHandler("Foo").createSubclass("FooImpl");

        CorrespondingTypeSearcher searcher = new CorrespondingTypeSearcher(context.getCompilationUnit("FooTest"), getPreferences());
        Collection<IType> matches = searcher.getMatches(false);

        // Should return FooImpl and EXCLUDE Foo because Foo is a pure interface
        assertThat(matches).extracting("elementName").containsExactly("FooImpl");
    }

    @Project(mainCls = "interface Foo", testCls = "FooTest")
    @Test
    public void getMatches_should_return_interface_test_for_implementation()
    {
        context.getPrimaryTypeHandler("Foo").createSubclass("FooImpl");

        CorrespondingTypeSearcher searcher = new CorrespondingTypeSearcher(context.getCompilationUnit("FooImpl"), getPreferences());
        Collection<IType> matches = searcher.getMatches(false);

        assertThat(matches).extracting("elementName").contains("FooTest");
    }

    @Project(mainCls = "interface Foo")
    @Test
    public void getMatches_should_return_interface_for_implementation_test()
    {
        context.getPrimaryTypeHandler("Foo").createSubclass("FooImpl");
        context.getProjectHandler().getTestSrcFolderHandler().createClass("FooImplTest");

        CorrespondingTypeSearcher searcher = new CorrespondingTypeSearcher(context.getCompilationUnit("FooImplTest"), getPreferences());
        Collection<IType> matches = searcher.getMatches(false);

        assertThat(matches).extracting("elementName").containsExactly("FooImpl");
    }

    @Project(mainCls = "interface Foo", testCls = "FooTest")
    @Test
    public void getMatches_should_include_interface_if_it_has_default_method() throws JavaModelException
    {
        IType foo = context.getPrimaryTypeHandler("Foo").get();
        foo.createMethod("default void bar() {}", null, true, null);
        context.getPrimaryTypeHandler("Foo").createSubclass("FooImpl");

        CorrespondingTypeSearcher searcher = new CorrespondingTypeSearcher(context.getCompilationUnit("FooTest"), getPreferences());
        Collection<IType> matches = searcher.getMatches(false);

        // Should return both Foo and FooImpl because Foo has a default method
        assertThat(matches).extracting("elementName").containsExactlyInAnyOrder("Foo", "FooImpl");
    }
}
