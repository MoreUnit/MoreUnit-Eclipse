package org.moreunit.preferences;

import static java.util.Arrays.asList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.moreunit.preferences.PreferenceConstants.PREFERENCES_VERSION;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.Test;

public class PreferencesMigratorTest
{
    List<StepTargetingVersion> calledSteps = new ArrayList<StepTargetingVersion>();

    IPreferenceStore store = mock(IPreferenceStore.class);

    StepTargetingVersion stepTargetingV5 = new StepTargetingVersion(5);
    StepTargetingVersion stepTargetingV3 = new StepTargetingVersion(3);
    StepTargetingVersion stepTargetingV7 = new StepTargetingVersion(7);

    List<StepTargetingVersion> unorderedStepsTargetingV_3_5_7 = asList(stepTargetingV5, stepTargetingV3, stepTargetingV7);

    @Test
    public void should_apply_steps_by_ascending_target_version() throws Exception
    {
        // given
        int appPreferencesVersion = 99;
        PreferencesMigrator migrator = new PreferencesMigrator(store, appPreferencesVersion, unorderedStepsTargetingV_3_5_7);

        storePreferencesVersionIs(1);

        // when
        migrator.migrate();

        // then
        assertThat(calledSteps).containsExactly(stepTargetingV3, stepTargetingV5, stepTargetingV7);
    }

    @Test
    public void should_only_apply_steps_with_target_version_greater_than_current_preferences_version() throws Exception
    {
        // given
        int appPreferencesVersion = 7;
        PreferencesMigrator migrator = new PreferencesMigrator(store, appPreferencesVersion, unorderedStepsTargetingV_3_5_7);

        storePreferencesVersionIs(4);

        // when
        migrator.migrate();

        // then
        assertThat(calledSteps).containsExactly(stepTargetingV5, stepTargetingV7);
    }

    @Test
    public void should_not_apply_any_step_when_target_versions_are_lower_than_or_equal_to_store_version() throws Exception
    {
        // given
        int appPreferencesVersion = 12;
        PreferencesMigrator migrator = new PreferencesMigrator(store, appPreferencesVersion, unorderedStepsTargetingV_3_5_7);

        storePreferencesVersionIs(7);

        // when
        migrator.migrate();

        // then
        assertThat(calledSteps).isEmpty();
    }

    @Test
    public void should_update_store_version_after_migration() throws Exception
    {
        // given
        int appPreferencesVersion = 42;
        PreferencesMigrator migrator = new PreferencesMigrator(store, appPreferencesVersion, unorderedStepsTargetingV_3_5_7);

        storePreferencesVersionIs(1);

        // when
        migrator.migrate();

        // then
        verify(store).setValue(PREFERENCES_VERSION, appPreferencesVersion);
    }

    @Test
    public void should_not_update_store_version_when_no_migration_occurred() throws Exception
    {
        // given
        int appPreferencesVersion = 17;
        PreferencesMigrator migrator = new PreferencesMigrator(store, appPreferencesVersion, unorderedStepsTargetingV_3_5_7);

        storePreferencesVersionIs(appPreferencesVersion);

        // when
        migrator.migrate();

        // then
        verify(store, never()).setValue(anyString(), anyInt());
    }

    private void storePreferencesVersionIs(int v)
    {
        when(store.getInt(PREFERENCES_VERSION)).thenReturn(v);
    }

    private class StepTargetingVersion implements MigrationStep
    {
        private int targetVersion;

        public StepTargetingVersion(int targetVersion)
        {
            this.targetVersion = targetVersion;
        }

        public int targetVersion()
        {
            return targetVersion;
        }

        public void apply(IPreferenceStore store)
        {
            calledSteps.add(this);
        }

        @Override
        public String toString()
        {
            return String.format("%s(%s)", getClass().getSimpleName(), targetVersion);
        }
    }
}
