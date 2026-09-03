package org.moreunit.preferences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.moreunit.preferences.PreferenceConstants.PREFERENCES_VERSION;
import static org.moreunit.preferences.PreferenceConstants.TEST_CLASS_NAME_TEMPLATE;
import static org.moreunit.preferences.PreferenceConstants.TEST_PACKAGE_PREFIX;
import static org.moreunit.preferences.PreferenceConstants.TEST_PACKAGE_SUFFIX;
import static org.moreunit.preferences.PreferenceConstants.USE_PROJECT_SPECIFIC_SETTINGS;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.moreunit.matching.TestClassNamePattern;
import org.moreunit.preferences.PreferenceConstants.Deprecated;
import org.moreunit.preferences.Preferences.MethodSearchMode;
import org.moreunit.preferences.Preferences.ProjectPreferences;
import org.moreunit.test.DummyPreferencesForTesting;
import org.moreunit.test.context.Project;
import org.moreunit.test.context.TestContextRule;

public class PreferencesTest
{
    @RegisterExtension
    public final TestContextRule context = new TestContextRule();

    private DummyPreferencesForTesting prefs;
    private IPreferenceStore workbenchStore;

    @BeforeEach
    public void createTestPreferences()
    {
        prefs = new DummyPreferencesForTesting();
        workbenchStore = prefs.getWorkbenchStore();
    }

    @AfterEach
    public void resetWorkbenchPreferences()
    {
        for (final String key : new String[] { TEST_PACKAGE_PREFIX, TEST_PACKAGE_SUFFIX, TEST_CLASS_NAME_TEMPLATE })
        {
            workbenchStore.setToDefault(key);
        }
    }

    @Test
    public void hasProjectSpecificSettings_should_return_false_when_no_project_given()
    {
        assertFalse(prefs.hasProjectSpecificSettings(null));
    }

    @Test
    @Project
    public void hasProjectSpecificSettings_should_return_false_by_default_and_true_when_set()
    {
        final IJavaProject project = context.getProjectHandler().get();

        assertFalse(prefs.hasProjectSpecificSettings(project));

        prefs.setHasProjectSpecificSettings(project, true);
        assertTrue(prefs.hasProjectSpecificSettings(project));

        prefs.setHasProjectSpecificSettings(project, false);
        assertFalse(prefs.hasProjectSpecificSettings(project));
    }

    @Test
    @Project
    public void getProjectStore_should_return_same_store_for_same_project()
    {
        final IJavaProject project = context.getProjectHandler().get();

        assertSame(prefs.getProjectStore(project), prefs.getProjectStore(project));
    }

    @Test
    @Project
    public void clearProjectCache_should_forget_created_project_stores()
    {
        final IJavaProject project = context.getProjectHandler().get();

        final IPreferenceStore store = prefs.getProjectStore(project);
        prefs.clearProjectCache();

        assertNotSame(store, prefs.getProjectStore(project));
    }

    @Test
    @Project
    public void should_roundtrip_simple_project_preferences() throws Exception
    {
        final IJavaProject project = context.getProjectHandler().get();
        prefs.setHasProjectSpecificSettings(project, true);

        prefs.setTestMethodDefaultContent(project, "someContent();");
        assertEquals("someContent();", prefs.getTestMethodDefaultContent(project));

        prefs.setTestSuperClass(project, "junit.framework.TestCase");
        assertEquals("junit.framework.TestCase", prefs.getTestSuperClass(project));

        prefs.setTestType(project, PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4);
        assertEquals(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4, prefs.getTestType(project));

        prefs.setTestMethodTypeShouldUsePrefix(project, true);
        assertEquals(PreferenceConstants.TEST_METHOD_TYPE_JUNIT3, prefs.getTestMethodType(project));

        prefs.setTestMethodTypeShouldUsePrefix(project, false);
        assertEquals(PreferenceConstants.TEST_METHOD_TYPE_NO_PREFIX, prefs.getTestMethodType(project));

        prefs.setTestAnnotationMode(project, TestAnnotationMode.BY_NAME);
        assertEquals(TestAnnotationMode.BY_NAME.toString(), prefs.getTestAnnotationMode(project));
        assertEquals(TestAnnotationMode.BY_NAME, prefs.getProjectView(project).getTestAnnotationMode());
    }

