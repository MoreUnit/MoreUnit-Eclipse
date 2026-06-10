package org.moreunit.core.preferences;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.eq;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.moreunit.core.log.Logger;

public class ProjectPreferencesTest {

    private ScopedPreferenceStore store;
    private Preferences wsPrefs;
    private ProjectPreferences prefs;

    @BeforeEach
    public void setUp() {
        IProject project = mock(IProject.class);
        store = mock(ScopedPreferenceStore.class);
        wsPrefs = mock(Preferences.class);
        Logger logger = mock(Logger.class);

        when(wsPrefs.readerForAnyLanguage()).thenReturn(mock(LanguagePreferencesReader.class));

        prefs = new ProjectPreferences(project, store, wsPrefs, logger);
    }

    @Test
    public void testHasPreferencesForLanguage() {
        when(store.getString(Preferences.BASE + "languages")).thenReturn("java,python,cpp");

        assertThat(prefs.hasPreferencesForLanguage("java")).isTrue();
        assertThat(prefs.hasPreferencesForLanguage("python")).isTrue();
        assertThat(prefs.hasPreferencesForLanguage("cpp")).isTrue();

        assertThat(prefs.hasPreferencesForLanguage("ruby")).isFalse();
        assertThat(prefs.hasPreferencesForLanguage("jav")).isFalse();
        assertThat(prefs.hasPreferencesForLanguage("thon")).isFalse();
    }

    @Test
    public void testHasPreferencesForLanguageWithEmptyStore() {
        when(store.getString(Preferences.BASE + "languages")).thenReturn("");
        assertThat(prefs.hasPreferencesForLanguage("java")).isFalse();

        when(store.getString(Preferences.BASE + "languages")).thenReturn(null);
        assertThat(prefs.hasPreferencesForLanguage("java")).isFalse();
    }

    @Test
    public void testActivatePreferencesForLanguage() {
        when(store.getString(Preferences.BASE + "languages")).thenReturn("java,python");

        // Activating a new language
        prefs.activatePreferencesForLanguage("cpp", true);
        verify(store).setValue(Preferences.BASE + "languages", "java,python,cpp");

        // Deactivating a language at the end
        prefs.activatePreferencesForLanguage("python", false);
        verify(store).setValue(Preferences.BASE + "languages", "java");
    }

    @Test
    public void testDeactivatePreferencesForLanguageMiddle() {
        when(store.getString(Preferences.BASE + "languages")).thenReturn("java,python,cpp");

        // Deactivating a language in the middle
        prefs.activatePreferencesForLanguage("python", false);
        verify(store).setValue(Preferences.BASE + "languages", "java,cpp");
    }

    @Test
    public void testDeactivatePreferencesForLanguageStart() {
        when(store.getString(Preferences.BASE + "languages")).thenReturn("java,python,cpp");

        // Deactivating a language at the start
        prefs.activatePreferencesForLanguage("java", false);
        verify(store).setValue(Preferences.BASE + "languages", "python,cpp");
    }

    @Test
    public void testActivatePreferencesForLanguageAlreadyActive() {
        when(store.getString(Preferences.BASE + "languages")).thenReturn("java,python");

        // Activating an already active language should do nothing
        prefs.activatePreferencesForLanguage("python", true);
        verify(store, never()).setValue(eq(Preferences.BASE + "languages"), eq("java,python,python"));
    }
}
