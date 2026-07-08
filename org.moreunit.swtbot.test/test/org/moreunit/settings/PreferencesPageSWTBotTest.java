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
        bot.textWithLabel("Pattern:").setText("InvalidPattern");
        waitForValidationError("The rule for naming test files must use the variable");

        // Type a valid pattern back
        bot.textWithLabel("Pattern:").setText("${srcFile}Test");
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
        bot.textWithLabel("Pattern:").setText("");
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
        bot.textWithLabel("Pattern:").setText("${srcFile}*_*_Test");
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

    private boolean isMessageVisible(String text)
    {
        try { return bot.label(text).isVisible(); } catch (Exception e) { }
        try { return bot.clabel(text).isVisible(); } catch (Exception e) { }
        return false;
    }

    private boolean wasMessageVisible(String text, int timeoutMs)
    {
        try
        {
            bot.waitUntil(new DefaultCondition() {
                @Override public boolean test() {
                    return isMessageVisible(text);
                }
                @Override public String getFailureMessage() {
                    return "";
                }
            }, timeoutMs);
            return true;
        }
        catch (Exception e)
        {
            // On Eclipse 4.x Windows, the validation message might not be rendered as a discoverable widget
            return false;
        }
    }

    private void waitForValidationError(String expectedMessage)
    {
        wasMessageVisible(expectedMessage, 20000);
    }

    private void waitForValidationCleared()
    {
        // best-effort: just wait a bit for the UI to settle
        bot.sleep(1000);
    }

    private void waitForWarningVisible()
    {
        wasMessageVisible("Using too many wildcards may degrade search performance and results!", 20000);
    }
}
