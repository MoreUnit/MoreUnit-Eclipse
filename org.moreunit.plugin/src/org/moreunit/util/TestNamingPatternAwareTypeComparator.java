package org.moreunit.util;

import java.text.Collator;
import java.util.Comparator;

import org.eclipse.jdt.core.IType;
import org.moreunit.preferences.Preferences;

public class TestNamingPatternAwareTypeComparator implements Comparator<IType>
{
    private final Preferences preferences;

    public TestNamingPatternAwareTypeComparator(Preferences preferences)
    {
        this.preferences = preferences;
    }

    public int compare(IType type1, IType type2)
    {
        String type1Name = getNameWithoutTestPrefixAndSufixIfAny(type1);
        String type2Name = getNameWithoutTestPrefixAndSufixIfAny(type2);
        return Collator.getInstance().compare(type1Name, type2Name);
    }

    private String getNameWithoutTestPrefixAndSufixIfAny(IType type)
    {
        String testName = type.getElementName();

        String[] prefixes = preferences.getPrefixes(type.getJavaProject());
        testName = removeTestPrefix(testName, prefixes);

        String[] suffixes = preferences.getSuffixes(type.getJavaProject());
        testName = removeTestSuffix(testName, suffixes);

        return testName;
    }

    private String removeTestPrefix(String className, String[] prefixes)
    {
        for (String prefix : prefixes)
        {
            if((prefix.length() > 0) && className.startsWith(prefix))
            {
                return className.substring(prefix.length());
            }
        }

        return className;
    }

    private String removeTestSuffix(String className, String[] suffixes)
    {
        for (String suffix : suffixes)
        {
            if((suffix.length() > 0) && className.endsWith(suffix))
            {
                return className.substring(0, className.length() - suffix.length());
            }
        }

        return className;
    }
}
