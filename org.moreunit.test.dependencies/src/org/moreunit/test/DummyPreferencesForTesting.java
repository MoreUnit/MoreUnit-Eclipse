package org.moreunit.test;

import org.moreunit.preferences.Preferences;

public class DummyPreferencesForTesting extends Preferences
{
    public DummyPreferencesForTesting()
    {
        Preferences.setInstance(this);
    }
}