    @Test
    @Project
    public void should_roundtrip_comments_and_code_mining_preferences()
    {
        final IJavaProject project = context.getProjectHandler().get();
        prefs.setHasProjectSpecificSettings(project, true);

        // defaults
        assertFalse(prefs.shouldGenerateCommentsForTestMethod(project));
        assertTrue(prefs.shouldEnableMoreUnitCodeMining(project));
        assertTrue(prefs.shouldEnableJumpToMethodCodeMining(project));
        assertTrue(prefs.shouldEnableJumpToClassCodeMining(project));

        prefs.setGenerateCommentsForTestMethod(project, true);
        assertTrue(prefs.shouldGenerateCommentsForTestMethod(project));

        prefs.setEnableMoreUnitCodeMining(project, false);
        assertFalse(prefs.shouldEnableMoreUnitCodeMining(project));

        prefs.setEnableJumpToMethodCodeMining(project, false);
        assertFalse(prefs.shouldEnableJumpToMethodCodeMining(project));

        prefs.setEnableJumpToClassCodeMining(project, false);
        assertFalse(prefs.shouldEnableJumpToClassCodeMining(project));
    }

    @Test
    @Project
    public void getMethodSearchMode_should_reflect_extended_search_and_search_by_name_settings()
    {
        final IJavaProject project = context.getProjectHandler().get();
        prefs.setHasProjectSpecificSettings(project, true);

        // defaults: extended search enabled + search by name enabled
        MethodSearchMode mode = prefs.getMethodSearchMode(project);
        assertTrue(mode.searchByCall);
        assertTrue(mode.searchByName);

        prefs.setShouldUseTestMethodExtendedSearch(project, false);
        mode = prefs.getMethodSearchMode(project);
        assertFalse(mode.searchByCall);
        assertTrue(mode.searchByName);

        prefs.setShouldUseTestMethodExtendedSearch(project, true);
        prefs.setShouldUseTestMethodSearchByName(project, false);
        mode = prefs.getMethodSearchMode(project);
        assertTrue(mode.searchByCall);
        assertFalse(mode.searchByName);
    }

    @Test
    public void should_roundtrip_test_package_prefix_and_suffix_for_workspace()
    {
        workbenchStore.setValue(TEST_PACKAGE_PREFIX, "test");
        workbenchStore.setValue(TEST_PACKAGE_SUFFIX, "it");

        final ProjectPreferences workspaceView = prefs.getWorkspaceView();
        assertEquals("test", workspaceView.getPackagePrefix());
        assertEquals("it", workspaceView.getPackageSuffix());
        assertEquals("test", Preferences.forProject(null).getPackagePrefix());
    }

    @Test
    public void getPackagePrefix_should_return_null_when_blank()
    {
        workbenchStore.setValue(TEST_PACKAGE_PREFIX, "   ");

        assertNull(prefs.getWorkspaceView().getPackagePrefix());
    }

    @Test
    public void getTestClassNamePattern_should_cache_pattern_and_renew_it_when_preferences_change()
    {
        final TestClassNamePattern pattern = prefs.getWorkspaceView().getTestClassNamePattern();

        assertSame(pattern, prefs.getWorkspaceView().getTestClassNamePattern());

        workbenchStore.setValue(TEST_PACKAGE_PREFIX, "test");

        final TestClassNamePattern newPattern = prefs.getWorkspaceView().getTestClassNamePattern();
        assertNotSame(pattern, newPattern);
    }

    @Test
    public void shouldUseJunitType_should_reflect_test_type()
    {
        workbenchStore.setValue(PreferenceConstants.TEST_TYPE, PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4);
        final ProjectPreferences workspaceView = prefs.getWorkspaceView();
        assertTrue(workspaceView.shouldUseJunit4Type());
        assertFalse(workspaceView.shouldUseJunit3Type());
        assertFalse(workspaceView.shouldUseJunit5Type());
        assertFalse(workspaceView.shouldUseTestNgType());
        assertFalse(workspaceView.shouldUseSpockType());
    }

    @Test
    @Project
    public void should_save_project_preferences_when_migration_occurs_on_freshly_created_project_store()
    {
        final IJavaProject project = context.getProjectHandler().get();

        final IPreferenceStore store = prefs.getProjectStore(project);
        store.setValue(USE_PROJECT_SPECIFIC_SETTINGS, true);
        store.setValue(PREFERENCES_VERSION, "");
        store.setValue(TEST_CLASS_NAME_TEMPLATE, "");
        store.setValue(Deprecated.PREFIXES, "Pre1,Pre2");
        store.setValue(Deprecated.SUFFIXES, "");
        store.setValue(Deprecated.FLEXIBEL_TESTCASE_NAMING, true);

        // a new Preferences instance has an empty project store cache:
        // getting the project store again must trigger the migration, and the
        // migration result has to be saved (otherwise it would happen again
        // and again at each startup)
        final DummyPreferencesForTesting freshPrefs = new DummyPreferencesForTesting();
        final IPreferenceStore recreatedStore = freshPrefs.getProjectStore(project);

        assertFalse(recreatedStore.needsSaving());
        assertEquals("(Pre1|Pre2)*${srcFile}", freshPrefs.getProjectView(project).getTestClassNameTemplate());
    }
}
