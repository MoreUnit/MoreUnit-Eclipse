package org.moreunit.core.preferences;

import static org.moreunit.core.preferences.Preferences.orDefault;

import org.moreunit.core.matching.TestFolderPathPattern;

public class LanguagePreferencesReader extends LanguagePreferences
{
    private LanguagePreferencesReader defaults;

    public LanguagePreferencesReader(String languageId, LanguagePreferencesReader defaults, WriteablePreferences parentPreferences)
    {
        super(languageId, parentPreferences);
        this.defaults = defaults;
    }

    @Override
    public String getFileWordSeparator()
    {
        return orDefault(getString(LanguagePreferences.FILE_WORD_SEPARATOR), defaults.getFileWordSeparator());
    }

    @Override
    public String getTestFileNameTemplate()
    {
        return orDefault(getString(LanguagePreferences.TEST_FILE_NAME_TEMPLATE), defaults.getTestFileNameTemplate());
    }

    @Override
    public String getSrcFolderPathTemplate()
    {
        return orDefault(getString(LanguagePreferences.SRC_FOLDER_PATH_TEMPLATE), defaults.getSrcFolderPathTemplate());
    }

    @Override
    public String getTestFolderPathTemplate()
    {
        return orDefault(getString(LanguagePreferences.TEST_FOLDER_PATH_TEMPLATE), defaults.getTestFolderPathTemplate());
    }

    public TestFolderPathPattern getTestFolderPathPattern()
    {
        return new TestFolderPathPattern(getSrcFolderPathTemplate(), getTestFolderPathTemplate());
    }
}
