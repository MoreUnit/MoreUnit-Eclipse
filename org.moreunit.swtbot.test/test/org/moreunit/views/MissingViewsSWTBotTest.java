package org.moreunit.views;

import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellIsActive;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.junit5.SWTBotJunit5Extension;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.moreunit.JavaProjectSWTBotTestHelper;
import org.moreunit.test.context.Project;
import org.moreunit.test.context.Properties;
import org.moreunit.test.context.TestType;

@ExtendWith(SWTBotJunit5Extension.class)
public class MissingViewsSWTBotTest extends JavaProjectSWTBotTestHelper
{
    @BeforeEach
    public void closeEditors()
    {
        for (var editor : bot.editors())
            editor.close();
    }

    @Test
    @Project(
            mainCls = "org:SomeUntestedClass",
            properties = @Properties(
                    testType = TestType.JUNIT5,
                    testClassNameTemplate = "${srcFile}Test"))
    public void should_open_missing_tests_per_project_view()
    {
        selectAndReturnJavaProjectFromPackageExplorer();

        bot.menu("Window").menu("Show View").menu("Other...").click();
        bot.waitUntil(shellIsActive("Show View"));

        bot.text(0).setText("Missing Tests per Project");
        bot.waitUntil(new DefaultCondition()
        {
            @Override
            public boolean test()
            {
                return bot.tree().rowCount() > 0;
            }
            @Override
            public String getFailureMessage()
            {
                return "Missing Tests per Project view not found in Show View dialog";
            }
        }, 10000);

        bot.tree().expandNode("MoreUnit").select("Missing Tests per Project");
        bot.button("Open").click();

        bot.waitUntil(new DefaultCondition()
        {
            @Override
            public boolean test()
            {
                try
                {
                    ((org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot)bot).viewByTitle("Missing Tests per Project");
                    return true;
                }
                catch (WidgetNotFoundException e)
                {
                    return false;
                }
            }
            @Override
            public String getFailureMessage()
            {
                return "Missing Tests per Project view did not open";
            }
        }, 10000);
    }

    @Test
    @Project(
            mainSrc = "MyClassWithMethods.txt",
            testSrc = "MyClassWithMethodsTest.txt",
            properties = @Properties(
                    testType = TestType.JUNIT5,
                    testClassNameTemplate = "${srcFile}Test"))
    public void should_open_missing_test_methods_view()
    {
        selectAndReturnJavaProjectFromPackageExplorer();

        bot.menu("Window").menu("Show View").menu("Other...").click();
        bot.waitUntil(shellIsActive("Show View"));

        bot.text(0).setText("Missing Test Methods");
        bot.waitUntil(new DefaultCondition()
        {
            @Override
            public boolean test()
            {
                return bot.tree().rowCount() > 0;
            }
            @Override
            public String getFailureMessage()
            {
                return "Missing Test Methods view not found in Show View dialog";
            }
        }, 10000);

        bot.tree().expandNode("MoreUnit").select("Missing Test Methods");
        bot.button("Open").click();

        bot.waitUntil(new DefaultCondition()
        {
            @Override
            public boolean test()
            {
                try
                {
                    ((org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot)bot).viewByTitle("Missing Test Methods");
                    return true;
                }
                catch (WidgetNotFoundException e)
                {
                    return false;
                }
            }
            @Override
            public String getFailureMessage()
            {
                return "Missing Test Methods view did not open";
            }
        }, 10000);
    }
}
