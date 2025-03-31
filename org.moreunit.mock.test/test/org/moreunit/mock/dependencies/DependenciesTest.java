package org.moreunit.mock.dependencies;

import static java.util.Arrays.asList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.moreunit.mock.elements.NamingRules;
import org.moreunit.mock.model.Dependency;
import org.moreunit.mock.model.FieldDependency;
import org.moreunit.mock.model.SetterDependency;
import org.moreunit.mock.model.TypeParameter;

@RunWith(MockitoJUnitRunner.class)
public class DependenciesTest
{
    @Mock
    private NamingRules namingRules;
    @Mock
    private IType classUnderTest;
    @Mock
    private DependencyInjectionPointStore dependencyInjectionPointStore;

    private Dependencies dependencies;

    @Before
    public void createDependencies()
    {
        dependencies = new Dependencies(classUnderTest, dependencyInjectionPointStore, namingRules);
    }

    @Test
    public void resolveTypeSignature_should_return_type_fully_qualified_name() throws Exception
    {
        when(classUnderTest.resolveType("Comparator")).thenReturn(new String[][] { { "java.util", "Comparator" } });

        assertThat(dependencies.resolveTypeSignature("Comparator<String>")).isEqualTo("java.util.Comparator");
    }

    @Test
    public void resolveTypeSignature_should_return_input_without_type_parameters_when_unresolved() throws Exception
    {
        when(classUnderTest.resolveType("Callable")).thenReturn(null);

        assertThat(dependencies.resolveTypeSignature("Callable<String>")).isEqualTo("Callable");
    }

    @Test
    public void resolveTypeSignature_should_return_input_without_type_parameters_when_unresolved__complex_case() throws Exception
    {
        when(classUnderTest.resolveType("Callable")).thenReturn(null);

        assertThat(dependencies.resolveTypeSignature("Callable<Map<String, List<Integer>>>")).isEqualTo("Callable");
    }

    // rationale: @NonNull etc. should probably not be put on test case fields
    @Test
    public void resolveTypeSignature_should_ignore_main_type_annotations() throws Exception
    {
        when(classUnderTest.resolveType("Comparator")).thenReturn(new String[][] { { "java.util", "Comparator" } });

        assertThat(dependencies.resolveTypeSignature("@NonNull Comparator<String>")).isEqualTo("java.util.Comparator");
    }

    // TODO see what to do with array types (should not be mockable...)

    @Test
    public void resolveTypeParameters_should_return_an_empty_list_when_there_are_no_type_parameters() throws Exception
    {
        assertThat(dependencies.resolveTypeParameters("String")).isEmpty();
    }

    @Test
    public void resolveTypeParameters_should_return_one_parameter() throws Exception
    {
        when(classUnderTest.resolveType("String")).thenReturn(new String[][] { { "java.lang", "String" } });

        assertThat(dependencies.resolveTypeParameters("Callable<String>")).isEqualTo(asList(new TypeParameter("java.lang.String")));
    }

    @Test
    public void resolveTypeParameters_should_return_several_parameters() throws Exception
    {
        when(classUnderTest.resolveType("String")).thenReturn(new String[][] { { "java.lang", "String" } });
        when(classUnderTest.resolveType("Integer")).thenReturn(new String[][] { { "java.lang", "Integer" } });

        assertThat(dependencies.resolveTypeParameters("Map<String, Integer>"))
                .isEqualTo(asList(new TypeParameter("java.lang.String"), new TypeParameter("java.lang.Integer")));
    }

    @Test
    public void resolveTypeParameters_should_return_nested_parameters() throws Exception
    {
        when(classUnderTest.resolveType("String")).thenReturn(new String[][] { { "java.lang", "String" } });
        when(classUnderTest.resolveType("List")).thenReturn(new String[][] { { "java.util", "List" } });
        when(classUnderTest.resolveType("Integer")).thenReturn(new String[][] { { "java.lang", "Integer" } });

        assertThat(dependencies.resolveTypeParameters("Map<String, List<Integer>>"))
                .isEqualTo(asList(new TypeParameter("java.lang.String"),
                                  new TypeParameter("java.util.List").withTypeParameters(new TypeParameter("java.lang.Integer"))));
    }

    @Test
    public void resolveTypeParameters_should_return_several_nested_parameters() throws Exception
    {
        when(classUnderTest.resolveType("Set")).thenReturn(new String[][] { { "java.util", "Set" } });
        when(classUnderTest.resolveType("String")).thenReturn(new String[][] { { "java.lang", "String" } });
        when(classUnderTest.resolveType("List")).thenReturn(new String[][] { { "java.util", "List" } });
        when(classUnderTest.resolveType("Integer")).thenReturn(new String[][] { { "java.lang", "Integer" } });

        assertThat(dependencies.resolveTypeParameters("Map<Set<String>, List<Integer>>"))
                .isEqualTo(asList(new TypeParameter("java.util.Set").withTypeParameters(new TypeParameter("java.lang.String")),
                                  new TypeParameter("java.util.List").withTypeParameters(new TypeParameter("java.lang.Integer"))));
    }

