package org.moreunit.core.preferences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.junit.jupiter.api.Test;
import org.moreunit.core.log.Logger;

public class ProjectPreferencesTest
{
    private static final String LANGUAGES_KEY = "org.moreunit.core.languages";
    private static final String ANY_LANGUAGE_ACTIVE_KEY = "org.moreunit.core.anyLanguage.active";

    @Test
    public void should_remove_language_from_list()
    {
        // ⚡ Bolt Performance Optimization Verification
        // Test edge cases for replacing replaceFirst(",?\\b%s\\b,?")

        // Remove from middle (the bug in the original regex)
        assertEquals(ProjectPreferences.removeLanguage("java,python,cpp", "python"), "java,cpp");

        // Remove from start
        assertEquals(ProjectPreferences.removeLanguage("python,java", "python"), "java");

        // Remove from end
        assertEquals(ProjectPreferences.removeLanguage("java,python", "python"), "java");

        // Remove exact match
        assertEquals(ProjectPreferences.removeLanguage("python", "python"), "");

        // Remove multiple matches (should only remove first to mimic replaceFirst)
        assertEquals(ProjectPreferences.removeLanguage("java,python,python,cpp", "python"), "java,python,cpp");

        // Remove when substring but not bounded
        assertEquals(ProjectPreferences.removeLanguage("java,python3,cpp", "python"), "java,python3,cpp");

        // Remove non-existent
        assertEquals(ProjectPreferences.removeLanguage("java,cpp", "python"), "java,cpp");
    }

    @Test
    public void should_check_language_in_list()
    {
        // ⚡ Bolt Performance Optimization Verification
        // Test edge cases for replacing String.matches(".*\\b%s\\b.*")

        // Match from middle
        assertTrue(ProjectPreferences.hasLanguage("java,python,cpp", "python"));

        // Match from start
        assertTrue(ProjectPreferences.hasLanguage("python,java", "python"));

        // Match from end
        assertTrue(ProjectPreferences.hasLanguage("java,python", "python"));

        // Match exact match
        assertTrue(ProjectPreferences.hasLanguage("python", "python"));

        // Reject when substring but not bounded (end)
        assertFalse(ProjectPreferences.hasLanguage("java,python3,cpp", "python"));

        // Reject when substring but not bounded (start)
        assertFalse(ProjectPreferences.hasLanguage("java,cpython,cpp", "python"));

        // Reject non-existent
        assertFalse(ProjectPreferences.hasLanguage("java,cpp", "python"));

        // Handle null/empty
        assertFalse(ProjectPreferences.hasLanguage("", "python"));
        assertFalse(ProjectPreferences.hasLanguage(null, "python"));
        assertFalse(ProjectPreferences.hasLanguage("python", null));
        assertFalse(ProjectPreferences.hasLanguage("python", ""));
    }

    @Test
    public void should_skip_unbounded_occurrence_when_searching_language()
    {
        // first occurrence of "python" is unbounded ("pythonX"), second one matches
        assertTrue(ProjectPreferences.hasLanguage("pythonX,python", "python"));
        assertFalse(ProjectPreferences.hasLanguage("pythonX,jython3", "python"));
    }

    @Test
    public void should_remove_bounded_occurrence_when_first_occurrence_is_unbounded()
    {
        // "python" at index 0 is followed by 'X' (no boundary), so the
        // second, bounded occurrence must be removed instead
        assertEquals("pythonX", ProjectPreferences.removeLanguage("pythonX,python", "python"));
    }

    @Test
    public void should_cache_writer_for_language()
    {
        final ProjectPreferences prefs = newPreferences(mock(ScopedPreferenceStore.class));

        assertSame(prefs.writerForLanguage("java"), prefs.writerForLanguage("java"));
    }

    @Test
    public void should_cache_reader_for_language()
    {
        final ScopedPreferenceStore store = mock(ScopedPreferenceStore.class);
        when(store.getString(LANGUAGES_KEY)).thenReturn("java");

        final Preferences wsPrefs = newWorkspacePreferences();
        when(wsPrefs.hasPreferencesForLanguage("java")).thenReturn(true);
        when(wsPrefs.readerForLanguage("java")).thenReturn(Preferences.DEFAULTS);

        final ProjectPreferences prefs = newPreferences(store, wsPrefs);

        assertSame(prefs.readerForLanguage("java"), prefs.readerForLanguage("java"));
    }

