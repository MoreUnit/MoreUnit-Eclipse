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
        bot.menu("Window").menu("Preferences").click();
        bot.waitUntil(shellIsActive("Preferences"));

        bot.tree().expandNode("MoreUnit").select("Java");

        bot.waitUntil(new DefaultCondition()
        {
            @Override
            public boolean test()
            {
                try
                {
                    return bot.label("Pattern:").isVisible();
                }
                catch (Exception e)
                {
                    return false;
                }
            }
            @Override
            public String getFailureMessage()
            {
                return "Java language preference page did not show the pattern field";
            }
        }, 10000);

        bot.button("Cancel").click();
    }

    @Test
    public void should_open_other_languages_preference_page()
    {
        bot.menu("Window").menu("Preferences").click();
        bot.waitUntil(shellIsActive("Preferences"));

        bot.tree().expandNode("MoreUnit").select("Other");

        bot.waitUntil(new DefaultCondition()
        {
            @Override
            public boolean test()
            {
                try
                {
                    return bot.label("Additional file extensions:").isVisible();
                }
                catch (Exception e)
                {
                    return false;
                }
            }
            @Override
            public String getFailureMessage()
            {
                return "Other languages preference page did not show the additional file extensions field";
            }
        }, 10000);

        bot.button("Cancel").click();
    }
}
