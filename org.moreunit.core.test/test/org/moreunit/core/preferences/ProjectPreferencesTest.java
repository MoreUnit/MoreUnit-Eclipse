package org.moreunit.core.preferences;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class ProjectPreferencesTest
{
    @Test
    public void should_remove_language_from_list()
    {
        // ⚡ Bolt Performance Optimization Verification
        // Test edge cases for replacing replaceFirst(",?\\b%s\\b,?")

        // Remove from middle (the bug in the original regex)
        assertThat(ProjectPreferences.removeLanguage("java,python,cpp", "python")).isEqualTo("java,cpp");

        // Remove from start
        assertThat(ProjectPreferences.removeLanguage("python,java", "python")).isEqualTo("java");

        // Remove from end
        assertThat(ProjectPreferences.removeLanguage("java,python", "python")).isEqualTo("java");

        // Remove exact match
        assertThat(ProjectPreferences.removeLanguage("python", "python")).isEqualTo("");

        // Remove multiple matches (should only remove first to mimic replaceFirst)
        assertThat(ProjectPreferences.removeLanguage("java,python,python,cpp", "python")).isEqualTo("java,python,cpp");

        // Remove when substring but not bounded
        assertThat(ProjectPreferences.removeLanguage("java,python3,cpp", "python")).isEqualTo("java,python3,cpp");

        // Remove non-existent
        assertThat(ProjectPreferences.removeLanguage("java,cpp", "python")).isEqualTo("java,cpp");
    }

    @Test
    public void should_check_language_in_list()
    {
        // ⚡ Bolt Performance Optimization Verification
        // Test edge cases for replacing String.matches(".*\\b%s\\b.*")

        // Match from middle
        assertThat(ProjectPreferences.hasLanguage("java,python,cpp", "python")).isTrue();

        // Match from start
        assertThat(ProjectPreferences.hasLanguage("python,java", "python")).isTrue();

        // Match from end
        assertThat(ProjectPreferences.hasLanguage("java,python", "python")).isTrue();

        // Match exact match
        assertThat(ProjectPreferences.hasLanguage("python", "python")).isTrue();

        // Reject when substring but not bounded (end)
        assertThat(ProjectPreferences.hasLanguage("java,python3,cpp", "python")).isFalse();

        // Reject when substring but not bounded (start)
        assertThat(ProjectPreferences.hasLanguage("java,cpython,cpp", "python")).isFalse();

        // Reject non-existent
        assertThat(ProjectPreferences.hasLanguage("java,cpp", "python")).isFalse();

        // Handle null/empty
        assertThat(ProjectPreferences.hasLanguage("", "python")).isFalse();
        assertThat(ProjectPreferences.hasLanguage(null, "python")).isFalse();
        assertThat(ProjectPreferences.hasLanguage("python", null)).isFalse();
        assertThat(ProjectPreferences.hasLanguage("python", "")).isFalse();
    }
}
