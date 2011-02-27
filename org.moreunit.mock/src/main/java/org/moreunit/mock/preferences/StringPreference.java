package org.moreunit.mock.preferences;

import org.eclipse.jface.preference.IPreferenceStore;

public class StringPreference extends Preference<String>
{
    public StringPreference(String name, String defaultValue, String... possibleValues)
    {
        super(name, defaultValue, possibleValues);
    }

    @Override
    protected void registerDefaultValue(IPreferenceStore store, String value)
    {
        store.setDefault(name, value);
    }
}
