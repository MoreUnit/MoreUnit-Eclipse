package org.moreunit.mock.dependencies;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.moreunit.mock.elements.NamingRules;
import org.moreunit.mock.model.Dependency;
import org.moreunit.mock.model.FieldDependency;
import org.moreunit.mock.model.SetterDependency;
import org.moreunit.mock.model.TypeParameter;

@Disabled
@ExtendWith(MockitoExtension.class)
public class DependenciesTest
{
    @Mock
    private NamingRules namingRules;
    @Mock
    private IType classUnderTest;
    @Mock
    private DependencyInjectionPointStore dependencyInjectionPointStore;

    private Dependencies dependencies;

    @BeforeEach
    public void createDependencies()
    {
        dependencies = new Dependencies(classUnderTest, dependencyInjectionPointStore, namingRules);
    }

    @Test
    public void resolveTypeSignature_should_return_type_fully_qualified_name() throws Exception
    {
        when(classUnderTest.resolveType("Comparator")).thenReturn(new String[][] { { "java.util", "Comparator" } });

        assertEquals(dependencies.resolveTypeSignature("Comparator<String>"), "java.util.Comparator");
    }

    @Test
    public void resolveTypeSignature_should_return_input_without_type_parameters_when_unresolved() throws Exception
    {
        when(classUnderTest.resolveType("Callable")).thenReturn(null);

        assertEquals(dependencies.resolveTypeSignature("Callable<String>"), "Callable");
    }

    @Test
    public void resolveTypeSignature_should_return_input_without_type_parameters_when_unresolved__complex_case() throws Exception
    {
        when(classUnderTest.resolveType("Callable")).thenReturn(null);

        assertEquals(dependencies.resolveTypeSignature("Callable<Map<String, List<Integer>>>"), "Callable");
    }

    // rationale: @NonNull etc. should probably not be put on test case fields
    @Test
    public void resolveTypeSignature_should_ignore_main_type_annotations() throws Exception
    {
        when(classUnderTest.resolveType("Comparator")).thenReturn(new String[][] { { "java.util", "Comparator" } });

        assertEquals(dependencies.resolveTypeSignature("@NonNull Comparator<String>"), "java.util.Comparator");
    }

    // TODO see what to do with array types (should not be mockable...)

    @Test
    public void resolveTypeParameters_should_return_an_empty_list_when_there_are_no_type_parameters() throws Exception
    {
        assertTrue(dependencies.resolveTypeParameters("String").isEmpty());
    }

    @Test
    public void resolveTypeParameters_should_return_one_parameter() throws Exception
    {
        when(classUnderTest.resolveType("String")).thenReturn(new String[][] { { "java.lang", "String" } });

        assertEquals(dependencies.resolveTypeParameters("Callable<String>"), asList(new TypeParameter("java.lang.String")));
    }

    @Test
    public void resolveTypeParameters_should_return_several_parameters() throws Exception
    {
        when(classUnderTest.resolveType("String")).thenReturn(new String[][] { { "java.lang", "String" } });
        when(classUnderTest.resolveType("Integer")).thenReturn(new String[][] { { "java.lang", "Integer" } });

        assertEquals(dependencies.resolveTypeParameters("Map<String, Integer>"), asList(new TypeParameter("java.lang.String"), new TypeParameter("java.lang.Integer")));
    }

    @Test
    public void resolveTypeParameters_should_return_nested_parameters() throws Exception
    {
        when(classUnderTest.resolveType("String")).thenReturn(new String[][] { { "java.lang", "String" } });
        when(classUnderTest.resolveType("List")).thenReturn(new String[][] { { "java.util", "List" } });
        when(classUnderTest.resolveType("Integer")).thenReturn(new String[][] { { "java.lang", "Integer" } });

        assertEquals(dependencies.resolveTypeParameters("Map<String, List<Integer>>"), asList(new TypeParameter("java.lang.String"),
                                  new TypeParameter("java.util.List").withTypeParameters(new TypeParameter("java.lang.Integer"))));
    }

