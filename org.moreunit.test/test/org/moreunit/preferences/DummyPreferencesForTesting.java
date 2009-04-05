package org.moreunit.preferences;

public class DummyPreferencesForTesting extends Preferences
{
    public DummyPreferencesForTesting()
    {
        Preferences.setInstance(this);
    }
}
