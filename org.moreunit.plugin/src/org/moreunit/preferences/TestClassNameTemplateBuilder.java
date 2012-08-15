package org.moreunit.preferences;

import org.moreunit.core.matching.TestFileNamePattern;
import org.moreunit.core.util.Strings;

public class TestClassNameTemplateBuilder
{
    public String buildFromSettings(String[] prefixes, String[] suffixes, boolean flexibleNaming)
    {
        StringBuilder sb = new StringBuilder();

        if(prefixes.length != 0)
        {
            appendOrExpression(sb, prefixes);
            appendWildcardIfFlexibleNaming(sb, flexibleNaming);
        }

        sb.append(TestFileNamePattern.SRC_FILE_VARIABLE);

        if(suffixes.length != 0)
        {
            appendWildcardIfFlexibleNaming(sb, flexibleNaming);
            appendOrExpression(sb, suffixes);
        }

        return sb.toString();
    }

    private void appendWildcardIfFlexibleNaming(StringBuilder sb, boolean flexibleNaming)
    {
        if(flexibleNaming)
        {
            sb.append("*");
        }
    }

    private void appendOrExpression(StringBuilder sb, String[] parts)
    {
        if(parts.length > 1)
        {
            sb.append("(");
            Strings.join(sb, "|", parts);
            sb.append(")");
        }
        else
        {
            sb.append(parts[0]);
        }
    }
}
