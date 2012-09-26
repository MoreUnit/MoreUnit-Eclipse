package org.moreunit.preferences;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * A step participating in migrating user preferences to a version N.
 */
public interface MigrationStep
{
    /**
     * The version in which the modifications performed by this step should
     * appear.
     */
    int targetVersion();

    /**
     * Performs the migration.
     */
    void apply(IPreferenceStore store);
}
