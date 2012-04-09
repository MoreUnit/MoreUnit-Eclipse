package org.moreunit.core.preferences;

import org.moreunit.core.matching.CamelCaseNameTokenizer;
import org.moreunit.core.matching.NameTokenizer;
import org.moreunit.core.matching.SeparatorNameTokenizer;
import org.moreunit.core.matching.TestFileNamePattern;

abstract class LanguagePreferences
{
    private static final String BASE = Preferences.BASE;

    public static final String ANY_LANGUAGE = "anyLanguage";
    public static final String FILE_WORD_SEPARATOR = ".fileWordSeparator";
    public static final String TEST_FILE_NAME_TEMPLATE = ".testFileNameTemplate";

    protected String languageId;
    protected WriteablePreferences parentPreferences;

    protected LanguagePreferences(String languageId, WriteablePreferences parentPreferences)
    {
        this.languageId = languageId;
        this.parentPreferences = parentPreferences;
    }

    public abstract String getFileWordSeparator();

    public abstract String getTestFileNameTemplate();

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
