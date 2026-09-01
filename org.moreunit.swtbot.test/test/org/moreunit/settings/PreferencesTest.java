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

/**
 * Each opening of the Preferences dialog is expensive (the dialog loads every
 * preference page of the IDE, which takes several seconds on CI runners).
 * Tests are therefore grouped so that the dialog is opened as few times as
 * possible, while keeping the exact same assertions as before.
 */
@Context(mainCls = "org:HelloWorld")
@ExtendWith(SWTBotJunit5Extension.class)
public class PreferencesTest extends JavaProjectSWTBotTestHelper
{
    private void openPreferencesAndSelectMoreUnitPage()
    {
        try { bot.shell("Preferences").close(); } catch (Exception e) { }
        getShortcutStrategy().openPreferences();
        bot.waitUntil(org.eclipse.swtbot.swt.finder.waits.Conditions.shellIsActive("Preferences"), 20000);
        bot.shell("Preferences").activate();
        bot.shell("Preferences").setFocus();
        bot.tree().expandNode("MoreUnit").select("Java");
    }

    @Test
    public void should_update_fields_when_preferences_change()
    {
        openPreferencesAndSelectMoreUnitPage();

        SWTBotText sourceFolderTextField = bot.textWithLabel(PreferenceConstants.TEXT_TEST_SOURCE_FOLDER);
        sourceFolderTextField.setText("unittest");
        bot.textWithLabel(PreferenceConstants.TEXT_PACKAGE_PREFIX).setText("pckgprefix");
        bot.textWithLabel(PreferenceConstants.TEXT_PACKAGE_SUFFIX).setText("pckgsuffix");
        bot.textWithLabel(PreferenceConstants.TEXT_TEST_SUPERCLASS).setText("org.moreunit.SuperClass");
        bot.textWithLabel(PreferenceConstants.TEXT_TEST_METHOD_CONTENT).setText("blubbContent");
        bot.textWithLabel("Pattern:").setText("${srcFile}(Test|ITTest)");
        bot.checkBox(PreferenceConstants.TEXT_EXTENDED_TEST_METHOD_SEARCH).select();
        bot.checkBox(PreferenceConstants.TEXT_ENABLE_MOREUNIT_CODEMINING).select();
        bot.checkBox(PreferenceConstants.TEXT_ENABLE_JUMP_TO_METHOD_CODE_MINING).deselect();
        bot.checkBox(PreferenceConstants.TEXT_ENABLE_JUMP_TO_CLASS_CODE_MINING).deselect();
        saveAndClosePrefs();

        String junitDirectoryFromPreferences = Preferences.getInstance().getJunitDirectoryFromPreferences(getJavaProjectFromContext());
        assertEquals("unittest", junitDirectoryFromPreferences);

        String testPackagePrefix = Preferences.getInstance().getTestPackagePrefix(getJavaProjectFromContext());
        assertEquals("pckgprefix", testPackagePrefix);

        String testPackageSuffix = Preferences.getInstance().getTestPackageSuffix(getJavaProjectFromContext());
        assertEquals("pckgsuffix", testPackageSuffix);

        String testSuperClass = Preferences.getInstance().getTestSuperClass(getJavaProjectFromContext());
        assertEquals("org.moreunit.SuperClass", testSuperClass);

        String testMethodDefaultContent = Preferences.getInstance().getTestMethodDefaultContent(getJavaProjectFromContext());
        assertEquals("blubbContent", testMethodDefaultContent);

        String template = Preferences.forProject(getJavaProjectFromContext()).getTestClassNameTemplate();
        assertEquals("${srcFile}(Test|ITTest)", template);

        assertTrue(Preferences.getInstance().getMethodSearchMode(getJavaProjectFromContext()).searchByCall);
        assertTrue(Preferences.getInstance().getMethodSearchMode(getJavaProjectFromContext()).searchByName);

        assertTrue(Preferences.getInstance().shouldEnableMoreUnitCodeMining(getJavaProjectFromContext()));
        assertFalse(Preferences.getInstance().shouldEnableJumpToMethodCodeMining(getJavaProjectFromContext()));
        assertFalse(Preferences.getInstance().shouldEnableJumpToClassCodeMining(getJavaProjectFromContext()));
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
    public void should_update_toggles_when_preferences_change()
    {
        openPreferencesAndSelectMoreUnitPage();
        bot.radio(PreferenceConstants.TEXT_JUNIT_4).click();
        bot.checkBox(PreferenceConstants.TEXT_TEST_METHOD_TYPE).select();
        saveAndClosePrefs();
        String testMethodType = Preferences.getInstance().getTestMethodType(getJavaProjectFromContext());
        assertEquals(PreferenceConstants.TEST_METHOD_TYPE_JUNIT3, testMethodType);

        openPreferencesAndSelectMoreUnitPage();
        bot.checkBox(PreferenceConstants.TEXT_TEST_METHOD_TYPE).deselect();
        bot.checkBox(PreferenceConstants.TEXT_EXTENDED_TEST_METHOD_SEARCH).deselect();
        saveAndClosePrefs();
        testMethodType = Preferences.getInstance().getTestMethodType(getJavaProjectFromContext());
        assertEquals(PreferenceConstants.TEST_METHOD_TYPE_NO_PREFIX, testMethodType);

        assertFalse(Preferences.getInstance().getMethodSearchMode(getJavaProjectFromContext()).searchByCall);
        assertTrue(Preferences.getInstance().getMethodSearchMode(getJavaProjectFromContext()).searchByName);
    }

    private void saveAndClosePrefs()
    {
        // in newer version (at least 4.8), the label has been changed and is stored in a preference
        String label = JFaceResources.getString("PreferencesDialog.okButtonLabel");
        bot.button("PreferencesDialog.okButtonLabel".equals(label)? "OK" : label).click();
    }
}
