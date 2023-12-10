package org.moreunit.settings;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.moreunit.JavaProjectSWTBotTestHelper;
import org.moreunit.preferences.PreferenceConstants;
import org.moreunit.preferences.Preferences;
import org.moreunit.test.context.Context;

@Context(mainCls = "org:HelloWorld")
@RunWith(SWTBotJunit4ClassRunner.class)
@Ignore
public class PreferencesTest extends JavaProjectSWTBotTestHelper
{
    private void openPreferencesAndSelectMoreUnitPage()
    {
        getShortcutStrategy().openPreferences();
        bot.waitUntil(Conditions.shellIsActive("Preferences"), 20000);
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
        assertThat(junitDirectoryFromPreferences).isEqualTo("unittest");
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
        assertThat(testType).isEqualTo(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_3);

        openPreferencesAndSelectMoreUnitPage();
        bot.radio(PreferenceConstants.TEXT_JUNIT_4).click();
        saveAndClosePrefs();
        testType = Preferences.getInstance().getTestType(getJavaProjectFromContext());
        assertThat(testType).isEqualTo(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4);

        openPreferencesAndSelectMoreUnitPage();
        bot.radio(PreferenceConstants.TEXT_JUNIT_5).click();
        saveAndClosePrefs();
        testType = Preferences.getInstance().getTestType(getJavaProjectFromContext());
        assertThat(testType).isEqualTo(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_5);

        openPreferencesAndSelectMoreUnitPage();
        bot.radio(PreferenceConstants.TEXT_SPOCK).click();
        saveAndClosePrefs();
        testType = Preferences.getInstance().getTestType(getJavaProjectFromContext());
        assertThat(testType).isEqualTo(PreferenceConstants.TEST_TYPE_VALUE_SPOCK);

        openPreferencesAndSelectMoreUnitPage();
        bot.radio(PreferenceConstants.TEXT_TEST_NG).click();
        saveAndClosePrefs();
        testType = Preferences.getInstance().getTestType(getJavaProjectFromContext());
        assertThat(testType).isEqualTo(PreferenceConstants.TEST_TYPE_VALUE_TESTNG);
    }

    @Test
    public void should_update_test_method_prefix_when_preferences_change()
    {
        openPreferencesAndSelectMoreUnitPage();
        bot.radio(PreferenceConstants.TEXT_JUNIT_4).click();
        bot.checkBox(PreferenceConstants.TEXT_TEST_METHOD_TYPE).select();
        saveAndClosePrefs();
        String testMethodType = Preferences.getInstance().getTestMethodType(getJavaProjectFromContext());
        assertThat(testMethodType).isEqualTo(PreferenceConstants.TEST_METHOD_TYPE_JUNIT3);

        openPreferencesAndSelectMoreUnitPage();
        bot.checkBox(PreferenceConstants.TEXT_TEST_METHOD_TYPE).deselect();
        saveAndClosePrefs();
        testMethodType = Preferences.getInstance().getTestMethodType(getJavaProjectFromContext());
        assertThat(testMethodType).isEqualTo(PreferenceConstants.TEST_METHOD_TYPE_NO_PREFIX);
    }

    @Test
    public void should_update_test_method_content_when_preferences_change()
    {
        openPreferencesAndSelectMoreUnitPage();
        bot.textWithLabel(PreferenceConstants.TEXT_TEST_METHOD_CONTENT).setText("blubbContent");
        saveAndClosePrefs();
        String testMethodDefaultContent = Preferences.getInstance().getTestMethodDefaultContent(getJavaProjectFromContext());
        assertThat(testMethodDefaultContent).isEqualTo("blubbContent");
    }

    @Test
    public void should_update_test_name_template_when_preferences_change()
    {
        openPreferencesAndSelectMoreUnitPage();
        bot.textWithLabel("Pattern:").setText("${srcFile}(Test|ITTest)");
        saveAndClosePrefs();
        String template = Preferences.forProject(getJavaProjectFromContext()).getTestClassNameTemplate();
        assertThat(template).isEqualTo("${srcFile}(Test|ITTest)");
    }

    @Test
    public void should_update_test_package_prefix_when_preferences_change()
    {
        openPreferencesAndSelectMoreUnitPage();
        bot.textWithLabel(PreferenceConstants.TEXT_PACKAGE_PREFIX).setText("pckgprefix");
        saveAndClosePrefs();
        String testPackagePrefix = Preferences.getInstance().getTestPackagePrefix(getJavaProjectFromContext());
        assertThat(testPackagePrefix).isEqualTo("pckgprefix");
    }

    @Test
    public void should_update_test_package_suffix_when_preferences_change()
    {
        openPreferencesAndSelectMoreUnitPage();
        bot.textWithLabel(PreferenceConstants.TEXT_PACKAGE_SUFFIX).setText("pckgsuffix");
        saveAndClosePrefs();
        String testPackageSuffix = Preferences.getInstance().getTestPackageSuffix(getJavaProjectFromContext());
        assertThat(testPackageSuffix).isEqualTo("pckgsuffix");
    }

    @Test
    public void should_update_test_superclass_when_preferences_change()
    {
        openPreferencesAndSelectMoreUnitPage();
        bot.textWithLabel(PreferenceConstants.TEXT_TEST_SUPERCLASS).setText("org.moreunit.SuperClass");
        saveAndClosePrefs();
        String testSuperClass = Preferences.getInstance().getTestSuperClass(getJavaProjectFromContext());
        assertThat(testSuperClass).isEqualTo("org.moreunit.SuperClass");
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
}
