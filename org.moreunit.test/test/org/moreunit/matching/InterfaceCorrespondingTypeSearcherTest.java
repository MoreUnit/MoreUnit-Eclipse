package org.moreunit.matching;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.jupiter.api.Test;
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

        assertEquals(1, matches.size());
        assertEquals("FooTest", matches.iterator().next().getElementName());
    }

    @Project(mainCls = "interface Foo", testCls = "FooTest")
    @Test
    public void getMatches_should_return_test_for_interface()
    {
        context.getPrimaryTypeHandler("Foo").createSubclass("FooImpl");

        CorrespondingTypeSearcher searcher = new CorrespondingTypeSearcher(context.getCompilationUnit("Foo"), getPreferences());
        Collection<IType> matches = searcher.getMatches(false);

        assertEquals(1, matches.size());
        assertEquals("FooTest", matches.iterator().next().getElementName());
    }

    @Project(mainCls = "interface Foo", testCls = "FooTest")
    @Test
    public void getMatches_should_return_implementation_for_interface_test_and_exclude_interface()
    {
        context.getPrimaryTypeHandler("Foo").createSubclass("FooImpl");

        CorrespondingTypeSearcher searcher = new CorrespondingTypeSearcher(context.getCompilationUnit("FooTest"), getPreferences());
        Collection<IType> matches = searcher.getMatches(false);

        // Should return FooImpl and EXCLUDE Foo because Foo is a pure interface
        assertEquals(1, matches.size());
        assertEquals("FooImpl", matches.iterator().next().getElementName());
    }

    @Project(mainCls = "interface Foo", testCls = "FooTest")
    @Test
    public void getMatches_should_return_interface_test_for_implementation()
    {
        context.getPrimaryTypeHandler("Foo").createSubclass("FooImpl");

        CorrespondingTypeSearcher searcher = new CorrespondingTypeSearcher(context.getCompilationUnit("FooImpl"), getPreferences());
        Collection<IType> matches = searcher.getMatches(false);

        boolean found = false;
        for (IType t : matches) {
            if ("FooTest".equals(t.getElementName())) {
                found = true;
                break;
            }
        }
        assertTrue(found, "expected FooTest to be in matches");
    }

    @Project(mainCls = "interface Foo")
    @Test
    public void getMatches_should_return_interface_for_implementation_test()
    {
        context.getPrimaryTypeHandler("Foo").createSubclass("FooImpl");
        context.getProjectHandler().getTestSrcFolderHandler().createClass("FooImplTest");

        CorrespondingTypeSearcher searcher = new CorrespondingTypeSearcher(context.getCompilationUnit("FooImplTest"), getPreferences());
        Collection<IType> matches = searcher.getMatches(false);

        assertEquals(1, matches.size());
        assertEquals("FooImpl", matches.iterator().next().getElementName());
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
        java.util.Set<String> names = new java.util.HashSet<>();
        for (IType t : matches) {
            names.add(t.getElementName());
        }
        assertEquals(java.util.Arrays.asList("Foo", "FooImpl"), new java.util.ArrayList<>(names));
    }
}
