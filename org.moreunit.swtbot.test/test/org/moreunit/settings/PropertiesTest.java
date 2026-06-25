package org.moreunit.settings;

import static org.eclipse.swtbot.swt.finder.waits.Conditions.waitForShell;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.junit5.SWTBotJunit5Extension;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.waits.WaitForObjectCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.moreunit.JavaProjectSWTBotTestHelper;
import org.moreunit.preferences.PreferenceConstants;
import org.moreunit.preferences.Preferences;
import org.moreunit.test.context.Project;

@Project(mainCls = "org:HelloWorld", mainSrcFolder = "src", testSrcFolder = "test")
@org.moreunit.test.context.Preferences(testSrcFolder = "junit")
@ExtendWith(SWTBotJunit5Extension.class)
public class PropertiesTest extends JavaProjectSWTBotTestHelper
{
    private void openProjectPropertiesAndSelectMoreUnitPage()
    {
        SWTBotTreeItem projectItem = selectAndReturnJavaProjectFromPackageExplorer();
        getShortcutStrategy().openProperties(projectItem);

        WaitForObjectCondition<Shell> waitForShell = waitForShell(shellWithTextStartingWith("Properties for "+projectItem.getText()));
        bot.waitUntil(waitForShell);
        setShellInFocus(waitForShell.get(0));

        bot.waitUntil(org.eclipse.swtbot.swt.finder.waits.Conditions.shellIsActive("Properties for "+projectItem.getText()));

        bot.tree().expandNode("MoreUnit").select("Java");
    }

    private void setShellInFocus(final Shell shell)
    {
        UIThreadRunnable.syncExec(new VoidResult()
        {
            @Override
            public void run()
            {
                    shell.forceFocus();
                    shell.forceActive();
            }
        });
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
        bot.waitUntil(org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses(addFolderDialog));
        propertiesDialogShell.setFocus();
        propertiesDialogShell.activate();
        bot.waitUntil(org.eclipse.swtbot.swt.finder.waits.Conditions.shellIsActive(propertiesDialogShell.getText()));
        saveAndCloseProps();
        assertTrue(Preferences.getInstance().hasProjectSpecificSettings(getJavaProjectFromContext()));
    }

    private void saveAndCloseProps()
    {
        // in newer version (at least 4.8), the label has been changed and is stored in a preference
        String label = JFaceResources.getString("PreferencesDialog.okButtonLabel");
        String realLabel = "PreferencesDialog.okButtonLabel".equals(label)? "OK" : label;
        SWTBotShell shellToClose = bot.activeShell();
        bot.button(realLabel).click();
        bot.waitUntil(org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses(shellToClose));
    }

    @BeforeEach
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
        assertEquals(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_3, testType);

        openPropertiesAndActivateOtherTab();
        bot.radio(PreferenceConstants.TEXT_JUNIT_4).click();
        saveAndCloseProps();
        testType = Preferences.getInstance().getTestType(getJavaProjectFromContext());
        assertEquals(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_4, testType);

        openPropertiesAndActivateOtherTab();
        bot.radio(PreferenceConstants.TEXT_JUNIT_5).click();
        saveAndCloseProps();
        testType = Preferences.getInstance().getTestType(getJavaProjectFromContext());
        assertEquals(PreferenceConstants.TEST_TYPE_VALUE_JUNIT_5, testType);

        openPropertiesAndActivateOtherTab();
        bot.radio(PreferenceConstants.TEXT_SPOCK).click();
        saveAndCloseProps();
        testType = Preferences.getInstance().getTestType(getJavaProjectFromContext());
        assertEquals(PreferenceConstants.TEST_TYPE_VALUE_SPOCK, testType);

        openPropertiesAndActivateOtherTab();
        bot.radio(PreferenceConstants.TEXT_TEST_NG).click();
        saveAndCloseProps();
        testType = Preferences.getInstance().getTestType(getJavaProjectFromContext());
        assertEquals(PreferenceConstants.TEST_TYPE_VALUE_TESTNG, testType);
    }

    @Test
    public void should_update_test_method_prefix_when_property_changes()
    {
        openPropertiesAndActivateOtherTab();
        bot.radio(PreferenceConstants.TEXT_JUNIT_4).click();
        bot.checkBox(PreferenceConstants.TEXT_TEST_METHOD_TYPE).select();
        saveAndCloseProps();
        String testMethodType = Preferences.getInstance().getTestMethodType(getJavaProjectFromContext());
        assertEquals(PreferenceConstants.TEST_METHOD_TYPE_JUNIT3, testMethodType);

        openPropertiesAndActivateOtherTab();
        bot.checkBox(PreferenceConstants.TEXT_TEST_METHOD_TYPE).deselect();
        saveAndCloseProps();
        testMethodType = Preferences.getInstance().getTestMethodType(getJavaProjectFromContext());
        assertEquals(PreferenceConstants.TEST_METHOD_TYPE_NO_PREFIX, testMethodType);
    }

    @Test
    public void should_update_test_method_content_when_property_changes()
    {
        openPropertiesAndActivateOtherTab();
        bot.textWithLabel(PreferenceConstants.TEXT_TEST_METHOD_CONTENT).setText("blubberContent");
        saveAndCloseProps();
        String testMethodDefaultContent = Preferences.getInstance().getTestMethodDefaultContent(getJavaProjectFromContext());
        assertEquals("blubberContent", testMethodDefaultContent);
    }

    @Test
    public void should_update_test_prefixes_when_preferences_change()
    {
        openPropertiesAndActivateOtherTab();
        bot.textWithLabel("Pattern:").setText("${srcFile}(Dest|ITDest)");
        saveAndCloseProps();
        String template = Preferences.forProject(getJavaProjectFromContext()).getTestClassNameTemplate();
        assertEquals("${srcFile}(Dest|ITDest)", template);
    }

    @Test
    public void should_update_test_package_prefix_when_preferences_change()
    {
        openPropertiesAndActivateOtherTab();
        bot.textWithLabel(PreferenceConstants.TEXT_PACKAGE_PREFIX).setText("pckgpref");
        saveAndCloseProps();
        String testPackagePrefix = Preferences.getInstance().getTestPackagePrefix(getJavaProjectFromContext());
        assertEquals("pckgpref", testPackagePrefix);
    }

    @Test
    public void should_update_test_package_suffix_when_preferences_change()
    {
        openPropertiesAndActivateOtherTab();
        bot.textWithLabel(PreferenceConstants.TEXT_PACKAGE_SUFFIX).setText("pckgsuff");
        saveAndCloseProps();
        String testPackageSuffix = Preferences.getInstance().getTestPackageSuffix(getJavaProjectFromContext());
        assertEquals("pckgsuff", testPackageSuffix);
    }

    @Test
    public void should_update_test_superclass_when_preferences_change()
    {
        openPropertiesAndActivateOtherTab();
        bot.textWithLabel(PreferenceConstants.TEXT_TEST_SUPERCLASS).setText("org.moreunit.SuperKlass");
        saveAndCloseProps();
        String testSuperClass = Preferences.getInstance().getTestSuperClass(getJavaProjectFromContext());
        assertEquals("org.moreunit.SuperKlass", testSuperClass);
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
        bot.cTabItem("Other").activate();
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
