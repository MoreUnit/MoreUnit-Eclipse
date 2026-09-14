package org.moreunit.core.preferences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.moreunit.core.languages.Language;
import org.moreunit.core.log.Logger;

public class PreferencesTest
{
    private IPreferenceStore store;
    private Logger logger;
    private Preferences preferences;

    @BeforeEach
    public void setUp()
    {
        store = mock(IPreferenceStore.class);
        logger = mock(Logger.class);
        when(store.getString(anyString())).thenReturn("");
        preferences = new Preferences(store, logger);
    }

    @Test
    public void should_return_project_preferences_for_project()
    {
        final IProject project = mock(IProject.class);

        assertNotNull(preferences.get(project));
    }

    @Test
    public void should_read_languages_from_store()
    {
        when(store.getString(Preferences.BASE + "languages")).thenReturn("java:Java,py:Python");

        final List<Language> languages = preferences.getLanguages();

        assertEquals(2, languages.size());
        assertEquals(new Language("java", "Java"), languages.get(0));
        assertEquals(new Language("py", "Python"), languages.get(1));
    }

    @Test
    public void should_add_language()
    {
        when(store.getString(Preferences.BASE + "languages")).thenReturn("java:Java");
        final Language pyLang = new Language("py", "Python");

        preferences.add(pyLang);

        verify(store).setValue(Preferences.BASE + "languages", "java:Java,py:Python");
    }

    @Test
    public void should_remove_language()
    {
        when(store.getString(Preferences.BASE + "languages")).thenReturn("java:Java,py:Python");
        final Language pyLang = new Language("py", "Python");

        preferences.remove(pyLang);

        verify(store).setValue(Preferences.BASE + "languages", "java:Java");
    }

    @Test
    public void should_tell_whether_language_has_preferences()
    {
        when(store.getString(Preferences.BASE + "languages")).thenReturn("java:Java,py:Python");

        assertTrue(preferences.hasPreferencesForLanguage("java"));
        assertFalse(preferences.hasPreferencesForLanguage("ruby"));
    }

    @Test
    public void should_cache_reader_per_language_and_fall_back_to_any_language()
    {
        when(store.getString(Preferences.BASE + "languages")).thenReturn("java:Java");

        final LanguagePreferencesReader reader = preferences.readerForLanguage("java");

        assertNotNull(reader);
        assertSame(reader, preferences.readerForLanguage("java"));
        assertSame(preferences.readerForAnyLanguage(), preferences.readerForLanguage("ruby"));
    }

    @Test
    public void should_cache_writer_per_language()
    {
        final LanguagePreferencesWriter writer = preferences.writerForLanguage("java");

        assertNotNull(writer);
        assertSame(writer, preferences.writerForLanguage("java"));
    }
}
