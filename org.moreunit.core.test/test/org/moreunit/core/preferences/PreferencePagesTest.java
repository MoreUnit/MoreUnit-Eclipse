package org.moreunit.core.preferences;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class PreferencePagesTest
{
    @Test
    public void should_define_preference_page_ids()
    {
        assertEquals("org.moreunit.core.preferences.featuredLanguagesPage", PreferencePages.FEATURED_LANGUAGES);
        assertEquals("org.moreunit.core.preferences.otherLanguagesPage", PreferencePages.OTHER_LANGUAGES);
    }
}
