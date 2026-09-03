package org.moreunit.mock.preferences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.jupiter.api.Test;
import org.moreunit.mock.MoreUnitMockPlugin;

public class PreferenceTest
{
    private static String prefixed(String name)
    {
        return MoreUnitMockPlugin.PLUGIN_ID + "." + name;
    }

    @Test
    public void should_prefix_preference_name_with_plugin_id()
    {
        // given
        final BooleanPreference pref = new BooleanPreference("myPref", true);

        // then
        assertEquals(prefixed("myPref"), pref.name);
        assertEquals(true, pref.defaultValue);
    }

    @Test
    public void boolean_preference_should_accept_both_boolean_values()
    {
        // given
        final BooleanPreference pref = new BooleanPreference("myPref", true);

        // then
        assertTrue(pref.isPossibleValue(true));
        assertTrue(pref.isPossibleValue(false));
    }

    @Test
    public void boolean_preference_should_register_default_value_in_store()
    {
        // given
        final BooleanPreference pref = new BooleanPreference("myPref", true);
        final IPreferenceStore store = mock(IPreferenceStore.class);

        // when
        pref.registerDefaultValue(store);

        // then
        verify(store).setDefault(prefixed("myPref"), true);
    }

    @Test
    public void string_preference_without_possible_values_should_accept_any_value()
    {
        // given
        final StringPreference pref = new StringPreference("myPref", "default");

        // then
        assertTrue(pref.isPossibleValue("anything"));
        assertTrue(pref.isPossibleValue("default"));
    }

    @Test
    public void string_preference_should_register_default_value_in_store()
    {
        // given
        final StringPreference pref = new StringPreference("myPref", "default");
        final IPreferenceStore store = mock(IPreferenceStore.class);

        // when
        pref.registerDefaultValue(store);

        // then
        verify(store).setDefault(prefixed("myPref"), "default");
    }

    @Test
    public void string_preference_with_possible_values_should_only_accept_these_values()
    {
        // given
        final StringPreference pref = new StringPreference("myPref", "a", "a", "b");

        // then
        assertTrue(pref.isPossibleValue("a"));
        assertTrue(pref.isPossibleValue("b"));
        assertFalse(pref.isPossibleValue("c"));
    }

    @Test
    public void should_reject_default_value_not_part_of_possible_values()
    {
        // then
        assertThrows(IllegalArgumentException.class, () -> new StringPreference("myPref", "default", "a", "b"));
    }

    @Test
    public void should_not_register_default_value_when_none_is_defined()
    {
        // given
        final BooleanPreference pref = new BooleanPreference("myPref", null);
        final IPreferenceStore store = mock(IPreferenceStore.class);

        // when
        pref.registerDefaultValue(store);

        // then
        verify(store, never()).setDefault(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyBoolean());
    }
}
