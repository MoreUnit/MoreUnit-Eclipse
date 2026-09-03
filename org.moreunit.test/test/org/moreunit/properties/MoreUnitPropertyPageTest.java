package org.moreunit.properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

import org.eclipse.core.resources.ProjectScope;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.moreunit.MoreUnitPlugin;
import org.moreunit.core.log.Logger;
import org.moreunit.core.matching.TestFileNamePattern;
import org.moreunit.log.LogHandler;
import org.moreunit.preferences.PreferenceConstants;
import org.moreunit.preferences.Preferences;
import org.moreunit.test.context.Context;
import org.moreunit.test.context.configs.SimpleJUnit4Project;

import static org.mockito.Mockito.mock;

/**
 * Tests {@link MoreUnitPropertyPage} with real SWT widgets.
 */
@Context(SimpleJUnit4Project.class)
// white-box test: uses internal types on purpose
@SuppressWarnings("restriction")
public class MoreUnitPropertyPageTest extends SwtPageTestCase
{
    private MoreUnitPropertyPage page;
    private IJavaProject javaProject;

    @BeforeEach
    public void createPropertyPage()
    {
        javaProject = context.getProjectHandler().get();
        page = new MoreUnitPropertyPage();
        page.setElement(javaProject);
    }

    private void createPageContents()
    {
        createContents(page, shell);
    }

    private Button findProjectSpecificSettingsCheckbox()
    {
        final Button checkbox = findButton(shell, "Use project specific settings");
        assertNotNull(checkbox);
        return checkbox;
    }

    private void selectProjectSpecificSettings(boolean selected)
    {
        final Button checkbox = findProjectSpecificSettingsCheckbox();
        checkbox.setSelection(selected);
        checkbox.notifyListeners(SWT.Selection, new Event());
    }

    @Test
    public void should_create_checkbox_and_two_tabs()
    {
        createPageContents();

        assertNotNull(findProjectSpecificSettingsCheckbox());
        assertNotNull(findButton(shell, "JUnit 3.8"));
        assertNotNull(findButton(shell, "Junit 5"));
        assertNotNull(findButton(shell, "Remove"));
    }

    @Test
    public void should_check_project_specific_settings_when_project_uses_them()
    {
        Preferences.getInstance().setHasProjectSpecificSettings(javaProject, true);
        try
        {
            createPageContents();

            assertTrue(findProjectSpecificSettingsCheckbox().getSelection());
        }
        finally
        {
            Preferences.getInstance().setHasProjectSpecificSettings(javaProject, false);
        }
    }

    @Test
    public void should_enable_all_tabs_when_project_specific_settings_get_activated()
    {
        Preferences.getInstance().setHasProjectSpecificSettings(javaProject, false);
        try
        {
            createPageContents();

            assertFalse(findButton(shell, "Junit 5").getEnabled());

            final Button cb = findProjectSpecificSettingsCheckbox();
            cb.setSelection(true);
            cb.notifyListeners(SWT.Selection, new Event());

            assertTrue(findButton(shell, "Junit 5").getEnabled());
            assertTrue(findButton(shell, "Add").getEnabled());
            // remove/remap stay disabled while no mapping is selected
            assertFalse(findButton(shell, "Remove").getEnabled());

            selectProjectSpecificSettings(false);

            assertFalse(findButton(shell, "Junit 5").getEnabled());
        }
        finally
        {
            Preferences.getInstance().setHasProjectSpecificSettings(javaProject, false);
        }
    }

    @Test
    public void should_show_error_when_project_specific_settings_get_disabled_and_reenabled()
    {
        Preferences.getInstance().setHasProjectSpecificSettings(javaProject, true);
        Preferences.getInstance().setMappingList(javaProject, new ArrayList<>());
        try
        {
            createPageContents();

            assertFalse(page.isValid());
            assertEquals("Choose at least one test folder!", page.getErrorMessage());

            selectProjectSpecificSettings(false);

            assertTrue(page.isValid());
            assertNull(page.getErrorMessage());

            selectProjectSpecificSettings(true);

            assertFalse(page.isValid());
            assertEquals("Choose at least one test folder!", page.getErrorMessage());
        }
        finally
        {
            Preferences.getInstance().setHasProjectSpecificSettings(javaProject, false);
        }
    }

    @Test
    public void should_adapt_project_element_to_java_project()
    {
        page.setElement(javaProject.getProject());

        createPageContents();

        assertNotNull(findProjectSpecificSettingsCheckbox());
    }

    @Test
    public void should_save_all_preferences_on_perform_ok()
    {
        Preferences.getInstance().setHasProjectSpecificSettings(javaProject, true);
        try
        {
            createPageContents();
            selectProjectSpecificSettings(true);

            final Text packagePrefixField = findTextByLabel(shell, "Test package prefix:");
            packagePrefixField.setText("testpref");

            final Button junit4Button = findButton(shell, "Junit 4");
            junit4Button.setSelection(true);
            junit4Button.notifyListeners(SWT.Selection, new Event());

            assertTrue(page.performOk());

            final var projectStore = Preferences.getInstance().getProjectStore(javaProject);
            assertEquals(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4, projectStore.getString(PreferenceConstants.TEST_TYPE));
            assertEquals("testpref", projectStore.getString(PreferenceConstants.TEST_PACKAGE_PREFIX));
            assertTrue(Preferences.getInstance().hasProjectSpecificSettings(javaProject));
            assertEquals(1, Preferences.getInstance().getSourceMappingList(javaProject).size());
        }
        finally
        {
            Preferences.getInstance().setHasProjectSpecificSettings(javaProject, false);
        }
    }

