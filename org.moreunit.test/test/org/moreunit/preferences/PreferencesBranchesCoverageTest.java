package org.moreunit.preferences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.moreunit.preferences.PreferenceConstants.DEFAULT_TEST_TYPE;
import static org.moreunit.preferences.PreferenceConstants.ENABLE_JUMP_TO_CLASS_CODE_MINING;
import static org.moreunit.preferences.PreferenceConstants.ENABLE_JUMP_TO_METHOD_CODE_MINING;
import static org.moreunit.preferences.PreferenceConstants.ENABLE_MOREUNIT_CODE_MINING;
import static org.moreunit.preferences.PreferenceConstants.ENABLE_TEST_METHOD_SEARCH_BY_NAME;
import static org.moreunit.preferences.PreferenceConstants.EXTENDED_TEST_METHOD_SEARCH;
import static org.moreunit.preferences.PreferenceConstants.GENERATE_COMMENTS_FOR_TEST_METHOD;
import static org.moreunit.preferences.PreferenceConstants.PREF_JUNIT_PATH;
import static org.moreunit.preferences.PreferenceConstants.TEST_ANNOTATION_MODE;
import static org.moreunit.preferences.PreferenceConstants.TEST_CLASS_NAME_TEMPLATE;
import static org.moreunit.preferences.PreferenceConstants.TEST_PACKAGE_PREFIX;
import static org.moreunit.preferences.PreferenceConstants.TEST_PACKAGE_SUFFIX;
import static org.moreunit.preferences.PreferenceConstants.TEST_TYPE;
import static org.moreunit.preferences.PreferenceConstants.USE_PROJECT_SPECIFIC_SETTINGS;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.moreunit.core.log.Logger;
import org.moreunit.preferences.Preferences.MethodSearchMode;
import org.moreunit.test.DummyPreferencesForTesting;
import org.moreunit.test.context.Project;
import org.moreunit.test.context.TestContextRule;

