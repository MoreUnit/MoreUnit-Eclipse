package org.moreunit.settings;

import static org.eclipse.swtbot.swt.finder.waits.Conditions.waitForShell;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Before;
import org.junit.Test;
import org.moreunit.JavaProjectSWTBotTestHelper;
import org.moreunit.preferences.PreferenceConstants;
import org.moreunit.preferences.Preferences;
import org.moreunit.test.context.Project;

@Project(mainCls = "org:HelloWorld", mainSrcFolder = "src", testSrcFolder = "test")
@org.moreunit.test.context.Preferences(testSrcFolder = "junit")
public class PropertiesTest extends JavaProjectSWTBotTestHelper
{
    private void openProjectPropertiesAndSelectMoreUnitPage()
    {
        SWTBotTreeItem projectItem = selectAndReturnJavaProjectFromPackageExplorer();
        getShortcutStrategy().openProperties(projectItem);
        bot.waitUntil(waitForShell(shellWithTextStartingWith("Properties for ")));
        bot.tree().expandNode("MoreUnit").select("Java");
    }

    private void initProjectSpecificSettings()
    {
        openProjectPropertiesAndSelectMoreUnitPage();
        bot.checkBox("Use project specific settings").select();
        SWTBotShell propertiesDialogShell = bot.activeShell();
        bot.button("Add").click();

        SWTBotTreeItem projectItem = bot.tree().getAllItems()[0];
        projectItem.expand();
        SWTBotShell addFolderDialog = bot.activeShell();
        projectItem.getNode("test").select().check();
        bot.button("Finish").click();
        bot.waitUntil(Conditions.shellCloses(addFolderDialog));
        propertiesDialogShell.setFocus();
        propertiesDialogShell.activate();
        saveAndCloseProps();
        assertTrue(Preferences.getInstance().hasProjectSpecificSettings(getJavaProjectFromContext()));
    }

    private void saveAndCloseProps()
    {
        bot.button("OK").click();
    }

    @Before
    public void before()
    {
        initProjectSpecificSettings();
    }

    @Test
    public void should_update_test_type_when_property_changes()
    {
        openPropertiesAndActivateOtherTab();
        bot.radio(PreferenceConstants.TEXT_JUNIT_3_8).click();
        saveAndCloseProps();
        String testType = Preferences.getInstance().getTestType(getJavaProjectFromContext());
        assertThat(testType).isEqualTo(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_3);

        openPropertiesAndActivateOtherTab();
        bot.radio(PreferenceConstants.TEXT_JUNIT_4).click();
        saveAndCloseProps();
        testType = Preferences.getInstance().getTestType(getJavaProjectFromContext());
        assertThat(testType).isEqualTo(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4);

        openPropertiesAndActivateOtherTab();
        bot.radio(PreferenceConstants.TEXT_TEST_NG).click();
        saveAndCloseProps();
        testType = Preferences.getInstance().getTestType(getJavaProjectFromContext());
        assertThat(testType).isEqualTo(PreferenceConstants.TEST_TYPE_VALUE_TESTNG);
    }

    @Test
    public void should_update_test_method_prefix_when_property_changes()
    {
        openPropertiesAndActivateOtherTab();
        bot.radio(PreferenceConstants.TEXT_JUNIT_4).click();
        bot.checkBox(PreferenceConstants.TEXT_TEST_METHOD_TYPE).select();
        saveAndCloseProps();
        String testMethodType = Preferences.getInstance().getTestMethodType(getJavaProjectFromContext());
        assertThat(testMethodType).isEqualTo(PreferenceConstants.TEST_METHOD_TYPE_JUNIT3);

        openPropertiesAndActivateOtherTab();
        bot.checkBox(PreferenceConstants.TEXT_TEST_METHOD_TYPE).deselect();
        saveAndCloseProps();
        testMethodType = Preferences.getInstance().getTestMethodType(getJavaProjectFromContext());
        assertThat(testMethodType).isEqualTo(PreferenceConstants.TEST_METHOD_TYPE_NO_PREFIX);
    }

    @Test
    public void should_update_test_method_content_when_property_changes()
    {
        openPropertiesAndActivateOtherTab();
        bot.textWithLabel(PreferenceConstants.TEXT_TEST_METHOD_CONTENT).setText("blubberContent");
        saveAndCloseProps();
        String testMethodDefaultContent = Preferences.getInstance().getTestMethodDefaultContent(getJavaProjectFromContext());
        assertThat(testMethodDefaultContent).isEqualTo("blubberContent");
    }

