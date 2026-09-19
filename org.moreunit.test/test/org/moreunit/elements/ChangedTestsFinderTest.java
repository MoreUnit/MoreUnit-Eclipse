package org.moreunit.elements;

import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.junit.jupiter.api.Test;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.Project;
import org.moreunit.test.workspace.SourceFolderHandler;

@Preferences(testClassNameTemplate = "${srcFile}Test", testSrcFolder = "test")
@Project(mainCls = "com:Foo", testCls = "com:FooTest")
public class ChangedTestsFinderTest extends ContextTestCase
{
    private final ChangedTestsFinder finder = new ChangedTestsFinder();

    @Test
    public void findTests_should_return_the_test_of_a_changed_class()
    {
        final ChangedTests result = finder.findTests(List.of(context.getCompilationUnit("com.Foo")));

        assertEquals(Set.of("com.FooTest"), namesOfTypes(result.getTests()));
        assertEquals(1, result.getChangedFileCount());
        assertTrue(result.getFilesWithoutTests().isEmpty());
    }

    @Test
    public void findTests_should_return_a_changed_test_itself()
    {
        final ChangedTests result = finder.findTests(List.of(context.getCompilationUnit("com.FooTest")));

        assertEquals(Set.of("com.FooTest"), namesOfTypes(result.getTests()));
    }

    @Test
    public void findTests_should_report_changed_classes_without_test()
    {
        final ICompilationUnit changedClassWithoutTest = context.getProjectHandler().getMainSrcFolderHandler().createClass("com.Bar").getCompilationUnit();

        final ChangedTests result = finder.findTests(List.of(changedClassWithoutTest));

        assertTrue(result.isEmpty());
        assertEquals(1, result.getChangedFileCount());
        assertEquals(Set.of("Bar.java"), namesOfCompilationUnits(result.getFilesWithoutTests()));
    }

    @Test
    public void findTests_should_ignore_changed_files_without_primary_type()
    {
        final ICompilationUnit packageInfo = context.getProjectHandler().getMainSrcFolderHandler().createCompilationUnit("com.package-info", "package com;\n").get();

        final ChangedTests result = finder.findTests(List.of(packageInfo));

        assertTrue(result.isEmpty());
        assertEquals(1, result.getChangedFileCount());
        assertTrue(result.getFilesWithoutTests().isEmpty());
    }

    @Test
    public void findTests_should_replace_abstract_test_cases_with_their_concrete_subclasses()
    {
        final SourceFolderHandler testFolder = context.getProjectHandler().getTestSrcFolderHandler();
        final ICompilationUnit abstractTest = testFolder.createCompilationUnit("com.AbstractFooTest", "package com;\npublic abstract class AbstractFooTest {}\n").get();
        testFolder.createCompilationUnit("com.ConcreteFooTest", "package com;\npublic class ConcreteFooTest extends AbstractFooTest {}\n");

        final ChangedTests result = finder.findTests(List.of(abstractTest));

        assertEquals(Set.of("com.ConcreteFooTest"), namesOfTypes(result.getTests()));
    }

    private static Set<String> namesOfTypes(Collection<IType> types)
    {
        return types.stream().map(IType::getFullyQualifiedName).collect(toSet());
    }

    private static Set<String> namesOfCompilationUnits(Collection<ICompilationUnit> units)
    {
        return units.stream().map(ICompilationUnit::getElementName).collect(toSet());
    }
}
