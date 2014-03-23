package org.moreunit.core.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.moreunit.core.matching.TestFileNamePattern;

public abstract class LanguagePreferences
{
    public static final String ANY_LANGUAGE = "anyLanguage";
    public static final String FILE_WORD_SEPARATOR = ".fileWordSeparator";
    public static final String SRC_FOLDER_PATH_TEMPLATE = ".srcFolderPathTemplate";
    public static final String TEST_FILE_NAME_TEMPLATE = ".testFileNameTemplate";
    public static final String TEST_FOLDER_PATH_TEMPLATE = ".testFolderPathTemplate";

    private static final String BASE = Preferences.BASE;

    /**
     * Cache for TestFileNamePattern instances.
     * <p>
     * It is OK to clear it entirely every time a preference changes ; that way
     * we can guarantee that entries won't be kept in memory without reason.
     */
    private static final Map<String, TestFileNamePattern> FILE_NAME_PATTERN_CACHE = new HashMap<String, TestFileNamePattern>();
    private static final Object CACHE_LOCK = new Object();

    protected String languageId;
    protected WriteablePreferences parentPreferences;

    public LanguagePreferences(String languageId, WriteablePreferences parentPreferences)
    {
        this.languageId = languageId;
        this.parentPreferences = parentPreferences;
    }

    public abstract String getFileWordSeparator();

    public abstract String getSrcFolderPathTemplate();

    public abstract String getTestFileNameTemplate();

    public abstract String getTestFolderPathTemplate();

    public TestFileNamePattern getTestFileNamePattern()
    {
        synchronized (CACHE_LOCK)
        {
            String template = getTestFileNameTemplate();
            String separator = getFileWordSeparator();
            String key = (template == null ? "" : template.length() + template) //
                         + (separator == null ? "" : separator.length() + separator);

            TestFileNamePattern pattern = FILE_NAME_PATTERN_CACHE.get(key);
            if(pattern == null)
            {
                pattern = new TestFileNamePattern(template, separator);
                FILE_NAME_PATTERN_CACHE.put(key, pattern);
            }

            return pattern;
        }
    }

    protected String getString(String name)
    {
        return getStore().getString(BASE + languageId + name);
    }

    protected IPreferenceStore getStore()
    {
        return parentPreferences.getStore();
    }

    public void setValue(String name, String value)
    {
        synchronized (CACHE_LOCK)
        {
            FILE_NAME_PATTERN_CACHE.clear();
            getStore().setValue(BASE + languageId + name, value);
        }
    }

    public void save()
    {
        parentPreferences.save();
    }
}
