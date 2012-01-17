package org.moreunit.test.context;

class PropertiesConfiguration extends PreferencesBaseConfiguration
{
    private boolean hasUserSetProperties;

    public boolean hasUserSetProperties()
    {
        return hasUserSetProperties;
    }

    public void setUserSetProperties(boolean hasUserSetProperties)
    {
        this.hasUserSetProperties = hasUserSetProperties;
    }
}
