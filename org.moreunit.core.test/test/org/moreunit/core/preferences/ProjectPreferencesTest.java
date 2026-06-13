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
}