    @Test
    public void should_clear_cached_readers()
    {
        final ScopedPreferenceStore store = mock(ScopedPreferenceStore.class);
        when(store.getString(LANGUAGES_KEY)).thenReturn("java");

        final Preferences wsPrefs = newWorkspacePreferences();
        when(wsPrefs.hasPreferencesForLanguage("java")).thenReturn(true);
        when(wsPrefs.readerForLanguage("java")).thenReturn(Preferences.DEFAULTS);

        final ProjectPreferences prefs = newPreferences(store, wsPrefs);

        final LanguagePreferencesReader cached = prefs.readerForLanguage("java");
        prefs.clearCache();

        assertNotSame(cached, prefs.readerForLanguage("java"));
    }

    @Test
    public void should_activate_preferences_for_any_language()
    {
        final ScopedPreferenceStore store = mock(ScopedPreferenceStore.class);
        when(store.getBoolean(ANY_LANGUAGE_ACTIVE_KEY)).thenReturn(false);

        newPreferences(store).activatePreferencesForLanguage(LanguagePreferences.ANY_LANGUAGE, true);

        verify(store).setValue(ANY_LANGUAGE_ACTIVE_KEY, true);
    }

    @Test
    public void should_do_nothing_when_activation_state_is_unchanged()
    {
        final ScopedPreferenceStore store = mock(ScopedPreferenceStore.class);
        when(store.getBoolean(ANY_LANGUAGE_ACTIVE_KEY)).thenReturn(true);

        newPreferences(store).activatePreferencesForLanguage(LanguagePreferences.ANY_LANGUAGE, true);

        verify(store, never()).setValue(eq(ANY_LANGUAGE_ACTIVE_KEY), eq(true));
    }

    @Test
    public void should_add_language_when_activating_named_language()
    {
        final ScopedPreferenceStore store = mock(ScopedPreferenceStore.class);
        when(store.getString(LANGUAGES_KEY)).thenReturn("java");

        newPreferences(store).activatePreferencesForLanguage("python", true);

        verify(store).setValue(LANGUAGES_KEY, "java,python");
    }

    @Test
    public void should_remove_language_when_deactivating_named_language()
    {
        final ScopedPreferenceStore store = mock(ScopedPreferenceStore.class);
        when(store.getString(LANGUAGES_KEY)).thenReturn("java,python");

        newPreferences(store).activatePreferencesForLanguage("python", false);

        verify(store).setValue(LANGUAGES_KEY, "java");
    }

    @Test
    public void should_save_store() throws Exception
    {
        final ScopedPreferenceStore store = mock(ScopedPreferenceStore.class);

        newPreferences(store).save();

        verify(store).save();
    }

    @Test
    public void should_log_error_when_save_fails() throws Exception
    {
        final ScopedPreferenceStore store = mock(ScopedPreferenceStore.class);
        final IOException failure = new IOException("boom");
        doThrow(failure).when(store).save();

        final IProject project = mock(IProject.class);
        when(project.getName()).thenReturn("MyProject");

        final Logger logger = mock(Logger.class);

        new ProjectPreferences(project, store, newWorkspacePreferences(), logger).save();

        verify(logger).error(contains("MyProject"), eq(failure));
    }

    private ProjectPreferences newPreferences(ScopedPreferenceStore store)
    {
        return newPreferences(store, newWorkspacePreferences());
    }

    private ProjectPreferences newPreferences(ScopedPreferenceStore store, Preferences wsPrefs)
    {
        return new ProjectPreferences(mock(IProject.class), store, wsPrefs, mock(Logger.class));
    }

    private Preferences newWorkspacePreferences()
    {
        final Preferences wsPrefs = mock(Preferences.class);
        when(wsPrefs.readerForAnyLanguage()).thenReturn(Preferences.DEFAULTS);
        return wsPrefs;
    }
}
