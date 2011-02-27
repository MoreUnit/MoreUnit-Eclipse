package org.moreunit.mock.model;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.eclipse.jdt.core.IType;
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
}
