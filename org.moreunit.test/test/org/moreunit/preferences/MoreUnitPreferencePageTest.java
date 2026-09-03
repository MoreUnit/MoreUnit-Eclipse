package org.moreunit.preferences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.moreunit.MoreUnitPlugin;
import org.moreunit.core.matching.TestFileNamePattern;
import org.moreunit.preferences.MoreUnitPreferencePage;

import org.moreunit.properties.SwtPageTestCase;

/**
 * Tests {@link MoreUnitPreferencePage} with real SWT widgets. The page works
 * on the workspace preference store (no project selected). Values changed
 * during the tests are restored so that other tests are not affected.
 */
// white-box test: uses internal types on purpose
@SuppressWarnings("restriction")
public class MoreUnitPreferencePageTest extends SwtPageTestCase
{
    private static final String SRC_FILE_VARIABLE = TestFileNamePattern.SRC_FILE_VARIABLE;

    private MoreUnitPreferencePage page;
    private String junitDirectoryBefore;
    private String packagePrefixBefore;
    private String packageSuffixBefore;

    @BeforeEach
    public void createPreferencePage()
    {
        junitDirectoryBefore = workbenchStore().getString(PreferenceConstants.PREF_JUNIT_PATH);
        packagePrefixBefore = workbenchStore().getString(PreferenceConstants.TEST_PACKAGE_PREFIX);
        packageSuffixBefore = workbenchStore().getString(PreferenceConstants.TEST_PACKAGE_SUFFIX);

        page = new MoreUnitPreferencePage();
    }

    private org.eclipse.jface.preference.IPreferenceStore workbenchStore()
    {
        return MoreUnitPlugin.getDefault().getPreferenceStore();
    }

    private void createPageContents()
    {
        createContents(page, shell);
    }

    private Text findTestSourceFolderField()
    {
        Text field = findTextByLabel(shell, PreferenceConstants.TEXT_TEST_SOURCE_FOLDER);
        assertNotNull(field);
        return field;
    }

    @org.junit.jupiter.api.AfterEach
    public void restoreWorkbenchPreferences()
    {
        workbenchStore().setValue(PreferenceConstants.PREF_JUNIT_PATH, junitDirectoryBefore);
        workbenchStore().setValue(PreferenceConstants.TEST_PACKAGE_PREFIX, packagePrefixBefore);
        workbenchStore().setValue(PreferenceConstants.TEST_PACKAGE_SUFFIX, packageSuffixBefore);
    }

    @Test
    public void should_create_test_source_folder_field_with_current_preference()
    {
        createPageContents();

        assertEquals(junitDirectoryBefore, findTestSourceFolderField().getText());
    }

    @Test
    public void should_declare_error_message_when_test_source_folder_ends_with_slash()
    {
        createPageContents();
        Text field = findTestSourceFolderField();

        field.setText("src/test/");
        field.notifyListeners(SWT.Modify, new org.eclipse.swt.widgets.Event());

        assertEquals("Test source folder should not end with a slash", page.getErrorMessage());

        field.setText("test");
        field.notifyListeners(SWT.Modify, new org.eclipse.swt.widgets.Event());

        assertNull(page.getErrorMessage());
    }

    @Test
    public void should_validate_page_when_test_file_pattern_changes()
    {
        createPageContents();

        Text patternField = findTextByLabel(shell, "Pattern:");
        assertNotNull(patternField);

        patternField.setText(SRC_FILE_VARIABLE);
        patternField.notifyListeners(SWT.Modify, new org.eclipse.swt.widgets.Event());

        assertFalse(page.isValid());
        assertEquals("Test files must have a name different from their corresponding source file", page.getMessage());

        patternField.setText(SRC_FILE_VARIABLE + "(Test|IT)");
        patternField.notifyListeners(SWT.Modify, new org.eclipse.swt.widgets.Event());

        assertTrue(page.isValid());
        assertNull(page.getMessage());
    }

    @Test
    public void should_save_junit_directory_on_perform_ok()
    {
        createPageContents();
        Text field = findTestSourceFolderField();
        field.setText("src/test/java");

        assertTrue(page.performOk());

        assertEquals("src/test/java", workbenchStore().getString(PreferenceConstants.PREF_JUNIT_PATH));
        assertEquals("src/test/java", Preferences.getInstance().getJunitDirectoryFromPreferences(null));
    }

    @Test
    public void should_save_workspace_properties_on_perform_ok()
    {
        createPageContents();

        org.moreunit.properties.OtherMoreunitPropertiesBlock block = (org.moreunit.properties.OtherMoreunitPropertiesBlock) getField(page, "otherMoreunitPropertiesBlock");
        assertNotNull(block);

        Text packagePrefixField = findTextByLabel(shell, PreferenceConstants.TEXT_PACKAGE_PREFIX);
        packagePrefixField.setText("pref-prefix");

        Text packageSuffixField = findTextByLabel(shell, PreferenceConstants.TEXT_PACKAGE_SUFFIX);
        packageSuffixField.setText("pref-suffix");

        assertTrue(page.performOk());

        assertEquals("pref-prefix", workbenchStore().getString(PreferenceConstants.TEST_PACKAGE_PREFIX));
        assertEquals("pref-suffix", workbenchStore().getString(PreferenceConstants.TEST_PACKAGE_SUFFIX));
    }

    @Test
    public void should_use_plugin_preference_store_by_default()
    {
        // without init(IWorkbench), the page must fall back to the plugin's store
        MoreUnitPreferencePage freshPage = new MoreUnitPreferencePage();

        assertSame(MoreUnitPlugin.getDefault().getPreferenceStore(), freshPage.getPreferenceStore());
    }

    @Test
    public void should_show_warning_when_test_file_pattern_uses_too_many_wildcards()
    {
        createPageContents();

        Text patternField = findTextByLabel(shell, "Pattern:");
        patternField.setText(SRC_FILE_VARIABLE + "*Test*");
        patternField.notifyListeners(SWT.Modify, new org.eclipse.swt.widgets.Event());

        assertTrue(page.isValid());
        assertEquals("Using too many wildcards may degrade search performance and results!", page.getMessage());
    }

    @Test
    public void should_initialize_preference_store_from_workbench()
    {
        IWorkbench workbench = PlatformUI.getWorkbench();

        page.init(workbench);

        assertSame(MoreUnitPlugin.getDefault().getPreferenceStore(), page.getPreferenceStore());
        assertSame(MoreUnitPlugin.getDefault().getPreferenceStore(), workbenchStore());
    }
}
