package org.moreunit.mock.elements;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import org.eclipse.jdt.core.IJavaProject;
import org.junit.jupiter.api.Test;

public class NamingRulesTest
{
    @Test
    public void should_create_with_project()
    {
        IJavaProject project = mock(IJavaProject.class);
        NamingRules rules = new NamingRules(project);
        assertNotNull(rules);
    }

    @Test
    public void should_clean_field_name()
    {
        IJavaProject project = mock(IJavaProject.class);
        NamingRules rules = new NamingRules(project);
        // if NamingConventions fails, this returns the original name
        String result = rules.cleanFieldName("myField");
        assertEquals("myField", result);
    }
}
