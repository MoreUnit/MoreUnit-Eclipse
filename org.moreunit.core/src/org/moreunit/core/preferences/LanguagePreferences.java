package org.moreunit.core.preferences;

abstract class LanguagePreferences
{
    private static final String BASE = Preferences.BASE;

    public static final String ANY_LANGUAGE = "anyLanguage";
    public static final String FILE_WORD_SEPARATOR = ".fileWordSeparator";
    public static final String SRC_FOLDER_PATH_TEMPLATE = ".srcFolderPathTemplate";
    public static final String TEST_FILE_NAME_TEMPLATE = ".testFileNameTemplate";
    public static final String TEST_FOLDER_PATH_TEMPLATE = ".testFolderPathTemplate";

    protected String languageId;
    protected WriteablePreferences parentPreferences;

    protected LanguagePreferences(String languageId, WriteablePreferences parentPreferences)
    {
        this.languageId = languageId;
        this.parentPreferences = parentPreferences;
    }

    public abstract String getFileWordSeparator();

    public abstract String getSrcFolderPathTemplate();

    public abstract String getTestFileNameTemplate();

    public abstract String getTestFolderPathTemplate();

    protected String getString(String name)
    {
        return parentPreferences.getStore().getString(BASE + languageId + name);
    }

    protected void setValue(String name, String value)
    {
        parentPreferences.getStore().setValue(BASE + languageId + name, value);
    }

    public void save()
    {
        parentPreferences.save();
    }
}
