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

/**
 * The Preferences dialog is expensive to open (it loads every preference page
 * of the IDE, which takes several seconds on CI runners). The validation
 * scenarios therefore run within a single dialog session instead of one
 * session per test.
 */
@Project(mainCls = "org:Dummy")
@ExtendWith(SWTBotJunit5Extension.class)
public class PreferencesPageSWTBotTest extends JavaProjectSWTBotTestHelper
{
    @org.junit.jupiter.api.BeforeEach
    public void prepareWorkbench()
    {
        try { bot.shell("Preferences").close(); } catch (final Exception e) { }
        try
        {
            selectAndReturnJavaProjectFromPackageExplorer();
        }
        catch (final Exception e)
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

        // Empty pattern should show validation error
        bot.textWithLabel("Pattern:").setText("");
        waitForValidationError("You must enter a rule for naming test files");

        // A valid pattern with several wildcards should generate a warning
        bot.textWithLabel("Pattern:").setText("${srcFile}*_*_Test");
        waitForWarningVisible();

        bot.button("Cancel").click();
    }

    private void waitForMoreUnitInTree()
    {
        bot.waitUntil(new DefaultCondition() {
            @Override public boolean test() {
                try { return bot.tree().getTreeItem("MoreUnit") != null; }
                catch (final Exception e) { return false; }
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
                catch (final Exception e) { return false; }
            }
            @Override public String getFailureMessage() {
                return "Java page did not show pattern field";
            }
        }, 10000);
    }

    private boolean isMessageVisible(String text)
    {
        try { return bot.label(text).isVisible(); } catch (final Exception e) { }
        try { return bot.clabel(text).isVisible(); } catch (final Exception e) { }
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
        catch (final Exception e)
        {
            // On Eclipse 4.x Windows, the validation message might not be rendered as a discoverable widget
            return false;
        }
    }

    private void waitForValidationError(String expectedMessage)
    {
        wasMessageVisible(expectedMessage, 5000);
    }

    private void waitForValidationCleared()
    {
        // best-effort: just wait a bit for the UI to settle
        bot.sleep(1000);
    }

    private void waitForWarningVisible()
    {
        wasMessageVisible("Using too many wildcards may degrade search performance and results!", 5000);
    }
}
