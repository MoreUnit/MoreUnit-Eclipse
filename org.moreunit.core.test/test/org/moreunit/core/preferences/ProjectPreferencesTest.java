package org.moreunit.core.preferences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ProjectPreferencesTest
{
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
}
