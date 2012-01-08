package org.moreunit.test;

import org.moreunit.preferences.Preferences;

@SuppressWarnings("restriction")
public class DummyPreferencesForTesting extends Preferences
{
    public DummyPreferencesForTesting()
    {
        Preferences.setInstance(this);
    }
}