public class PreferencesBranchesCoverageTest
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
        // make sure "contains" fallbacks are exercised deterministically,
        // whatever other test classes left behind on the shared workbench store
        for (final String key : new String[] { TEST_TYPE, EXTENDED_TEST_METHOD_SEARCH, ENABLE_TEST_METHOD_SEARCH_BY_NAME, GENERATE_COMMENTS_FOR_TEST_METHOD, ENABLE_MOREUNIT_CODE_MINING, ENABLE_JUMP_TO_METHOD_CODE_MINING, ENABLE_JUMP_TO_CLASS_CODE_MINING, TEST_ANNOTATION_MODE, PREF_JUNIT_PATH })
        {
            workbenchStore.setToDefault(key);
        }
    }

    @AfterEach
    public void resetWorkbenchPreferences()
    {
        for (final String key : new String[] { TEST_PACKAGE_PREFIX, TEST_PACKAGE_SUFFIX, TEST_CLASS_NAME_TEMPLATE, TEST_ANNOTATION_MODE, PREF_JUNIT_PATH })
        {
            workbenchStore.setToDefault(key);
        }
    }

    @Test
    public void should_return_default_test_type_when_store_does_not_contain_it()
    {
        // a store without any default cannot report the key as contained,
        // which is the only way to reach the default-type fallback
        final IPreferenceStore storeWithoutDefaults = mock(IPreferenceStore.class);
        when(storeWithoutDefaults.contains(anyString())).thenReturn(false);
        when(storeWithoutDefaults.getInt(anyString())).thenReturn(999);
        final Preferences barePrefs = new DummyPreferencesForTesting()
        {
            @Override
            public IPreferenceStore getWorkbenchStore()
            {
                return storeWithoutDefaults;
            }
        };

        assertEquals(DEFAULT_TEST_TYPE, barePrefs.getTestType(null));
    }

    @Test
    public void should_use_default_booleans_when_keys_are_not_set()
    {
        final MethodSearchMode mode = prefs.getMethodSearchMode(null);
        assertTrue(mode.searchByCall);
        assertTrue(mode.searchByName);

        assertTrue(prefs.shouldEnableMoreUnitCodeMining(null));
        assertTrue(prefs.shouldEnableJumpToMethodCodeMining(null));
        assertTrue(prefs.shouldEnableJumpToClassCodeMining(null));
        assertFalse(prefs.shouldGenerateCommentsForTestMethod(null));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void should_reuse_project_store_from_cache_on_second_access() throws Exception
    {
        final IJavaProject project = mock(IJavaProject.class);
        final IPreferenceStore cached = mock(IPreferenceStore.class);

        final Field mapField = Preferences.class.getDeclaredField("preferenceMap");
        mapField.setAccessible(true);
        final Map<IJavaProject, IPreferenceStore> map = (Map<IJavaProject, IPreferenceStore>) mapField.get(prefs);
        map.put(project, cached);
        try
        {
            final Method method = Preferences.class.getDeclaredMethod("getOrCreateProjectStore", IJavaProject.class);
            method.setAccessible(true);
            assertSame(cached, method.invoke(prefs, project));
        }
        finally
        {
            map.remove(project);
        }
    }

    @Test
    public void should_log_debug_message_when_saving_migrated_project_preferences() throws Exception
    {
        final ScopedPreferenceStore store = mock(ScopedPreferenceStore.class);
        when(store.getBoolean(USE_PROJECT_SPECIFIC_SETTINGS)).thenReturn(true);
        when(store.needsSaving()).thenReturn(true);
        when(store.getString(TEST_CLASS_NAME_TEMPLATE)).thenReturn("template");

        final Logger mockLogger = mock(Logger.class);
        when(mockLogger.debugEnabled()).thenReturn(true);

        final Field loggerField = Preferences.class.getDeclaredField("logger");
        loggerField.setAccessible(true);
        final Logger original = (Logger) loggerField.get(prefs);
        loggerField.set(prefs, mockLogger);
        try
        {
            callSaveMigrationResultIfRequired(store, null);
        }
        finally
        {
            loggerField.set(prefs, original);
        }

        verify(mockLogger).debug(anyString());
    }

    @Test
    public void should_log_error_when_saving_migrated_preferences_fails() throws Exception
    {
        final ScopedPreferenceStore store = mock(ScopedPreferenceStore.class);
        when(store.getBoolean(USE_PROJECT_SPECIFIC_SETTINGS)).thenReturn(true);
        when(store.needsSaving()).thenReturn(true);
        doThrow(new IOException("boom")).when(store).save();

        final Logger mockLogger = mock(Logger.class);

        final Field loggerField = Preferences.class.getDeclaredField("logger");
        loggerField.setAccessible(true);
        final Logger original = (Logger) loggerField.get(prefs);
        loggerField.set(prefs, mockLogger);
        try
        {
            callSaveMigrationResultIfRequired(store, null);
        }
        finally
        {
            loggerField.set(prefs, original);
        }

        verify(mockLogger).error(eq("Could not save preferences for project null"), any(IOException.class));
    }

    private void callSaveMigrationResultIfRequired(ScopedPreferenceStore store, IJavaProject project) throws Exception
    {
        final Method method = Preferences.class.getDeclaredMethod("saveMigrationResultIfRequired", ScopedPreferenceStore.class, IJavaProject.class);
        method.setAccessible(true);
        try
        {
            method.invoke(prefs, store, project);
        }
        catch (final java.lang.reflect.InvocationTargetException e)
        {
            throw new AssertionError("unexpected invocation failure", e.getCause());
        }
    }

    @Test
    @Project(mainSrcFolder = "src", mainCls = "Hello")
    public void should_fall_back_to_junit_folder_when_no_mapping_exists()
    {
        // given a project without any test folder mapping
        final IJavaProject project = context.getProjectHandler().get();
        final IPackageFragmentRoot srcFolder = context.getProjectHandler().getSrcFolderHandler("src").get();

        // the derived mappings follow the workbench junit folder, so pin it
        // to a non-existing folder first for a deterministic empty mapping
        final String oldJunitPath = prefs.getJunitDirectoryFromPreferences(null);
        prefs.setJunitDirectory("junit");
        try
        {
            assertTrue(prefs.getSourceMappingList(project).isEmpty());

            prefs.setJunitDirectory("src");

            // when
            final IPackageFragmentRoot result = prefs.getTestSourceFolder(project, srcFolder);

            // then the junit folder itself is returned
            assertEquals(srcFolder, result);
        }
        finally
        {
            prefs.setJunitDirectory(oldJunitPath);
        }
    }

    @Test
    @Project(mainSrcFolder = "src", mainCls = "Hello")
    public void should_delegate_project_view_methods_to_preferences()
    {
        final IJavaProject project = context.getProjectHandler().get();
        final IPackageFragmentRoot srcFolder = context.getProjectHandler().getSrcFolderHandler("src").get();

        assertNotNull(prefs.getProjectView(project).getSourceFolderMappings());
        assertNotNull(prefs.getProjectView(project).getSpockTestSourceFolder(srcFolder));
        assertNotNull(prefs.getProjectView(project).getTestMethodType());
        assertFalse(prefs.getProjectView(project).hasSpecificSettings());
    }

    @Test
    public void should_rebuild_test_class_name_pattern_when_template_changes()
    {
        final org.moreunit.matching.TestClassNamePattern pattern = prefs.getWorkspaceView().getTestClassNamePattern();
        assertSame(pattern, prefs.getWorkspaceView().getTestClassNamePattern());

        prefs.getWorkspaceView().setTestClassNameTemplate("Pre${srcFile}Suf");

        assertNotSame(pattern, prefs.getWorkspaceView().getTestClassNamePattern());
    }

    @Test
    public void should_return_off_annotation_mode_when_preference_is_blank()
    {
        workbenchStore.setValue(TEST_ANNOTATION_MODE, "");
        try
        {
            assertEquals(TestAnnotationMode.OFF, prefs.getWorkspaceView().getTestAnnotationMode());
        }
        finally
        {
            workbenchStore.setToDefault(TEST_ANNOTATION_MODE);
        }
    }
}
