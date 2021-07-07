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
    
    @Override
    public String getTestFileExt()
    {
        return getString(LanguagePreferences.TEST_FILE_EXT);
    }
    
    @Override
    public String getSrcFileExt()
    {
        return getString(LanguagePreferences.SRC_FILE_EXT);
    }     
    
    @Override
    public Boolean getExtEnable()
    {
        String enableString = getString(LanguagePreferences.EXT_ENABLE);        
        return enableString.equals(BOOL_TRUE);
    }    
    
    public void setExtEnable(Boolean enable)
    {
        String enableString = enable ? BOOL_TRUE : BOOL_FALSE;
        setValue(EXT_ENABLE, enableString);
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
    
    public void setFileExts(String src, String test)
    {
        setValue(SRC_FILE_EXT, src);
        setValue(TEST_FILE_EXT, test);
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