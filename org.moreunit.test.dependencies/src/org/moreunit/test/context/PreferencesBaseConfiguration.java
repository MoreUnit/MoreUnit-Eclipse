package org.moreunit.test.context;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collections;
import java.util.List;

class PreferencesBaseConfiguration
{
    private final List<String> testClassPrefixes = newArrayList();
    private final List<String> testClassSuffixes = newArrayList();
    private String testPackagePrefix;
    private String testPackageSuffix;
    private String testSuperClass;
    private TestType testType;
    private boolean extendedMethodSearch;
    private boolean flexibleNaming;
    private boolean testMethodPrefix;

    public List<String> getTestClassSuffixes()
    {
        return testClassSuffixes;
    }

    public String[] getTestClassSuffixArray()
    {
        return testClassSuffixes.toArray(new String[testClassSuffixes.size()]);
    }

    public void setTestClassSuffixes(String[] suffixes)
    {
        if(suffixes != null)
        {
            Collections.addAll(testClassSuffixes, suffixes);
        }
    }

    public List<String> getTestClassPrefixes()
    {
        return testClassPrefixes;
    }

    public String[] getTestClassPrefixArray()
    {
        return testClassPrefixes.toArray(new String[testClassPrefixes.size()]);
    }

    public void setTestClassPrefixes(String[] prefixes)
    {
        if(prefixes != null)
        {
            Collections.addAll(testClassPrefixes, prefixes);
        }
    }

    public String getTestPackagePrefix()
    {
        return testPackagePrefix;
    }

    public void setTestPackagePrefix(String testPackagePrefix)
    {
        this.testPackagePrefix = testPackagePrefix;
    }

    public String getTestPackageSuffix()
    {
        return testPackageSuffix;
    }

    public void setTestPackageSuffix(String testPackageSuffix)
    {
        this.testPackageSuffix = testPackageSuffix;
    }

    public String getTestSuperClass()
    {
        return testSuperClass;
    }

    public void setTestSuperClass(String testSuperClass)
    {
        this.testSuperClass = testSuperClass;
    }

    public TestType getTestType()
    {
        return testType;
    }

    public void setTestType(TestType testType)
    {
        this.testType = testType;
    }

    public boolean isExtendedMethodSearch()
    {
        return extendedMethodSearch;
    }

    public void setExtendedMethodSearch(boolean extendedMethodSearch)
    {
        this.extendedMethodSearch = extendedMethodSearch;
    }

    public boolean isFlexibleNaming()
    {
        return flexibleNaming;
    }

    public void setFlexibleNaming(boolean flexibleNaming)
    {
        this.flexibleNaming = flexibleNaming;
    }

    public boolean isTestMethodPrefix()
    {
        return testMethodPrefix;
    }

    public void setMethodPrefix(boolean testMethodPrefix)
    {
        this.testMethodPrefix = testMethodPrefix;
    }
}
