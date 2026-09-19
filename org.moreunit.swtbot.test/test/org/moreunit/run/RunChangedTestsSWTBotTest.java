package org.moreunit.run;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.junit5.SWTBotJunit5Extension;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.moreunit.JavaProjectSWTBotTestHelper;
import org.moreunit.test.context.Project;
import org.moreunit.test.context.Properties;
import org.moreunit.test.context.TestType;

@ExtendWith(SWTBotJunit5Extension.class)
public class RunChangedTestsSWTBotTest extends JavaProjectSWTBotTestHelper
{
    @BeforeEach
    public void closeEditors()
    {
        for (final SWTBotEditor editor : bot.editors())
        {
            editor.close();
        }
    }

    /**
     * Only the presence of the command is asserted here: whether the test
     * workspace belongs to a Git working tree with changed files depends on
     * the machine running the tests.
     */
    @Test
    @Project(
            mainCls = "org:SomeClass",
            testCls = "org:SomeClassTest",
            properties = @Properties(
                    testType = TestType.JUNIT5,
                    testClassNameTemplate = "${srcFile}Test"))
    public void should_offer_running_tests_for_changed_files_on_a_java_project()
    {
        final SWTBotTreeItem projectNode = selectAndReturnJavaProjectFromPackageExplorer();

        final SWTBotMenu moreUnitMenu = projectNode.contextMenu("MoreUnit");
        assertTrue(moreUnitMenu.menu("Run Tests for Changed Files").isVisible());
        moreUnitMenu.hide();
    }
}
