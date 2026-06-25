package org.moreunit.util;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.moreunit.preferences.PreferenceConstants;
import org.moreunit.preferences.Preferences;

public class TestMethodDivinerFactoryTest
{
    private ICompilationUnit compilationUnit;
    private IJavaProject javaProject;
    private Preferences preferences;
    private MockedStatic<Preferences> mockedPreferences;

    @BeforeEach
    public void setUp()
    {
        compilationUnit = mock(ICompilationUnit.class);
        javaProject = mock(IJavaProject.class);
        when(compilationUnit.getJavaProject()).thenReturn(javaProject);

        preferences = mock(Preferences.class);
        mockedPreferences = Mockito.mockStatic(Preferences.class);
        mockedPreferences.when(Preferences::getInstance).thenReturn(preferences);
    }

    @AfterEach
    public void tearDown()
    {
        mockedPreferences.close();
    }

    @Test
    public void create_should_return_NoPraefix_when_preference_is_no_prefix()
    {
        when(preferences.getTestMethodType(javaProject)).thenReturn(PreferenceConstants.TEST_METHOD_TYPE_NO_PREFIX);

        TestMethodDivinerFactory factory = new TestMethodDivinerFactory(compilationUnit);
        TestMethodDiviner diviner = factory.create();

        assertNotNull(diviner);
        assertInstanceOf(TestMethodDivinerNoPraefix.class, diviner);
    }

    @Test
    public void create_should_return_Junit3Praefix_when_preference_is_not_no_prefix()
    {
        when(preferences.getTestMethodType(javaProject)).thenReturn(PreferenceConstants.TEST_METHOD_TYPE_JUNIT3);

        TestMethodDivinerFactory factory = new TestMethodDivinerFactory(compilationUnit);
        TestMethodDiviner diviner = factory.create();

        assertNotNull(diviner);
        assertInstanceOf(TestMethodDivinerJunit3Praefix.class, diviner);
    }

    @Test
    public void create_with_type_should_return_Junit3Praefix_for_junit3()
    {
        TestMethodDivinerFactory factory = new TestMethodDivinerFactory(compilationUnit);
        TestMethodDiviner diviner = factory.create(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_3);

        assertNotNull(diviner);
        assertInstanceOf(TestMethodDivinerJunit3Praefix.class, diviner);
    }

    @Test
    public void create_with_type_should_delegate_to_create_for_non_junit3()
    {
        when(preferences.getTestMethodType(javaProject)).thenReturn(PreferenceConstants.TEST_METHOD_TYPE_NO_PREFIX);

        TestMethodDivinerFactory factory = new TestMethodDivinerFactory(compilationUnit);
        TestMethodDiviner diviner = factory.create(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4);

        assertNotNull(diviner);
        assertInstanceOf(TestMethodDivinerNoPraefix.class, diviner);
    }
}