    @Test
    public void should_update_test_prefixes_when_preferences_change()
    {
        openPropertiesAndActivateOtherTab();
        bot.textWithLabel("Pattern:").setText("${srcFile}(Dest|ITDest)");
        saveAndCloseProps();
        String template = Preferences.forProject(getJavaProjectFromContext()).getTestClassNameTemplate();
        assertThat(template).isEqualTo("${srcFile}(Dest|ITDest)");
    }

    @Test
    public void should_update_test_package_prefix_when_preferences_change()
    {
        openPropertiesAndActivateOtherTab();
        bot.textWithLabel(PreferenceConstants.TEXT_PACKAGE_PREFIX).setText("pckgpref");
        saveAndCloseProps();
        String testPackagePrefix = Preferences.getInstance().getTestPackagePrefix(getJavaProjectFromContext());
        assertThat(testPackagePrefix).isEqualTo("pckgpref");
    }

    @Test
    public void should_update_test_package_suffix_when_preferences_change()
    {
        openPropertiesAndActivateOtherTab();
        bot.textWithLabel(PreferenceConstants.TEXT_PACKAGE_SUFFIX).setText("pckgsuff");
        saveAndCloseProps();
        String testPackageSuffix = Preferences.getInstance().getTestPackageSuffix(getJavaProjectFromContext());
        assertThat(testPackageSuffix).isEqualTo("pckgsuff");
    }

    @Test
    public void should_update_test_superclass_when_preferences_change()
    {
        openPropertiesAndActivateOtherTab();
        bot.textWithLabel(PreferenceConstants.TEXT_TEST_SUPERCLASS).setText("org.moreunit.SuperKlass");
        saveAndCloseProps();
        String testSuperClass = Preferences.getInstance().getTestSuperClass(getJavaProjectFromContext());
        assertThat(testSuperClass).isEqualTo("org.moreunit.SuperKlass");
    }

    @Test
    public void should_enable_extended_method_search_when_preferences_change()
    {
        openPropertiesAndActivateOtherTab();
        bot.checkBox(PreferenceConstants.TEXT_EXTENDED_TEST_METHOD_SEARCH).select();
        saveAndCloseProps();
        assertTrue(Preferences.getInstance().getMethodSearchMode(getJavaProjectFromContext()).searchByCall);
        assertTrue(Preferences.getInstance().getMethodSearchMode(getJavaProjectFromContext()).searchByName);

        openPropertiesAndActivateOtherTab();
        bot.checkBox(PreferenceConstants.TEXT_EXTENDED_TEST_METHOD_SEARCH).deselect();
        saveAndCloseProps();
        assertFalse(Preferences.getInstance().getMethodSearchMode(getJavaProjectFromContext()).searchByCall);
        assertTrue(Preferences.getInstance().getMethodSearchMode(getJavaProjectFromContext()).searchByName);
    }

    protected void openPropertiesAndActivateOtherTab()
    {
        openProjectPropertiesAndSelectMoreUnitPage();
        bot.checkBox("Use project specific settings").select();
        bot.tabItem("Other").activate();
    }
    
    @Test
    public void should_DisabledUI_when_no_project_specific_settings_used() throws Exception
    {
        openProjectPropertiesAndSelectMoreUnitPage();
        
        assertTrue(bot.button("Add").isEnabled());
        assertFalse(bot.button("Remove").isEnabled());
        assertFalse(bot.button("Remap").isEnabled());
        assertTrue(bot.tree(1).isEnabled());
        
        bot.checkBox("Use project specific settings").deselect();
        assertFalse(bot.button("Add").isEnabled());
        assertFalse(bot.button("Remove").isEnabled());
        assertFalse(bot.button("Remap").isEnabled());
        assertFalse(bot.tree(1).isEnabled());
        
        bot.checkBox("Use project specific settings").select();
        bot.tree(1).select(0);
        assertTrue(bot.button("Add").isEnabled());
        assertTrue(bot.button("Remove").isEnabled());
        assertTrue(bot.button("Remap").isEnabled());
        assertTrue(bot.tree(1).isEnabled());
        
        saveAndCloseProps();
    }

}
