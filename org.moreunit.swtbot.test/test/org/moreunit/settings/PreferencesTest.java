package org.moreunit.settings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swtbot.swt.finder.junit5.SWTBotJunit5Extension;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.moreunit.JavaProjectSWTBotTestHelper;
import org.moreunit.preferences.PreferenceConstants;
import org.moreunit.preferences.Preferences;
import org.moreunit.test.context.Context;

@Context(mainCls = "org:HelloWorld")
@ExtendWith(SWTBotJunit5Extension.class)
public class PreferencesTest extends JavaProjectSWTBotTestHelper
{
    private void openPreferencesAndSelectMoreUnitPage()
    {
        getShortcutStrategy().openPreferences();
        bot.waitUntil(org.eclipse.swtbot.swt.finder.waits.Conditions.shellIsActive("Preferences"), 20000);
        bot.shell("Preferences").activate();
        bot.shell("Preferences").setFocus();
        bot.tree().expandNode("MoreUnit").select("Java");
    }

    @Test
    public void should_update_test_source_folder_when_preferences_change()
    {
        openPreferencesAndSelectMoreUnitPage();
        SWTBotText sourceFolderTextField = bot.textWithLabel(PreferenceConstants.TEXT_TEST_SOURCE_FOLDER);
        sourceFolderTextField.setText("unittest");
        saveAndClosePrefs();

        String junitDirectoryFromPreferences = Preferences.getInstance().getJunitDirectoryFromPreferences(getJavaProjectFromContext());
        assertEquals("unittest", junitDirectoryFromPreferences);
    }

    private void saveAndClosePrefs()
    {
        // in newer version (at least 4.8), the label has been changed and is stored in a preference
        String label = JFaceResources.getString("PreferencesDialog.okButtonLabel");
        bot.button("PreferencesDialog.okButtonLabel".equals(label)? "OK" : label).click();
    }

    @Test
    public void should_update_test_type_when_preferences_change()
    {
        openPreferencesAndSelectMoreUnitPage();
        bot.radio(PreferenceConstants.TEXT_JUNIT_3_8).click();
        saveAndClosePrefs();
        String testType = Preferences.getInstance().getTestType(getJavaProjectFromContext());
        assertEquals(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_3, testType);

        openPreferencesAndSelectMoreUnitPage();
        bot.radio(PreferenceConstants.TEXT_JUNIT_4).click();
        saveAndClosePrefs();
        testType = Preferences.getInstance().getTestType(getJavaProjectFromContext());
        assertEquals(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4, testType);

        openPreferencesAndSelectMoreUnitPage();
        bot.radio(PreferenceConstants.TEXT_JUNIT_5).click();
        saveAndClosePrefs();
        testType = Preferences.getInstance().getTestType(getJavaProjectFromContext());
        assertEquals(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_5, testType);

        openPreferencesAndSelectMoreUnitPage();
        bot.radio(PreferenceConstants.TEXT_SPOCK).click();
        saveAndClosePrefs();
        testType = Preferences.getInstance().getTestType(getJavaProjectFromContext());
        assertEquals(PreferenceConstants.TEST_TYPE_VALUE_SPOCK, testType);

        openPreferencesAndSelectMoreUnitPage();
        bot.radio(PreferenceConstants.TEXT_TEST_NG).click();
        saveAndClosePrefs();
        testType = Preferences.getInstance().getTestType(getJavaProjectFromContext());
        assertEquals(PreferenceConstants.TEST_TYPE_VALUE_TESTNG, testType);
    }

    @Test
    public void should_update_test_method_prefix_when_preferences_change()
    {
        openPreferencesAndSelectMoreUnitPage();
        bot.radio(PreferenceConstants.TEXT_JUNIT_4).click();
        bot.checkBox(PreferenceConstants.TEXT_TEST_METHOD_TYPE).select();
        saveAndClosePrefs();
        String testMethodType = Preferences.getInstance().getTestMethodType(getJavaProjectFromContext());
        assertEquals(PreferenceConstants.TEST_METHOD_TYPE_JUNIT3, testMethodType);

        openPreferencesAndSelectMoreUnitPage();
        bot.checkBox(PreferenceConstants.TEXT_TEST_METHOD_TYPE).deselect();
        saveAndClosePrefs();
        testMethodType = Preferences.getInstance().getTestMethodType(getJavaProjectFromContext());
        assertEquals(PreferenceConstants.TEST_METHOD_TYPE_NO_PREFIX, testMethodType);
    }

