package org.moreunit.core.preferences;

import org.junit.Test;

public class ProjectPreferencesStringReplacementTest {

    @Test
    public void testRemoveLanguage_middle() {
        String activeLanguages = "java,ruby,python";
        String language = "ruby";

        String search1 = "," + language + ",";
        String search2 = language + ",";
        String search3 = "," + language;
        int idx = activeLanguages.indexOf(search1);

        if (idx != -1) {
            activeLanguages = activeLanguages.substring(0, idx) + activeLanguages.substring(idx + search1.length() - 1);
        } else if (activeLanguages.startsWith(search2)) {
            activeLanguages = activeLanguages.substring(search2.length());
        } else if (activeLanguages.endsWith(search3)) {
            activeLanguages = activeLanguages.substring(0, activeLanguages.length() - search3.length());
        } else if (activeLanguages.equals(language)) {
            activeLanguages = "";
        }
        org.junit.Assert.assertEquals("java,python", activeLanguages);
    }
}
