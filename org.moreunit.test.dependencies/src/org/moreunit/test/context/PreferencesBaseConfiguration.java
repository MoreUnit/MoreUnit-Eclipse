package org.moreunit.test.context;


class PreferencesBaseConfiguration
{
    private String testPackagePrefix;
    private String testPackageSuffix;
    private String testSuperClass;
    private TestType testType;
    private boolean extendedMethodSearch;
    private boolean flexibleNaming;
    private boolean methodSearchByName;
    private boolean testMethodPrefix;
    private String testClassNameTemplate;

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

    public boolean isMethodSearchByName()
    {
        return methodSearchByName;
    }

    public void setMethodSearchByName(boolean methodSearchByName)
    {
        this.methodSearchByName = methodSearchByName;
    }

    public boolean isTestMethodPrefix()
    {
        return testMethodPrefix;
    }

    public void setMethodPrefix(boolean testMethodPrefix)
    {
        this.testMethodPrefix = testMethodPrefix;
    }

    public void setTestMethodPrefix(boolean testMethodPrefix)
    {
        this.testMethodPrefix = testMethodPrefix;
    }

    public String getTestClassNameTemplate()
    {
        return testClassNameTemplate;
    }

    public void setTestClassNameTemplate(String testClassNameTemplate)
    {
        this.testClassNameTemplate = testClassNameTemplate;
    }
}
