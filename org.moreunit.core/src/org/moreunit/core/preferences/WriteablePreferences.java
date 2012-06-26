package org.moreunit.core.preferences;

import org.eclipse.jface.preference.IPreferenceStore;

public interface WriteablePreferences
{
    void activatePreferencesForLanguage(String language, boolean active);

    IPreferenceStore getStore();

    boolean hasPreferencesForLanguage(String language);

    LanguagePreferencesWriter writerForLanguage(String language);

    void save();
}
