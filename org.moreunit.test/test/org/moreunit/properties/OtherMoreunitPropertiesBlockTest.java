package org.moreunit.properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Text;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.moreunit.core.matching.TestFileNamePattern;
import org.moreunit.core.preferences.TestFileNamePatternGroup;
import org.moreunit.preferences.PreferenceConstants;
import org.moreunit.preferences.Preferences;
import org.moreunit.preferences.TestAnnotationMode;
import org.moreunit.test.context.Context;
import org.moreunit.test.context.configs.SimpleJUnit4Project;

/**
 * Tests {@link OtherMoreunitPropertiesBlock} with real SWT widgets. The
 * project preferences are explicitly reset before each test so that the
 * initial state of the controls is predictable (each test gets a fresh
 * project, so no cleanup is required).
 */
@Context(SimpleJUnit4Project.class)
// white-box test: uses internal types on purpose
@SuppressWarnings("restriction")
public class OtherMoreunitPropertiesBlockTest extends SwtPageTestCase
{
    private static final String SRC_FILE_VARIABLE = TestFileNamePattern.SRC_FILE_VARIABLE;

    private OtherMoreunitPropertiesBlock block;
    private Composite blockControl;
    private IJavaProject javaProject;

    @BeforeEach
    public void prepareProjectPreferences()
    {
        javaProject = context.getProjectHandler().get();
        Preferences preferences = Preferences.getInstance();

        preferences.setHasProjectSpecificSettings(javaProject, true);
        preferences.setTestType(javaProject, PreferenceConstants.TEST_TYPE_VALUE_JUNIT_5);
        preferences.setTestMethodTypeShouldUsePrefix(javaProject, false);
        preferences.setTestMethodDefaultContent(javaProject, PreferenceConstants.DEFAULT_TEST_METHOD_DEFAULT_CONTENT);
        preferences.setTestPackagePrefix(javaProject, "");
        preferences.setTestPackageSuffix(javaProject, "");
        preferences.setTestSuperClass(javaProject, "");
        Preferences.forProject(javaProject).setTestClassNameTemplate(PreferenceConstants.DEFAULT_TEST_CLASS_NAME_TEMPLATE);
        preferences.setTestAnnotationMode(javaProject, TestAnnotationMode.OFF);
        preferences.setShouldUseTestMethodExtendedSearch(javaProject, true);
        preferences.setShouldUseTestMethodSearchByName(javaProject, true);
        preferences.setGenerateCommentsForTestMethod(javaProject, false);
        preferences.setEnableMoreUnitCodeMining(javaProject, true);
        preferences.setEnableJumpToMethodCodeMining(javaProject, true);
        preferences.setEnableJumpToClassCodeMining(javaProject, true);
    }

    private void createBlock()
    {
        block = new OtherMoreunitPropertiesBlock(javaProject);
        blockControl = block.getControl(shell, false);
    }

    private Button findRadioButton(String text)
    {
        Button button = findButton(blockControl, text);
        assertNotNull(button, () -> "button not found: " + text);
        return button;
    }

    /**
     * Simulates the selection of the given radio button: radio buttons of the
     * same parent form a group whose members are deselected when one of them
     * gets selected by the user.
     */
    private void selectRadioButton(Button target)
    {
        for (Control control : target.getParent().getChildren())
        {
            if(control instanceof Button button && (button.getStyle() & SWT.RADIO) != 0)
            {
                button.setSelection(button == target);
            }
        }
        target.notifyListeners(SWT.Selection, new Event());
    }

    private void save()
    {
        block.saveProperties();
    }

