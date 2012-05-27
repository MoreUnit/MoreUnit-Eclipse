package org.moreunit.core.preferences;

import static org.moreunit.core.preferences.Preferences.orDefault;

import org.moreunit.core.matching.CamelCaseNameTokenizer;
import org.moreunit.core.matching.NameTokenizer;
import org.moreunit.core.matching.SeparatorNameTokenizer;
import org.moreunit.core.matching.TestFileNamePattern;
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

    public TestFileNamePattern getTestFileNamePattern()
    {
        String separator = getFileWordSeparator();
        final NameTokenizer tokenizer;
        if(separator.length() == 0)
        {
            tokenizer = new CamelCaseNameTokenizer();
        }
        else
        {
            tokenizer = new SeparatorNameTokenizer(separator);
        }
        return new TestFileNamePattern(getTestFileNameTemplate(), tokenizer);
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
