package org.moreunit.test.context;

class PreferencesConfiguration extends PreferencesBaseConfiguration
{
    private String testSourceFolder;

    public String getTestSourceFolder()
    {
        return testSourceFolder;
    }

    public void setTestSourceFolder(String testSourceFolder)
    {
        this.testSourceFolder = testSourceFolder;
    }
}