    @Test
    public void should_save_preferences_on_perform_apply()
    {
        Preferences.getInstance().setHasProjectSpecificSettings(javaProject, true);
        try
        {
            createPageContents();
            selectProjectSpecificSettings(true);

            final Text packageSuffixField = findTextByLabel(shell, "Test package suffix:");
            packageSuffixField.setText("testsuffix");

            performApply(page);

            assertEquals("testsuffix", Preferences.getInstance().getProjectStore(javaProject).getString(PreferenceConstants.TEST_PACKAGE_SUFFIX));
        }
        finally
        {
            Preferences.getInstance().setHasProjectSpecificSettings(javaProject, false);
        }
    }

    @Test
    public void should_refresh_valid_state_when_pattern_changes()
    {
        Preferences.getInstance().setHasProjectSpecificSettings(javaProject, true);
        try
        {
            createPageContents();
            selectProjectSpecificSettings(true);

            final Text patternField = findTextByLabel(shell, "Pattern:");
            patternField.setText(TestFileNamePattern.SRC_FILE_VARIABLE);
            patternField.notifyListeners(SWT.Modify, new Event());

            assertFalse(page.isValid());
            assertEquals("Test files must have a name different from their corresponding source file", page.getErrorMessage());

            patternField.setText(TestFileNamePattern.SRC_FILE_VARIABLE + "(Test|IT)");
            patternField.notifyListeners(SWT.Modify, new Event());

            assertTrue(page.isValid());
            assertNull(page.getErrorMessage());
        }
        finally
        {
            Preferences.getInstance().setHasProjectSpecificSettings(javaProject, false);
        }
    }

    @Test
    public void should_accept_default_selection_events()
    {
        createPageContents();

        // covers the widgetDefaultSelected implementations of the registered listeners
        findProjectSpecificSettingsCheckbox().notifyListeners(SWT.DefaultSelection, new Event());

        assertTrue(page.isValid());
    }

    @Test
    public void should_log_error_and_still_succeed_when_saving_preferences_fails() throws Exception
    {
        // a previous test (bug regression test) may have installed a throwing
        // logger; use a harmless one so that the logged error does not explode
        final Field loggerField = LogHandler.getInstance().getClass().getDeclaredField("logger");
        loggerField.setAccessible(true);
        loggerField.set(LogHandler.getInstance(), mock(Logger.class));

        createPageContents();
        selectProjectSpecificSettings(true);

        final IPreferenceStore replacedStore = replaceProjectStoreWithFailingStore();
        try
        {
            assertTrue(page.performOk());
        }
        finally
        {
            restoreProjectStore(replacedStore);
        }
    }

    // Acces reflexif au champ prive "preferenceMap" de Preferences (test uniquement) :
    // le cast Map<IJavaProject, IPreferenceStore> est sur mais non verifiable a l'execution.
    @SuppressWarnings("unchecked")
    private IPreferenceStore replaceProjectStoreWithFailingStore() throws Exception
    {
        final IPreferenceStore oldStore = Preferences.getInstance().getProjectStore(javaProject);
        final IPreferenceStore failingStore = new ScopedPreferenceStore(new ProjectScope(javaProject.getProject()), MoreUnitPlugin.PLUGIN_ID)
        {
            @Override
            public void save() throws IOException
            {
                throw new IOException("save must fail");
            }
        };

        final Field preferenceMapField = Preferences.class.getDeclaredField("preferenceMap");
        preferenceMapField.setAccessible(true);
        final Map<IJavaProject, IPreferenceStore> preferenceMap = (Map<IJavaProject, IPreferenceStore>) preferenceMapField.get(Preferences.getInstance());
        preferenceMap.put(javaProject, failingStore);

        return oldStore;
    }

    // Acces reflexif au champ prive "preferenceMap" de Preferences (test uniquement) :
    // le cast Map<IJavaProject, IPreferenceStore> est sur mais non verifiable a l'execution.
    @SuppressWarnings("unchecked")
    private void restoreProjectStore(IPreferenceStore oldStore) throws Exception
    {
        final Field preferenceMapField = Preferences.class.getDeclaredField("preferenceMap");
        preferenceMapField.setAccessible(true);
        final Map<IJavaProject, IPreferenceStore> preferenceMap = (Map<IJavaProject, IPreferenceStore>) preferenceMapField.get(Preferences.getInstance());
        preferenceMap.put(javaProject, oldStore);
    }

    @Test
    public void should_save_existing_mappings_when_perform_ok_without_project_settings()
    {
        createPageContents();

        assertTrue(page.performOk());

        // the default workspace mapping (src -> test) has been written to the project store
        final var projectStore = Preferences.getInstance().getProjectStore(javaProject);
        final String storedMappings = projectStore.getString(PreferenceConstants.UNIT_SOURCE_FOLDER);
        assertNotNull(storedMappings);
        assertFalse(storedMappings.isEmpty());
    }
}