    @Test
    public void resolveTypeParameters_should_handle_wildcards() throws Exception
    {
        when(classUnderTest.resolveType("Set")).thenReturn(new String[][] { { "java.util", "Set" } });
        when(classUnderTest.resolveType("String")).thenReturn(new String[][] { { "java.lang", "String" } });

        assertThat(dependencies.resolveTypeParameters("Callable<?>")).containsOnly(TypeParameter.wildcard());
        assertThat(dependencies.resolveTypeParameters("Callable<? extends Set<String>")).containsOnly(TypeParameter.extending("java.util.Set").withTypeParameters(new TypeParameter("java.lang.String")));
        assertThat(dependencies.resolveTypeParameters("Callable<Set<? super String>")).containsOnly(new TypeParameter("java.util.Set").withTypeParameters(TypeParameter.superOf("java.lang.String")));
    }

    @Test
    public void resolveTypeParameters_should_handle_type_annotations() throws Exception
    {
        when(classUnderTest.resolveType("Set")).thenReturn(new String[][] { { "java.util", "Set" } });
        when(classUnderTest.resolveType("Interned")).thenReturn(new String[][] { { "checkers.interning.quals", "Interned" } });
        when(classUnderTest.resolveType("ReadOnly")).thenReturn(new String[][] { { "checkers.interning.quals", "ReadOnly" } });

        assertThat(dependencies.resolveTypeParameters("Callable<@Interned Set<@NonNull @ReadOnly ? extends @English java.lang.String>"))
                .containsOnly(new TypeParameter("java.util.Set")
                        .withAnnotations("checkers.interning.quals.Interned")
                        .withTypeParameters(TypeParameter.extending("java.lang.String")
                                .withBaseTypeAnnotations("English")
                                .withAnnotations("NonNull", "checkers.interning.quals.ReadOnly")
                        ));
    }

    @Test
    public void should_not_sort_constructor_dependencies_after_init() throws Exception
    {
        // given
        mockMethodsAndFieldsRetrieval(classUnderTest);
        dependencies.injectableByConstructor().addAll(asList(new Dependency("x", "zzz"), new Dependency("x", "aaa"), new Dependency("x", "GGG")));

        // when
        dependencies.init();

        // then
        assertThat(dependencies.injectableByConstructor()).isEqualTo(asList(new Dependency("x", "zzz"), new Dependency("x", "aaa"), new Dependency("x", "GGG")));
    }

    private void mockMethodsAndFieldsRetrieval(IType type) throws JavaModelException
    {
        when(dependencyInjectionPointStore.getConstructors()).thenReturn(Collections.<IMethod> emptySet());
        when(dependencyInjectionPointStore.getFields()).thenReturn(Collections.<IField> emptySet());
        when(dependencyInjectionPointStore.getSetters()).thenReturn(Collections.<IMethod> emptySet());
    }

    @Test
    public void should_sort_field_dependencies_after_init() throws Exception
    {
        // given
        mockMethodsAndFieldsRetrieval(classUnderTest);
        dependencies.injectableByField().addAll(asList(new FieldDependency("x", "zzz", "zzz"), new FieldDependency("x", "mAaa", "aaa"), new FieldDependency("x", "fGGG", "GGG")));

        // when
        dependencies.init();

        // then
        assertThat(dependencies.injectableByField()).isEqualTo(asList(new FieldDependency("x", "mAaa", "aaa"), new FieldDependency("x", "fGGG", "GGG"), new FieldDependency("x", "zzz", "zzz")));
    }

    @Test
    public void should_sort_setter_dependencies_after_init() throws Exception
    {
        // given
        mockMethodsAndFieldsRetrieval(classUnderTest);
        dependencies.injectableBySetter().addAll(asList(new SetterDependency("x", "setZzz"), new SetterDependency("x", "setAaa"), new SetterDependency("x", "setGGG")));

        // when
        dependencies.init();

        // then
        assertThat(dependencies.injectableBySetter()).isEqualTo(asList(new SetterDependency("x", "setAaa"), new SetterDependency("x", "setGGG"), new SetterDependency("x", "setZzz")));
    }

    @Test
    public void should_sort_all_dependencies_after_init() throws Exception
    {
        // given
        mockMethodsAndFieldsRetrieval(classUnderTest);
        dependencies.addAll(asList(new Dependency("x", "zzz"), new Dependency("x", "aaa"), new Dependency("x", "GGG"),
                                   new SetterDependency("x", "setLLL"), new SetterDependency("x", "setBbb"), new SetterDependency("x", "setOoo"),
                                   new Dependency("x", "yyy"), new Dependency("x", "eee"), new Dependency("x", "HHH")));

        // when
        dependencies.init();

        // then
        assertThat(new ArrayList<Dependency>(dependencies)).isEqualTo(asList(new Dependency("x", "aaa"), new SetterDependency("x", "setBbb"), new Dependency("x", "eee"),
                                                                             new Dependency("x", "GGG"), new Dependency("x", "HHH"), new SetterDependency("x", "setLLL"),
                                                                             new SetterDependency("x", "setOoo"), new Dependency("x", "yyy"), new Dependency("x", "zzz")));
    }
}
