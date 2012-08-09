package org.moreunit.test;

import static java.util.Arrays.asList;

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
                               PreferenceConstants.PREFIXES, //
                               PreferenceConstants.SUFFIXES, //
                               PreferenceConstants.USE_WIZARDS, //
                               PreferenceConstants.SWITCH_TO_MATCHING_METHOD, //
                               PreferenceConstants.TEST_PACKAGE_PREFIX, //
                               PreferenceConstants.FLEXIBEL_TESTCASE_NAMING, //
                               PreferenceConstants.TEST_SUPERCLASS, //
                               PreferenceConstants.TEST_METHOD_TYPE, //
                               PreferenceConstants.EXTENDED_TEST_METHOD_SEARCH))
        {
            workbenchStore.setToDefault(p);
        }

        // replacing store does not work, it looks like it is already accessed
        // and stored somewhere else at this stage
        // workbenchStore = new PreferenceStore();
        // Preferences.initStore(workbenchStore);
    }

    // @Override
    // protected IPreferenceStore getWorkbenchStore()
    // {
    // return workbenchStore;
    // }
}
