package org.moreunit.core.preferences;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.Test;
import org.moreunit.core.matching.TestFileNamePattern;

public class LanguagePreferencesTest
{
    @Test
    public void should_cache_test_file_name_patterns_and_share_them_between_instances() throws Exception
    {
        // given
        LanguagePreferences prefs = preferencesWithTestFileNameTemplateAndSeparator("${srcFile}_test", "_");
        LanguagePreferences otherPrefsWithSamePattern = preferencesWithTestFileNameTemplateAndSeparator("${srcFile}_test", "_");
        LanguagePreferences otherPrefsWithDifferentPattern = preferencesWithTestFileNameTemplateAndSeparator("test-${srcFile}", "-");

        // then
        assertThat(prefs.getTestFileNamePattern()).isSameAs(otherPrefsWithSamePattern.getTestFileNamePattern());
        assertThat(prefs.getTestFileNamePattern()).isNotSameAs(otherPrefsWithDifferentPattern.getTestFileNamePattern());
    }

    @Test
    public void should_clear_cache_on_preference_change() throws Exception
    {
        // given
        LanguagePreferences prefs = preferencesWithTestFileNameTemplateAndSeparator("${srcFile}_test", "_");
        LanguagePreferences otherPrefsWithSamePattern = preferencesWithTestFileNameTemplateAndSeparator("${srcFile}_test", "_");

        TestFileNamePattern patternBeforeChange = prefs.getTestFileNamePattern();

        // when
        prefs.setValue("foo", "bar");

        // then
        assertThat(prefs.getTestFileNamePattern()).isNotSameAs(patternBeforeChange);

        assertThat(otherPrefsWithSamePattern.getTestFileNamePattern()) //
            .isNotSameAs(patternBeforeChange) //
            .isSameAs(prefs.getTestFileNamePattern());
    }

    private LanguagePreferences preferencesWithTestFileNameTemplateAndSeparator(final String template, final String separator)
    {
        return new LanguagePreferences(null, null)
        {
            @Override
            public String getTestFileNameTemplate()
            {
                return template;
            }

            @Override
            public String getFileWordSeparator()
            {
                return separator;
            }

            @Override
            public IPreferenceStore getStore()
            {
                return mock(IPreferenceStore.class);
            }

            @Override
            public String getSrcFolderPathTemplate()
            {
                throw new UnsupportedOperationException("test implementation");
            }

            @Override
            public String getTestFolderPathTemplate()
            {
                throw new UnsupportedOperationException("test implementation");
            }
        };
    }
}
