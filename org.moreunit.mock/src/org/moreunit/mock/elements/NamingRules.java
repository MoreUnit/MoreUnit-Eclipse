package org.moreunit.mock.elements;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.NamingConventions;

public class NamingRules
{
    private final IJavaProject project;

    public NamingRules(IJavaProject project)
    {
        this.project = project;
    }

    public String cleanFieldName(String fieldName)
    {
        String baseName = NamingConventions.getBaseName(NamingConventions.VK_INSTANCE_FIELD, fieldName, project);
        return baseName != null ? baseName : fieldName;
    }

    public String cleanParameterName(String parameterName)
    {
        String baseName = NamingConventions.getBaseName(NamingConventions.VK_PARAMETER, parameterName, project);
        return baseName != null ? baseName : parameterName;
    }

    public String decorateFieldName(String fieldName)
    {
        String[] suggestions = NamingConventions.suggestVariableNames(NamingConventions.VK_INSTANCE_FIELD, NamingConventions.BK_NAME, fieldName, project, 0, null, true);
        return suggestions != null && suggestions.length != 0 ? suggestions[0] : fieldName;
    }
}
