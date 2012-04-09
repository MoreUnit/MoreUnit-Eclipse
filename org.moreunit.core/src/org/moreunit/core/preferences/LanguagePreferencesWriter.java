package org.moreunit.core.preferences;

public class LanguagePreferencesWriter extends LanguagePreferences
{
    public LanguagePreferencesWriter(String languageId, WriteablePreferences parentPreferences)
    {
        super(languageId, parentPreferences);
    }

    public String getFileWordSeparator()
    {
        return getString(LanguagePreferences.FILE_WORD_SEPARATOR);
    }

    public void setFileWordSeparator(String separator)
    {
        setValue(FILE_WORD_SEPARATOR, separator);
    }

    public String getTestFileNameTemplate()
    {
        return getString(LanguagePreferences.TEST_FILE_NAME_TEMPLATE);
    }

    public void setTestFileNameTemplate(String template)
    {
        setValue(TEST_FILE_NAME_TEMPLATE, template);
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
