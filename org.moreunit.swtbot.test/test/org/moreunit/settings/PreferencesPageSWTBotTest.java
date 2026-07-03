package org.moreunit.settings;

import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellIsActive;

import org.eclipse.swtbot.swt.finder.junit5.SWTBotJunit5Extension;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.moreunit.JavaProjectSWTBotTestHelper;

@ExtendWith(SWTBotJunit5Extension.class)
public class PreferencesPageSWTBotTest extends JavaProjectSWTBotTestHelper
{
    @Test
    public void should_open_java_language_preference_page()
    {
        try { bot.shell("Preferences").close(); } catch (Exception e) { }

        getShortcutStrategy().openPreferences();
        bot.waitUntil(shellIsActive("Preferences"));

        bot.waitUntil(new DefaultCondition() {
            @Override public boolean test() {
                try { return bot.tree().getTreeItem("MoreUnit") != null; }
                catch (Exception e) { return false; }
            }
            @Override public String getFailureMessage() {
                return "MoreUnit not found in Preferences tree";
            }
        }, 10000);

        bot.tree().expandNode("MoreUnit").select("Java");

        bot.waitUntil(new DefaultCondition() {
            @Override public boolean test() {
                try { return bot.label("Pattern:").isVisible(); }
                catch (Exception e) { return false; }
            }
            @Override public String getFailureMessage() {
                return "Java page did not show pattern field";
            }
        }, 10000);

        bot.button("Cancel").click();
    }
}
