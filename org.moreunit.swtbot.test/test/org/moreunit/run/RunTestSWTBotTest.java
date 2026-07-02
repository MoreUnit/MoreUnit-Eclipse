package org.moreunit.run;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.swt.SWT;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.junit5.SWTBotJunit5Extension;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.moreunit.JavaProjectSWTBotTestHelper;
import org.moreunit.test.context.Project;
import org.moreunit.test.context.Properties;
import org.moreunit.test.context.TestType;

@ExtendWith(SWTBotJunit5Extension.class)
public class RunTestSWTBotTest extends JavaProjectSWTBotTestHelper
{
    @BeforeEach
    public void closeEditors()
    {
        for (SWTBotEditor editor : bot.editors())
        {
            editor.close();
        }
    }

    @Test
    @Project(
            mainSrc = "Calculator.txt",
            testSrc = "CalculatorTest.txt",
            properties = @Properties(
                    testType = TestType.JUNIT5,
                    testClassNameTemplate = "${srcFile}Test"))
    public void should_launch_corresponding_test_when_run_shortcut_pressed_in_cut()
    {
        int launchesBefore = DebugPlugin.getDefault().getLaunchManager().getLaunches().length;

        openResource("Calculator.java");
        // Ctrl+R is bound to org.moreunit.runtestaction in the java editor scope
        KeyboardFactory.getSWTKeyboard().pressShortcut(SWT.CTRL, 'r');

        bot.waitUntil(new DefaultCondition()
        {
            @Override
            public boolean test() throws Exception
            {
                ILaunch[] launches = DebugPlugin.getDefault().getLaunchManager().getLaunches();
                return launches.length > launchesBefore;
            }

            @Override
            public String getFailureMessage()
            {
                return "A test launch was not created after pressing the Run Test shortcut (Ctrl+R)";
            }
        }, 30000);
    }
}
