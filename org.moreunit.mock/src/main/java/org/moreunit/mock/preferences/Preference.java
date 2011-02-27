package org.moreunit.mock.preferences;

import static java.util.Arrays.asList;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.preference.IPreferenceStore;
import org.moreunit.mock.MoreUnitMockPlugin;

abstract class Preference<T>
{
    final String name;
    final T defaultValue;
    final Set<T> possibleValues;

    public Preference(String name, T defaultValue, T... possibleValues)
    {
        this.name = MoreUnitMockPlugin.PLUGIN_ID + "." + name;
        this.defaultValue = defaultValue;

        if(possibleValues.length == 0)
        {
            this.possibleValues = Collections.emptySet();
        }
        else
        {
            this.possibleValues = Collections.unmodifiableSet(new HashSet<T>(asList(possibleValues)));
            if(this.defaultValue != null && ! this.possibleValues.contains(this.defaultValue))
            {
                throw new IllegalArgumentException("Invalid default value");
            }
        }
    }

    public boolean isPossibleValue(T value)
    {
        return possibleValues.isEmpty() || possibleValues.contains(value);
    }

    public void registerDefaultValue(IPreferenceStore store)
    {
        if(defaultValue != null)
        {
            registerDefaultValue(store, defaultValue);
        }
    }

    protected abstract void registerDefaultValue(IPreferenceStore store, T value);
}