    @Test
    public void resolveTypeParameters_should_return_several_nested_parameters() throws Exception
    {
        when(classUnderTest.resolveType("Set")).thenReturn(new String[][] { { "java.util", "Set" } });
        when(classUnderTest.resolveType("String")).thenReturn(new String[][] { { "java.lang", "String" } });
        when(classUnderTest.resolveType("List")).thenReturn(new String[][] { { "java.util", "List" } });
        when(classUnderTest.resolveType("Integer")).thenReturn(new String[][] { { "java.lang", "Integer" } });

        assertEquals(dependencies.resolveTypeParameters("Map<Set<String>, List<Integer>>"), asList(new TypeParameter("java.util.Set").withTypeParameters(new TypeParameter("java.lang.String")),
                                  new TypeParameter("java.util.List").withTypeParameters(new TypeParameter("java.lang.Integer"))));
    }

    @Test
    public void resolveTypeParameters_should_handle_wildcards() throws Exception
    {
        when(classUnderTest.resolveType("Set")).thenReturn(new String[][] { { "java.util", "Set" } });
        when(classUnderTest.resolveType("String")).thenReturn(new String[][] { { "java.lang", "String" } });

        assertEquals(new HashSet<>(Arrays.asList(TypeParameter.wildcard())), new HashSet<>((dependencies.resolveTypeParameters("Callable<?>"))));
        assertEquals(new HashSet<>(Arrays.asList(TypeParameter.extending("java.util.Set").withTypeParameters(new TypeParameter("java.lang.String")))), new HashSet<>((dependencies.resolveTypeParameters("Callable<? extends Set<String>"))));
        assertEquals(new HashSet<>(Arrays.asList(new TypeParameter("java.util.Set").withTypeParameters(TypeParameter.superOf("java.lang.String")))), new HashSet<>((dependencies.resolveTypeParameters("Callable<Set<? super String>"))));
    }

    @Test
    public void resolveTypeParameters_should_handle_type_annotations() throws Exception
    {
        when(classUnderTest.resolveType("Set")).thenReturn(new String[][] { { "java.util", "Set" } });
        when(classUnderTest.resolveType("Interned")).thenReturn(new String[][] { { "checkers.interning.quals", "Interned" } });
        when(classUnderTest.resolveType("ReadOnly")).thenReturn(new String[][] { { "checkers.interning.quals", "ReadOnly" } });

        assertEquals(new HashSet<>(Arrays.asList(new TypeParameter("java.util.Set")
                        .withAnnotations("checkers.interning.quals.Interned")
                        .withTypeParameters(TypeParameter.extending("java.lang.String")
                                .withBaseTypeAnnotations("English")
                                .withAnnotations("NonNull", "checkers.interning.quals.ReadOnly")
                        ))), new HashSet<>((dependencies.resolveTypeParameters("Callable<@Interned Set<@NonNull @ReadOnly ? extends @English java.lang.String>"))));
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
        assertEquals(dependencies.injectableByConstructor(), asList(new Dependency("x", "zzz"), new Dependency("x", "aaa"), new Dependency("x", "GGG")));
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
        assertEquals(dependencies.injectableByField(), asList(new FieldDependency("x", "mAaa", "aaa"), new FieldDependency("x", "fGGG", "GGG"), new FieldDependency("x", "zzz", "zzz")));
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
        assertEquals(dependencies.injectableBySetter(), asList(new SetterDependency("x", "setAaa"), new SetterDependency("x", "setGGG"), new SetterDependency("x", "setZzz")));
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
        assertEquals(new ArrayList<>(dependencies), asList(new Dependency("x", "aaa"), new SetterDependency("x", "setBbb"), new Dependency("x", "eee"),
                                                                             new Dependency("x", "GGG"), new Dependency("x", "HHH"), new SetterDependency("x", "setLLL"),
                                                                             new SetterDependency("x", "setOoo"), new Dependency("x", "yyy"), new Dependency("x", "zzz")));
    }
}
