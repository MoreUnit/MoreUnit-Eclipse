package org.moreunit.core.preferences;

public interface ReadablePreferences
{
    boolean hasPreferencesForLanguage(String language);

    LanguagePreferencesReader readerForLanguage(String language);
}
