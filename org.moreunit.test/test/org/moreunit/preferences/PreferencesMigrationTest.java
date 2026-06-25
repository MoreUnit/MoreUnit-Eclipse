package org.moreunit.preferences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.moreunit.preferences.PreferenceConstants.PREFERENCES_VERSION;
import static org.moreunit.preferences.PreferenceConstants.TEST_CLASS_NAME_TEMPLATE;
import static org.moreunit.preferences.PreferenceConstants.USE_PROJECT_SPECIFIC_SETTINGS;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.moreunit.preferences.PreferenceConstants.Deprecated;
import org.moreunit.test.DummyPreferencesForTesting;
import org.moreunit.test.context.Project;
import org.moreunit.test.context.TestContextRule;

public class PreferencesMigrationTest
{
    @RegisterExtension
    public final TestContextRule context = new TestContextRule();

    private DummyPreferencesForTesting prefs;

    @BeforeEach
    public void createTestPreferences() throws Exception
    {
        prefs = new DummyPreferencesForTesting();
    }

    @Test
    public void should_migrate_workspace_preferences_from_v1_to_v2() throws Exception
    {
        // given
        IPreferenceStore workbenchPrefStore = prefs.getWorkbenchStore();

        // remove new-style pref values
        workbenchPrefStore.setValue(PREFERENCES_VERSION, "");
        workbenchPrefStore.setValue(TEST_CLASS_NAME_TEMPLATE, "");

        // define old-style pref values
        workbenchPrefStore.setValue(Deprecated.PREFIXES, "Pre1,Pre2");
        workbenchPrefStore.setValue(Deprecated.SUFFIXES, "Suf1,Suf2");
        workbenchPrefStore.setValue(Deprecated.FLEXIBEL_TESTCASE_NAMING, true);

        // when
        prefs.forceWorkspacePreferencesMigration();

        // then
        assertEquals(prefs.getWorkspaceView().getTestClassNameTemplate(), "(Pre1|Pre2)*${srcFile}*(Suf1|Suf2)");
    }

    @Test
    @Project
    public void should_migrate_project_preferences_from_v1_to_v2() throws Exception
    {
        // given
        IJavaProject project = context.getProjectHandler().get();

        IPreferenceStore projectPrefStore = prefs.getProjectStore(project);
        projectPrefStore.setValue(USE_PROJECT_SPECIFIC_SETTINGS, true);

        // remove new-style pref values
        projectPrefStore.setValue(PREFERENCES_VERSION, "");
        projectPrefStore.setValue(TEST_CLASS_NAME_TEMPLATE, "");

        // define old-style pref values
        projectPrefStore.setValue(Deprecated.PREFIXES, "Prefix1,Prefix2");
        projectPrefStore.setValue(Deprecated.SUFFIXES, "Suffix");
        projectPrefStore.setValue(Deprecated.FLEXIBEL_TESTCASE_NAMING, false);

        // when
        prefs.forceProjectPreferencesMigration(project);

        // then
        assertEquals(prefs.getProjectView(project).getTestClassNameTemplate(), "(Prefix1|Prefix2)${srcFile}Suffix");
    }
}
