package org.moreunit.mock.elements;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.eclipse.jdt.core.IJavaProject;
import org.junit.jupiter.api.Test;

public class NamingRulesTest
{
    @Test
    public void should_create_with_project()
    {
        final IJavaProject project = mock(IJavaProject.class);
        final NamingRules rules = new NamingRules(project);
        assertNotNull(rules);
    }

    @Test
    public void should_clean_field_name()
    {
        final IJavaProject project = mock(IJavaProject.class);
        final NamingRules rules = new NamingRules(project);
        // if NamingConventions fails, this returns the original name
        final String result = rules.cleanFieldName("myField");
        assertEquals("myField", result);
    }

    @Test
    public void should_decorate_field_name()
    {
        final IJavaProject project = mock(IJavaProject.class);
        final NamingRules rules = new NamingRules(project);

        final String result = rules.decorateFieldName("myField");

        assertNotNull(result);
        assertTrue(result.contains("myField"));
    }

    @Test
    public void should_clean_parameter_name()
    {
        final IJavaProject project = mock(IJavaProject.class);
        final NamingRules rules = new NamingRules(project);

        assertEquals("myParam", rules.cleanParameterName("myParam"));
    }
}
