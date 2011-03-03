package org.moreunit.mock.model;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DependenciesTest
{
    @Mock
    private IType classUnderTest;
    @Mock
    private IType testCase;
    private Dependencies dependencies;

    @Before
    public void createDependencies()
    {
        dependencies = new Dependencies(classUnderTest, testCase);
    }

    @Test
    public void should_return_an_empty_list_when_there_are_no_type_parameters() throws Exception
    {
        assertThat(dependencies.resolveTypeParameters("String")).isEmpty();
    }

    @Test
    public void should_return_one_parameter() throws Exception
    {
        when(classUnderTest.resolveType("String")).thenReturn(new String[][] { { "java.lang", "String" } });

        assertThat(dependencies.resolveTypeParameters("Callable<String>")).isEqualTo(asList(new TypeParameter("java.lang.String")));
    }

    @Test
    public void should_return_several_parameters() throws Exception
    {
        when(classUnderTest.resolveType("String")).thenReturn(new String[][] { { "java.lang", "String" } });
        when(classUnderTest.resolveType("Integer")).thenReturn(new String[][] { { "java.lang", "Integer" } });

        assertThat(dependencies.resolveTypeParameters("Map<String, Integer>"))
                .isEqualTo(asList(new TypeParameter("java.lang.String"), new TypeParameter("java.lang.Integer")));
    }

    @Test
    public void should_return_nested_parameters() throws Exception
    {
        when(classUnderTest.resolveType("String")).thenReturn(new String[][] { { "java.lang", "String" } });
        when(classUnderTest.resolveType("List")).thenReturn(new String[][] { { "java.util", "List" } });
        when(classUnderTest.resolveType("Integer")).thenReturn(new String[][] { { "java.lang", "Integer" } });

        assertThat(dependencies.resolveTypeParameters("Map<String, List<Integer>>"))
                .isEqualTo(asList(new TypeParameter("java.lang.String"),
                                  new TypeParameter("java.util.List", asList(new TypeParameter("java.lang.Integer")))));
    }

    @Test
    public void should_return_several_nested_parameters() throws Exception
    {
        when(classUnderTest.resolveType("Set")).thenReturn(new String[][] { { "java.util", "Set" } });
        when(classUnderTest.resolveType("String")).thenReturn(new String[][] { { "java.lang", "String" } });
        when(classUnderTest.resolveType("List")).thenReturn(new String[][] { { "java.util", "List" } });
        when(classUnderTest.resolveType("Integer")).thenReturn(new String[][] { { "java.lang", "Integer" } });

        assertThat(dependencies.resolveTypeParameters("Map<Set<String>, List<Integer>>"))
                .isEqualTo(asList(new TypeParameter("java.util.Set", asList(new TypeParameter("java.lang.String"))),
                                  new TypeParameter("java.util.List", asList(new TypeParameter("java.lang.Integer")))));
    }

    @Test
    public void should_not_sort_constructor_dependencies_after_computation() throws Exception
    {
        // given
        mockMethodsAndFieldsRetrieval(classUnderTest);
        dependencies.constructorDependencies.addAll(asList(new Dependency("x", "zzz"), new Dependency("x", "aaa"), new Dependency("x", "GGG")));

        // when
        dependencies.compute();

        // then
        assertThat(dependencies.constructorDependencies).isEqualTo(asList(new Dependency("x", "zzz"), new Dependency("x", "aaa"), new Dependency("x", "GGG")));
    }

    private void mockMethodsAndFieldsRetrieval(IType type) throws JavaModelException
    {
        when(type.getMethods()).thenReturn(new IMethod[0]);
        when(type.getFields()).thenReturn(new IField[0]);

        ITypeHierarchy typeHierarchy = mock(ITypeHierarchy.class);
        when(type.newSupertypeHierarchy(any(IProgressMonitor.class))).thenReturn(typeHierarchy);
        when(typeHierarchy.getAllClasses()).thenReturn(new IType[0]);
    }

    @Test
    public void should_sort_field_dependencies_after_computation() throws Exception
    {
        // given
        mockMethodsAndFieldsRetrieval(classUnderTest);
        dependencies.fieldDependencies.addAll(asList(new Dependency("x", "zzz"), new Dependency("x", "aaa"), new Dependency("x", "GGG")));

        // when
        dependencies.compute();

        // then
        assertThat(dependencies.fieldDependencies).isEqualTo(asList(new Dependency("x", "aaa"), new Dependency("x", "GGG"), new Dependency("x", "zzz")));
    }

    @Test
    public void should_sort_setter_dependencies_after_computation() throws Exception
    {
        // given
        mockMethodsAndFieldsRetrieval(classUnderTest);
        dependencies.setterDependencies.addAll(asList(new SetterDependency("x", "setZzz"), new SetterDependency("x", "setAaa"), new SetterDependency("x", "setGGG")));

        // when
        dependencies.compute();

        // then
        assertThat(dependencies.setterDependencies).isEqualTo(asList(new SetterDependency("x", "setAaa"), new SetterDependency("x", "setGGG"), new SetterDependency("x", "setZzz")));
    }

    @Test
    public void should_sort_all_dependencies_after_computation() throws Exception
    {
        // given
        mockMethodsAndFieldsRetrieval(classUnderTest);
        dependencies.addAll(asList(new Dependency("x", "zzz"), new Dependency("x", "aaa"), new Dependency("x", "GGG"),
                                   new SetterDependency("x", "setLLL"), new SetterDependency("x", "setBbb"), new SetterDependency("x", "setOoo"),
                                   new Dependency("x", "yyy"), new Dependency("x", "eee"), new Dependency("x", "HHH")));

        // when
        dependencies.compute();

        // then
        assertThat(new ArrayList<Dependency>(dependencies)).isEqualTo(asList(new Dependency("x", "aaa"), new SetterDependency("x", "setBbb"), new Dependency("x", "eee"),
                                                                             new Dependency("x", "GGG"), new Dependency("x", "HHH"), new SetterDependency("x", "setLLL"),
                                                                             new SetterDependency("x", "setOoo"), new Dependency("x", "yyy"), new Dependency("x", "zzz")));
    }
}
