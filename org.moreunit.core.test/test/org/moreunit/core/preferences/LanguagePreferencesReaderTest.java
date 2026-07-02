package org.moreunit.core.preferences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.moreunit.core.matching.TestFolderPathPattern;

public class LanguagePreferencesReaderTest
{
    private static final String LANG = "java";
    private static final String BASE = Preferences.BASE;

    private IPreferenceStore store;
    private WriteablePreferences parent;
    private LanguagePreferencesReader reader;

    @BeforeEach
    public void setUp()
    {
        store = mock(IPreferenceStore.class);
        parent = mock(WriteablePreferences.class);
        when(parent.getStore()).thenReturn(store);

        reader = new LanguagePreferencesReader(LANG, Preferences.DEFAULTS, parent);
    }

    @Test
    public void should_return_store_value_when_defined()
    {
        // given
        when(store.getString(BASE + LANG + LanguagePreferences.FILE_WORD_SEPARATOR)).thenReturn("_");

        // then
        assertEquals("_", reader.getFileWordSeparator());
    }

    @Test
    public void should_fall_back_to_defaults_when_store_value_is_empty()
    {
        // given
        when(store.getString(BASE + LANG + LanguagePreferences.FILE_WORD_SEPARATOR)).thenReturn("");
        when(store.getString(BASE + LANG + LanguagePreferences.TEST_FILE_NAME_TEMPLATE)).thenReturn("");

        // then
        assertEquals(Preferences.DEFAULTS.getFileWordSeparator(), reader.getFileWordSeparator());
        assertEquals(Preferences.DEFAULTS.getTestFileNameTemplate(), reader.getTestFileNameTemplate());
    }

    @Test
    public void should_fall_back_to_defaults_when_store_value_is_null()
    {
        // given
        when(store.getString(BASE + LANG + LanguagePreferences.SRC_FOLDER_PATH_TEMPLATE)).thenReturn(null);

        // then
        assertEquals(Preferences.DEFAULTS.getSrcFolderPathTemplate(), reader.getSrcFolderPathTemplate());
    }

    @Test
    public void should_return_configured_test_folder_path_templates()
    {
        // given
        when(store.getString(BASE + LANG + LanguagePreferences.SRC_FOLDER_PATH_TEMPLATE)).thenReturn("src/${srcProject}");
        when(store.getString(BASE + LANG + LanguagePreferences.TEST_FOLDER_PATH_TEMPLATE)).thenReturn("test/${srcProject}");

        // then
        assertEquals("src/${srcProject}", reader.getSrcFolderPathTemplate());
        assertEquals("test/${srcProject}", reader.getTestFolderPathTemplate());
    }

    @Test
    public void should_build_test_folder_path_pattern_from_templates()
    {
        // given: empty values fall back to valid defaults
        when(store.getString(BASE + LANG + LanguagePreferences.SRC_FOLDER_PATH_TEMPLATE)).thenReturn("");
        when(store.getString(BASE + LANG + LanguagePreferences.TEST_FOLDER_PATH_TEMPLATE)).thenReturn("");

        // when
        TestFolderPathPattern pattern = reader.getTestFolderPathPattern();

        // then
        assertNotNull(pattern);
    }
}