    @Test
    public void should_update_test_method_content_when_preferences_change()
    {
        openPreferencesAndSelectMoreUnitPage();
        bot.textWithLabel(PreferenceConstants.TEXT_TEST_METHOD_CONTENT).setText("blubbContent");
        saveAndClosePrefs();
        String testMethodDefaultContent = Preferences.getInstance().getTestMethodDefaultContent(getJavaProjectFromContext());
        assertEquals("blubbContent", testMethodDefaultContent);
    }

    @Test
    public void should_update_test_name_template_when_preferences_change()
    {
        openPreferencesAndSelectMoreUnitPage();
        bot.textWithLabel("Pattern:").setText("${srcFile}(Test|ITTest)");
        saveAndClosePrefs();
        String template = Preferences.forProject(getJavaProjectFromContext()).getTestClassNameTemplate();
        assertEquals("${srcFile}(Test|ITTest)", template);
    }

    @Test
    public void should_update_test_package_prefix_when_preferences_change()
    {
        openPreferencesAndSelectMoreUnitPage();
        bot.textWithLabel(PreferenceConstants.TEXT_PACKAGE_PREFIX).setText("pckgprefix");
        saveAndClosePrefs();
        String testPackagePrefix = Preferences.getInstance().getTestPackagePrefix(getJavaProjectFromContext());
        assertEquals("pckgprefix", testPackagePrefix);
    }

    @Test
    public void should_update_test_package_suffix_when_preferences_change()
    {
        openPreferencesAndSelectMoreUnitPage();
        bot.textWithLabel(PreferenceConstants.TEXT_PACKAGE_SUFFIX).setText("pckgsuffix");
        saveAndClosePrefs();
        String testPackageSuffix = Preferences.getInstance().getTestPackageSuffix(getJavaProjectFromContext());
        assertEquals("pckgsuffix", testPackageSuffix);
    }

    @Test
    public void should_update_test_superclass_when_preferences_change()
    {
        openPreferencesAndSelectMoreUnitPage();
        bot.textWithLabel(PreferenceConstants.TEXT_TEST_SUPERCLASS).setText("org.moreunit.SuperClass");
        saveAndClosePrefs();
        String testSuperClass = Preferences.getInstance().getTestSuperClass(getJavaProjectFromContext());
        assertEquals("org.moreunit.SuperClass", testSuperClass);
    }

    @Test
    public void should_enable_extended_method_search_when_preferences_change()
    {
        openPreferencesAndSelectMoreUnitPage();
        bot.checkBox(PreferenceConstants.TEXT_EXTENDED_TEST_METHOD_SEARCH).select();
        saveAndClosePrefs();
        assertTrue(Preferences.getInstance().getMethodSearchMode(getJavaProjectFromContext()).searchByCall);
        assertTrue(Preferences.getInstance().getMethodSearchMode(getJavaProjectFromContext()).searchByName);

        openPreferencesAndSelectMoreUnitPage();
        bot.checkBox(PreferenceConstants.TEXT_EXTENDED_TEST_METHOD_SEARCH).deselect();
        saveAndClosePrefs();
        assertFalse(Preferences.getInstance().getMethodSearchMode(getJavaProjectFromContext()).searchByCall);
        assertTrue(Preferences.getInstance().getMethodSearchMode(getJavaProjectFromContext()).searchByName);
    }

    @Test
    public void should_update_code_mining_preferences_when_preferences_change()
    {
        openPreferencesAndSelectMoreUnitPage();
        bot.checkBox(PreferenceConstants.TEXT_ENABLE_MOREUNIT_CODEMINING).select();
        bot.checkBox(PreferenceConstants.TEXT_ENABLE_JUMP_TO_METHOD_CODE_MINING).deselect();
        bot.checkBox(PreferenceConstants.TEXT_ENABLE_JUMP_TO_CLASS_CODE_MINING).deselect();
        saveAndClosePrefs();
        assertTrue(Preferences.getInstance().shouldEnableMoreUnitCodeMining(getJavaProjectFromContext()));
        assertFalse(Preferences.getInstance().shouldEnableJumpToMethodCodeMining(getJavaProjectFromContext()));
        assertFalse(Preferences.getInstance().shouldEnableJumpToClassCodeMining(getJavaProjectFromContext()));

        openPreferencesAndSelectMoreUnitPage();
        bot.checkBox(PreferenceConstants.TEXT_ENABLE_JUMP_TO_METHOD_CODE_MINING).select();
        bot.checkBox(PreferenceConstants.TEXT_ENABLE_JUMP_TO_CLASS_CODE_MINING).select();
        saveAndClosePrefs();
        assertTrue(Preferences.getInstance().shouldEnableJumpToMethodCodeMining(getJavaProjectFromContext()));
        assertTrue(Preferences.getInstance().shouldEnableJumpToClassCodeMining(getJavaProjectFromContext()));
    }
}
