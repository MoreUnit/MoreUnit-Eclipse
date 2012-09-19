package org.moreunit.core.preferences;

public class LanguagePreferencesWriter extends LanguagePreferences implements TestFileNamePatternPreferencesWriter
{
    public LanguagePreferencesWriter(String languageId, WriteablePreferences parentPreferences)
    {
        super(languageId, parentPreferences);
    }

    @Override
    public String getFileWordSeparator()
    {
        return getString(LanguagePreferences.FILE_WORD_SEPARATOR);
    }

    @Override
    public String getSrcFolderPathTemplate()
    {
        return getString(LanguagePreferences.SRC_FOLDER_PATH_TEMPLATE);
    }

    @Override
    public String getTestFileNameTemplate()
    {
        return getString(LanguagePreferences.TEST_FILE_NAME_TEMPLATE);
    }

    @Override
    public String getTestFolderPathTemplate()
    {
        return getString(LanguagePreferences.TEST_FOLDER_PATH_TEMPLATE);
    }

    public void setTestFileNameTemplate(String template, String separator)
    {
        setValue(TEST_FILE_NAME_TEMPLATE, template);
        setValue(FILE_WORD_SEPARATOR, separator);
    }

    public void setTestFolderPathTemplate(String srcTemplate, String testTemplate)
    {
        setValue(SRC_FOLDER_PATH_TEMPLATE, srcTemplate);
        setValue(TEST_FOLDER_PATH_TEMPLATE, testTemplate);
    }

    public boolean isActive()
    {
        return parentPreferences.hasPreferencesForLanguage(languageId);
    }

    public void setActive(boolean active)
    {
        parentPreferences.activatePreferencesForLanguage(languageId, active);
    }
}
