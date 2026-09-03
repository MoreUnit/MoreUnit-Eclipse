package org.moreunit.preferences;

import static java.util.Arrays.asList;
import static java.util.Collections.sort;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;

public class PreferencesMigrator
{
    private static final Comparator<MigrationStep> BY_TARGET_VERSION = (s1, s2) -> {
        final int v1 = s1.targetVersion();
        final int v2 = s2.targetVersion();
        return v1 == v2 ? 0 : (v1 < v2 ? - 1 : 1);
    };

    private final IPreferenceStore store;
    private final int pluginPrefVersion;
    private final List< ? extends MigrationStep> steps;

    public PreferencesMigrator(IPreferenceStore preferenceStore)
    {
        this(preferenceStore, PreferenceConstants.CURRENT_PREFERENCES_VERSION, asList(new ChangeTestClassNamePrefixesAndSuffixesIntoPattern()));
    }

    public PreferencesMigrator(IPreferenceStore preferenceStore, int currentPluginPreferencesVersion, List< ? extends MigrationStep> migrationSteps)
    {
        store = preferenceStore;
        pluginPrefVersion = currentPluginPreferencesVersion;
        steps = new ArrayList<MigrationStep>(migrationSteps);

        // step reordering in case we accidentally specified them in wrong order
        sort(steps, BY_TARGET_VERSION);
    }

    public void migrate()
    {
        final int storeVersion = getStoreVersion();
        if(storeVersion < pluginPrefVersion)
        {
            int targetVersion = storeVersion + 1;
            for (final MigrationStep step : steps)
            {
                if(step.targetVersion() < targetVersion)
                {
                    continue;
                }
                step.apply(store);
                targetVersion = step.targetVersion();
            }

            store.setValue(PreferenceConstants.PREFERENCES_VERSION, pluginPrefVersion);
        }
    }

    private int getStoreVersion()
    {
        final int v = store.getInt(PreferenceConstants.PREFERENCES_VERSION);
        return v < 1 ? 1 : v;
    }
}