    @Test
    public void should_initialize_controls_from_workspace_preferences()
    {
        createBlock();

        Button junit5 = findRadioButton("Junit 5");
        assertTrue(junit5.getSelection());

        // default test method type has no prefix
        Button methodPrefixButton = findRadioButton(PreferenceConstants.TEXT_TEST_METHOD_TYPE);
        assertFalse(methodPrefixButton.getSelection());
        assertTrue(methodPrefixButton.getEnabled());

        Text methodContentField = findTextByLabel(blockControl, PreferenceConstants.TEXT_TEST_METHOD_CONTENT);
        assertEquals(PreferenceConstants.DEFAULT_TEST_METHOD_DEFAULT_CONTENT, methodContentField.getText());

        assertEquals("", findTextByLabel(blockControl, PreferenceConstants.TEXT_PACKAGE_PREFIX).getText());
        assertEquals("", findTextByLabel(blockControl, PreferenceConstants.TEXT_PACKAGE_SUFFIX).getText());
        assertEquals("", findTextByLabel(blockControl, PreferenceConstants.TEXT_TEST_SUPERCLASS).getText());

        assertTrue(findRadioButton(PreferenceConstants.TEXT_EXTENDED_TEST_METHOD_SEARCH).getSelection());
        assertTrue(findRadioButton(PreferenceConstants.TEXT_ENABLE_TEST_METHOD_SEARCH_BY_NAME).getSelection());

        Button codeMiningButton = findRadioButton(PreferenceConstants.TEXT_ENABLE_MOREUNIT_CODEMINING);
        assertTrue(codeMiningButton.getSelection());
        assertTrue(findRadioButton(PreferenceConstants.TEXT_ENABLE_JUMP_TO_METHOD_CODE_MINING).getEnabled());
        assertTrue(findRadioButton(PreferenceConstants.TEXT_ENABLE_JUMP_TO_CLASS_CODE_MINING).getEnabled());
    }

    @Test
    public void should_disable_method_prefix_checkbox_when_junit3_is_selected()
    {
        createBlock();

        selectRadioButton(findRadioButton("JUnit 3.8"));

        assertFalse(findRadioButton(PreferenceConstants.TEXT_TEST_METHOD_TYPE).getEnabled());
    }

    @Test
    public void should_enable_method_prefix_checkbox_when_non_junit3_type_is_selected()
    {
        createBlock();

        selectRadioButton(findRadioButton("JUnit 3.8"));
        selectRadioButton(findRadioButton("Junit 4"));

        assertTrue(findRadioButton(PreferenceConstants.TEXT_TEST_METHOD_TYPE).getEnabled());
    }

    @Test
    public void should_save_selected_test_type()
    {
        createBlock();

        selectRadioButton(findRadioButton("Junit 4"));
        save();
        assertEquals(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4, testTypeInStore());

        selectRadioButton(findRadioButton("TestNG"));
        save();
        assertEquals(PreferenceConstants.TEST_TYPE_VALUE_TESTNG, testTypeInStore());

        selectRadioButton(findRadioButton("Spock"));
        save();
        assertEquals(PreferenceConstants.TEST_TYPE_VALUE_SPOCK, testTypeInStore());
    }

    @Test
    public void should_save_test_method_prefix_preference()
    {
        createBlock();

        selectRadioButton(findRadioButton("Junit 4"));

        Button methodPrefixButton = findRadioButton(PreferenceConstants.TEXT_TEST_METHOD_TYPE);
        methodPrefixButton.setSelection(true);
        save();
        assertEquals(PreferenceConstants.TEST_METHOD_TYPE_JUNIT3, Preferences.getInstance().getProjectStore(javaProject).getString(PreferenceConstants.TEST_METHOD_TYPE));

        methodPrefixButton.setSelection(false);
        save();
        assertEquals(PreferenceConstants.TEST_METHOD_TYPE_NO_PREFIX, Preferences.getInstance().getProjectStore(javaProject).getString(PreferenceConstants.TEST_METHOD_TYPE));
    }

    @Test
    public void should_always_use_prefix_when_junit3_is_selected()
    {
        createBlock();

        selectRadioButton(findRadioButton("JUnit 3.8"));

        Button methodPrefixButton = findRadioButton(PreferenceConstants.TEXT_TEST_METHOD_TYPE);
        methodPrefixButton.setSelection(false);
        save();

        assertEquals(PreferenceConstants.TEST_METHOD_TYPE_JUNIT3, Preferences.getInstance().getProjectStore(javaProject).getString(PreferenceConstants.TEST_METHOD_TYPE));
    }

