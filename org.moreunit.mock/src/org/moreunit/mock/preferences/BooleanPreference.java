package org.moreunit.mock.preferences;

import org.eclipse.jface.preference.IPreferenceStore;

public class BooleanPreference extends Preference<Boolean>
{
    public BooleanPreference(String name, Boolean defaultValue)
    {
        super(name, defaultValue, true, false);
    }

    @Override
    protected void registerDefaultValue(IPreferenceStore store, Boolean value)
    {
        store.setDefault(name, value);
    }
}
