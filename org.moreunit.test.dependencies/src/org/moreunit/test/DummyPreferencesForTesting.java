package org.moreunit.test;

import static java.util.Arrays.asList;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.moreunit.preferences.PreferenceConstants;
import org.moreunit.preferences.Preferences;

public final class DummyPreferencesForTesting extends Preferences
{
    // private IPreferenceStore workbenchStore;

    public DummyPreferencesForTesting()
    {
        Preferences.setInstance(this);

        IPreferenceStore workbenchStore = getWorkbenchStore();

        // reset
        for (String p : asList(PreferenceConstants.PREF_JUNIT_PATH, //
                               PreferenceConstants.TEST_TYPE, //
                               PreferenceConstants.SHOW_REFACTORING_DIALOG, //
                               PreferenceConstants.SWITCH_TO_MATCHING_METHOD, //
                               PreferenceConstants.TEST_PACKAGE_PREFIX, //
                               PreferenceConstants.TEST_SUPERCLASS, //
                               PreferenceConstants.TEST_METHOD_TYPE, //
                               PreferenceConstants.EXTENDED_TEST_METHOD_SEARCH, //
                               PreferenceConstants.ENABLE_TEST_METHOD_SEARCH_BY_NAME))
        {
            workbenchStore.setToDefault(p);
        }

        // replacing store does not work, it looks like it is already accessed
        // and stored somewhere else at this stage
        // workbenchStore = new PreferenceStore();
        // Preferences.initStore(workbenchStore);
    }

    public void forceProjectPreferencesMigration(IJavaProject project)
    {
        IPreferenceStore store = getProjectStore(project);
        migratePrefsIfRequired(store);
    }

    public void forceWorkspacePreferencesMigration()
    {
        forceProjectPreferencesMigration(null);
    }
}