    @Test
    public void should_save_text_field_values()
    {
        createBlock();

        findTextByLabel(blockControl, PreferenceConstants.TEXT_TEST_METHOD_CONTENT).setText("// custom content");
        findTextByLabel(blockControl, PreferenceConstants.TEXT_PACKAGE_PREFIX).setText("test");
        findTextByLabel(blockControl, PreferenceConstants.TEXT_PACKAGE_SUFFIX).setText(".test");
        findTextByLabel(blockControl, PreferenceConstants.TEXT_TEST_SUPERCLASS).setText("junit.framework.TestCase");

        save();

        var projectStore = Preferences.getInstance().getProjectStore(javaProject);
        assertEquals("// custom content", projectStore.getString(PreferenceConstants.TEST_METHOD_DEFAULT_CONTENT));
        assertEquals("test", projectStore.getString(PreferenceConstants.TEST_PACKAGE_PREFIX));
        assertEquals(".test", projectStore.getString(PreferenceConstants.TEST_PACKAGE_SUFFIX));
        assertEquals("junit.framework.TestCase", projectStore.getString(PreferenceConstants.TEST_SUPERCLASS));
    }

    @Test
    public void should_save_test_file_name_template()
    {
        createBlock();

        Text patternField = findTextByLabel(blockControl, "Pattern:");
        patternField.setText(SRC_FILE_VARIABLE + "IT");

        save();

        assertEquals(SRC_FILE_VARIABLE + "IT", Preferences.forProject(javaProject).getTestClassNameTemplate());
    }

    @Test
    public void should_restore_defaults_for_pattern()
    {
        createBlock();

        Text patternField = findTextByLabel(blockControl, "Pattern:");
        patternField.setText(SRC_FILE_VARIABLE + "IT");

        TestFileNamePatternGroup patternGroup = (TestFileNamePatternGroup) getField(block, "testCaseNamePatternArea");
        patternGroup.restoreDefaults();

        assertEquals(org.moreunit.core.preferences.Preferences.DEFAULTS.getTestFileNameTemplate(), patternField.getText());
    }

    @Test
    public void should_force_search_by_name_when_extended_search_gets_unchecked()
    {
        createBlock();

        Button extendedSearchButton = findRadioButton(PreferenceConstants.TEXT_EXTENDED_TEST_METHOD_SEARCH);
        Button searchByNameButton = findRadioButton(PreferenceConstants.TEXT_ENABLE_TEST_METHOD_SEARCH_BY_NAME);

        extendedSearchButton.setSelection(false);
        extendedSearchButton.notifyListeners(SWT.Selection, new Event());

        assertTrue(searchByNameButton.getSelection());
    }

    @Test
    public void should_force_extended_search_when_search_by_name_gets_unchecked()
    {
        createBlock();

        Button extendedSearchButton = findRadioButton(PreferenceConstants.TEXT_EXTENDED_TEST_METHOD_SEARCH);
        Button searchByNameButton = findRadioButton(PreferenceConstants.TEXT_ENABLE_TEST_METHOD_SEARCH_BY_NAME);

        searchByNameButton.setSelection(false);
        searchByNameButton.notifyListeners(SWT.Selection, new Event());

        assertTrue(extendedSearchButton.getSelection());
    }

    @Test
    public void should_save_method_search_mode()
    {
        createBlock();

        findRadioButton(PreferenceConstants.TEXT_EXTENDED_TEST_METHOD_SEARCH).setSelection(true);
        findRadioButton(PreferenceConstants.TEXT_ENABLE_TEST_METHOD_SEARCH_BY_NAME).setSelection(false);
        save();

        Preferences.MethodSearchMode mode = Preferences.getInstance().getMethodSearchMode(javaProject);
        assertTrue(mode.searchByCall);
        assertFalse(mode.searchByName);
    }

    @Test
    public void should_disable_jump_code_mining_checkboxes_when_moreunit_code_mining_gets_disabled()
    {
        createBlock();

        Button codeMiningButton = findRadioButton(PreferenceConstants.TEXT_ENABLE_MOREUNIT_CODEMINING);
        Button jumpToMethodButton = findRadioButton(PreferenceConstants.TEXT_ENABLE_JUMP_TO_METHOD_CODE_MINING);
        Button jumpToClassButton = findRadioButton(PreferenceConstants.TEXT_ENABLE_JUMP_TO_CLASS_CODE_MINING);

        codeMiningButton.setSelection(false);
        codeMiningButton.notifyListeners(SWT.Selection, new Event());

        assertFalse(jumpToMethodButton.getEnabled());
        assertFalse(jumpToClassButton.getEnabled());

        codeMiningButton.setSelection(true);
        codeMiningButton.notifyListeners(SWT.Selection, new Event());

        assertTrue(jumpToMethodButton.getEnabled());
        assertTrue(jumpToClassButton.getEnabled());
    }

