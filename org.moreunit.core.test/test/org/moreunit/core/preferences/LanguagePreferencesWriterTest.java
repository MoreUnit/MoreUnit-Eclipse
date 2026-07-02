package org.moreunit.core.preferences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LanguagePreferencesWriterTest
{
    private static final String LANG = "java";
    private static final String BASE = Preferences.BASE;

    private IPreferenceStore store;
    private WriteablePreferences parent;
    private LanguagePreferencesWriter writer;

    @BeforeEach
    public void setUp()
    {
        store = mock(IPreferenceStore.class);
        parent = mock(WriteablePreferences.class);
        when(parent.getStore()).thenReturn(store);

        writer = new LanguagePreferencesWriter(LANG, parent);
    }

    @Test
    public void should_read_values_from_store_using_qualified_keys()
    {
        // given
        when(store.getString(BASE + LANG + LanguagePreferences.FILE_WORD_SEPARATOR)).thenReturn("_");
        when(store.getString(BASE + LANG + LanguagePreferences.TEST_FILE_NAME_TEMPLATE)).thenReturn("${srcFile}Test");
        when(store.getString(BASE + LANG + LanguagePreferences.SRC_FOLDER_PATH_TEMPLATE)).thenReturn("src");
        when(store.getString(BASE + LANG + LanguagePreferences.TEST_FOLDER_PATH_TEMPLATE)).thenReturn("test");

        // then
        assertEquals("_", writer.getFileWordSeparator());
        assertEquals("${srcFile}Test", writer.getTestFileNameTemplate());
        assertEquals("src", writer.getSrcFolderPathTemplate());
        assertEquals("test", writer.getTestFolderPathTemplate());
    }

    @Test
    public void should_write_test_file_name_template_and_separator()
    {
        // when
        writer.setTestFileNameTemplate("${srcFile}Test", "_");

        // then
        verify(store).setValue(BASE + LANG + LanguagePreferences.TEST_FILE_NAME_TEMPLATE, "${srcFile}Test");
        verify(store).setValue(BASE + LANG + LanguagePreferences.FILE_WORD_SEPARATOR, "_");
    }

    @Test
    public void should_write_test_folder_path_templates()
    {
        // when
        writer.setTestFolderPathTemplate("src/${srcProject}", "test/${srcProject}");

        // then
        verify(store).setValue(BASE + LANG + LanguagePreferences.SRC_FOLDER_PATH_TEMPLATE, "src/${srcProject}");
        verify(store).setValue(BASE + LANG + LanguagePreferences.TEST_FOLDER_PATH_TEMPLATE, "test/${srcProject}");
    }

    @Test
    public void should_delegate_is_active_to_parent_preferences()
    {
        // given
        when(parent.hasPreferencesForLanguage(LANG)).thenReturn(true);

        // then
        assertTrue(writer.isActive());

        // given
        when(parent.hasPreferencesForLanguage(LANG)).thenReturn(false);

        // then
        assertFalse(writer.isActive());
    }

    @Test
    public void should_delegate_set_active_to_parent_preferences()
    {
        // when
        writer.setActive(true);

        // then
        verify(parent).activatePreferencesForLanguage(LANG, true);
    }
}
