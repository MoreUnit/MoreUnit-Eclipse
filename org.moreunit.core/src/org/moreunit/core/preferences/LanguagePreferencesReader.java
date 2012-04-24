package org.moreunit.core.preferences;

import static org.moreunit.core.preferences.Preferences.orDefault;

public class LanguagePreferencesReader extends LanguagePreferences
{
    private LanguagePreferencesReader defaults;

    public LanguagePreferencesReader(String languageId, LanguagePreferencesReader defaults, WriteablePreferences parentPreferences)
    {
        super(languageId, parentPreferences);
        this.defaults = defaults;
    }

    @Override
    protected String getFileWordSeparator()
    {
        return orDefault(getString(LanguagePreferences.FILE_WORD_SEPARATOR), defaults.getFileWordSeparator());
    }

    @Override
    protected String getTestFileNameTemplate()
    {
        return orDefault(getString(LanguagePreferences.TEST_FILE_NAME_TEMPLATE), defaults.getTestFileNameTemplate());
    }
}