    @Test
    public void should_save_code_mining_preferences()
    {
        createBlock();

        findRadioButton(PreferenceConstants.TEXT_ENABLE_MOREUNIT_CODEMINING).setSelection(false);
        findRadioButton(PreferenceConstants.TEXT_ENABLE_JUMP_TO_METHOD_CODE_MINING).setSelection(false);
        findRadioButton(PreferenceConstants.TEXT_ENABLE_JUMP_TO_CLASS_CODE_MINING).setSelection(false);

        save();

        var projectStore = Preferences.getInstance().getProjectStore(javaProject);
        assertFalse(projectStore.getBoolean(PreferenceConstants.ENABLE_MOREUNIT_CODE_MINING));
        assertFalse(projectStore.getBoolean(PreferenceConstants.ENABLE_JUMP_TO_METHOD_CODE_MINING));
        assertFalse(projectStore.getBoolean(PreferenceConstants.ENABLE_JUMP_TO_CLASS_CODE_MINING));
    }

    @Test
    public void should_save_comments_preference()
    {
        createBlock();

        findRadioButton(PreferenceConstants.TEXT_GENERATE_COMMENTS_FOR_TEST_METHOD).setSelection(true);
        save();

        assertTrue(Preferences.getInstance().getProjectStore(javaProject).getBoolean(PreferenceConstants.GENERATE_COMMENTS_FOR_TEST_METHOD));
    }

    @Test
    public void should_save_annotation_mode()
    {
        createBlock();

        save();
        assertEquals(org.moreunit.preferences.TestAnnotationMode.OFF.name(), Preferences.getInstance().getProjectStore(javaProject).getString(PreferenceConstants.TEST_ANNOTATION_MODE));

        selectRadioButton(findRadioButton(PreferenceConstants.TEST_ANNOTATION_MODE_BY_NAME));
        save();
        assertEquals(org.moreunit.preferences.TestAnnotationMode.BY_NAME.name(), Preferences.getInstance().getProjectStore(javaProject).getString(PreferenceConstants.TEST_ANNOTATION_MODE));

        selectRadioButton(findRadioButton(PreferenceConstants.TEST_ANNOTATION_MODE_EXTENDED_SEARCH));
        save();
        assertEquals(org.moreunit.preferences.TestAnnotationMode.BY_CALL_AND_BY_NAME.name(), Preferences.getInstance().getProjectStore(javaProject).getString(PreferenceConstants.TEST_ANNOTATION_MODE));
    }

    @Test
    public void should_return_error_when_pattern_is_invalid()
    {
        createBlock();

        Text patternField = findTextByLabel(blockControl, "Pattern:");

        patternField.setText("");
        assertEquals("You must enter a rule for naming test files", block.getError());

        patternField.setText(SRC_FILE_VARIABLE);
        assertEquals("Test files must have a name different from their corresponding source file", block.getError());

        patternField.setText(SRC_FILE_VARIABLE + "(Test|IT)");
        assertNull(block.getError());
    }

    @Test
    public void should_return_warning_when_pattern_uses_too_many_wildcards()
    {
        createBlock();

        Text patternField = findTextByLabel(blockControl, "Pattern:");
        patternField.setText(SRC_FILE_VARIABLE + "*Test*");

        assertNotNull(block.getWarning());
    }

    @Test
    public void should_propagate_modify_events_from_pattern_field()
    {
        createBlock();

        AtomicInteger modificationCount = new AtomicInteger();
        block.addModifyListener(new ModifyListener()
        {
            @Override
            public void modifyText(ModifyEvent e)
            {
                modificationCount.incrementAndGet();
            }
        });

        findTextByLabel(blockControl, "Pattern:").setText(SRC_FILE_VARIABLE + "Test");

        assertEquals(1, modificationCount.get());
    }

    @Test
    public void should_disable_all_controls_when_block_gets_disabled()
    {
        createBlock();

        block.setEnabled(false);

        assertFalse(findRadioButton("Junit 5").getEnabled());
        assertFalse(findRadioButton(PreferenceConstants.TEXT_TEST_METHOD_TYPE).getEnabled());
        assertFalse(findTextByLabel(blockControl, PreferenceConstants.TEXT_PACKAGE_PREFIX).getEnabled());
        assertFalse(findRadioButton(PreferenceConstants.TEXT_EXTENDED_TEST_METHOD_SEARCH).getEnabled());

        block.setEnabled(true);

        assertTrue(findRadioButton("Junit 5").getEnabled());
        assertTrue(findTextByLabel(blockControl, PreferenceConstants.TEXT_PACKAGE_PREFIX).getEnabled());
    }

