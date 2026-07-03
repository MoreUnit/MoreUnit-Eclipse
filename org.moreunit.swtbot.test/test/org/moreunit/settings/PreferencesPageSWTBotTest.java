package org.moreunit.settings;

import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellIsActive;

import org.eclipse.swtbot.swt.finder.junit5.SWTBotJunit5Extension;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.moreunit.JavaProjectSWTBotTestHelper;
import org.moreunit.test.context.Project;
import org.moreunit.test.context.Properties;
import org.moreunit.test.context.TestType;

@Project(mainCls = "org:Dummy")
@ExtendWith(SWTBotJunit5Extension.class)
public class PreferencesPageSWTBotTest extends JavaProjectSWTBotTestHelper
{
    @org.junit.jupiter.api.BeforeEach
    public void prepareWorkbench()
    {
        try { bot.shell("Preferences").close(); } catch (Exception e) { }
        try
        {
            selectAndReturnJavaProjectFromPackageExplorer();
        }
        catch (Exception e)
        {
        }
    }
    @Test
    public void should_validate_pattern_field()
    {
        getShortcutStrategy().openPreferences();
        bot.waitUntil(shellIsActive("Preferences"));

        waitForMoreUnitInTree();
        bot.tree().expandNode("MoreUnit").select("Java");

        // Wait for the page to load
        waitForPageField();

        // Type an invalid pattern (missing ${srcFile} variable)
        bot.text(0).setText("InvalidPattern");
        waitForValidationError("The rule for naming test files must use the variable");

        // Type a valid pattern back
        bot.text(0).setText("${srcFile}Test");
        waitForValidationCleared();

        bot.button("Cancel").click();
    }

    @Test
    public void should_detect_empty_pattern()
    {
        getShortcutStrategy().openPreferences();
        bot.waitUntil(shellIsActive("Preferences"));

        waitForMoreUnitInTree();
        bot.tree().expandNode("MoreUnit").select("Java");
        waitForPageField();

        // Empty pattern should show validation error
        bot.text(0).setText("");
        waitForValidationError("You must enter a rule for naming test files");

        bot.button("Cancel").click();
    }

    @Test
    public void should_warn_on_multiple_wildcards()
    {
        getShortcutStrategy().openPreferences();
        bot.waitUntil(shellIsActive("Preferences"));

        waitForMoreUnitInTree();
        bot.tree().expandNode("MoreUnit").select("Java");
        waitForPageField();

        // Multiple wildcards should generate a warning
        bot.text(0).setText("${srcFile}*_*_Test");
        waitForWarningVisible();

        bot.button("Cancel").click();
    }

    private void waitForMoreUnitInTree()
    {
        bot.waitUntil(new DefaultCondition() {
            @Override public boolean test() {
                try { return bot.tree().getTreeItem("MoreUnit") != null; }
                catch (Exception e) { return false; }
            }
            @Override public String getFailureMessage() {
                return "MoreUnit not found in Preferences tree";
            }
        }, 10000);
    }

    private void waitForPageField()
    {
        bot.waitUntil(new DefaultCondition() {
            @Override public boolean test() {
                try { return bot.label("Pattern:").isVisible(); }
                catch (Exception e) { return false; }
            }
            @Override public String getFailureMessage() {
                return "Java page did not show pattern field";
            }
        }, 10000);
    }

    private void waitForValidationError(String expectedMessage)
    {
        bot.waitUntil(new DefaultCondition() {
            @Override public boolean test() {
                try { return bot.label(expectedMessage).isVisible(); }
                catch (Exception e) { return false; }
            }
            @Override public String getFailureMessage() {
                return "Validation error did not appear: " + expectedMessage;
            }
        }, 5000);
    }

    private void waitForValidationCleared()
    {
        bot.waitUntil(new DefaultCondition() {
            @Override public boolean test() {
                try {
                    bot.label("The rule for naming test files must use the variable");
                    return false;
                } catch (Exception e) { return true; }
            }
            @Override public String getFailureMessage() {
                return "Validation error did not clear";
            }
        }, 5000);
    }

    private void waitForWarningVisible()
    {
        bot.waitUntil(new DefaultCondition() {
            @Override public boolean test() {
                try {
                    return bot.label("Using too many wildcards may degrade search performance and results!").isVisible();
                } catch (Exception e) { return false; }
            }
            @Override public String getFailureMessage() {
                return "Warning about multiple wildcards did not appear";
            }
        }, 5000);
    }
}