    @Test
    public void should_keep_jump_code_mining_checkboxes_disabled_when_block_is_enabled_but_code_mining_is_off()
    {
        createBlock();

        findRadioButton(PreferenceConstants.TEXT_ENABLE_MOREUNIT_CODEMINING).setSelection(false);
        findRadioButton(PreferenceConstants.TEXT_ENABLE_MOREUNIT_CODEMINING).notifyListeners(SWT.Selection, new Event());

        block.setEnabled(true);

        assertFalse(findRadioButton(PreferenceConstants.TEXT_ENABLE_JUMP_TO_METHOD_CODE_MINING).getEnabled());
        assertFalse(findRadioButton(PreferenceConstants.TEXT_ENABLE_JUMP_TO_CLASS_CODE_MINING).getEnabled());
    }

    @Test
    public void should_return_empty_test_type_when_no_type_is_selected()
    {
        createBlock();

        for (Control control : findRadioButton("Junit 5").getParent().getChildren())
        {
            if(control instanceof Button button && (button.getStyle() & SWT.RADIO) != 0)
            {
                button.setSelection(false);
            }
        }

        save();

        assertEquals("", testTypeInStore());
    }

    @Test
    public void should_accept_default_selection_events()
    {
        createBlock();

        Event event = new Event();
        event.widget = blockControl;
        block.widgetDefaultSelected(new SelectionEvent(event));
    }

    @Test
    public void should_preselect_junit3_radio_and_method_prefix_when_project_uses_junit3()
    {
        Preferences.getInstance().setTestType(javaProject, PreferenceConstants.TEST_TYPE_VALUE_JUNIT_3);
        Preferences.getInstance().setTestMethodTypeShouldUsePrefix(javaProject, true);
        createBlock();

        assertTrue(findRadioButton("JUnit 3.8").getSelection());
        assertTrue(findRadioButton(PreferenceConstants.TEXT_TEST_METHOD_TYPE).getSelection());
    }

    @Test
    public void should_preselect_junit4_radio_when_project_uses_junit4()
    {
        Preferences.getInstance().setTestType(javaProject, PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4);
        createBlock();

        assertTrue(findRadioButton("Junit 4").getSelection());
    }

    @Test
    public void should_preselect_by_name_annotation_mode_radio_when_project_uses_it()
    {
        Preferences.getInstance().setTestAnnotationMode(javaProject, org.moreunit.preferences.TestAnnotationMode.BY_NAME);
        createBlock();

        assertTrue(findRadioButton(PreferenceConstants.TEST_ANNOTATION_MODE_BY_NAME).getSelection());
        assertFalse(findRadioButton(PreferenceConstants.TEST_ANNOTATION_MODE_DISABLED).getSelection());
    }

    @Test
    public void should_preselect_extended_annotation_mode_radio_when_project_uses_it()
    {
        Preferences.getInstance().setTestAnnotationMode(javaProject, org.moreunit.preferences.TestAnnotationMode.BY_CALL_AND_BY_NAME);
        createBlock();

        assertTrue(findRadioButton(PreferenceConstants.TEST_ANNOTATION_MODE_EXTENDED_SEARCH).getSelection());
        assertFalse(findRadioButton(PreferenceConstants.TEST_ANNOTATION_MODE_DISABLED).getSelection());
    }

    @Test
    public void should_use_empty_word_separator_for_camel_case_patterns()
    {
        createBlock();

        TestFileNamePatternGroup patternGroup = (TestFileNamePatternGroup) getField(block, "testCaseNamePatternArea");
        var prefWriter = (org.moreunit.core.preferences.TestFileNamePatternPreferencesWriter) getField(patternGroup, "prefWriter");

        assertEquals("", prefWriter.getFileWordSeparator());
    }

    private String testTypeInStore()
    {
        return Preferences.getInstance().getProjectStore(javaProject).getString(PreferenceConstants.TEST_TYPE);
    }
}
