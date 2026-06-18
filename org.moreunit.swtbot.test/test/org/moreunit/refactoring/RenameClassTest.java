package org.moreunit.refactoring;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.swtbot.swt.finder.junit5.SWTBotJunit5Extension;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.moreunit.JavaProjectSWTBotTestHelper;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.Project;
import org.moreunit.test.context.TestType;


@Preferences(testClassNameTemplate="${srcFile}Test",
             testSrcFolder="test",
             testType=TestType.JUNIT4)
@ExtendWith(SWTBotJunit5Extension.class)
public class RenameClassTest extends JavaProjectSWTBotTestHelper
{
	@Project(
            mainCls = "org:SomeClass",
            testCls = "org:SomeClassTest",
            mainSrcFolder = "src",
            testSrcFolder = "test")
	@Test
	public void should_rename_test_when_cut_gets_renamed()
	{
		renameSomeClassToAnyClassAndWaitUntilFinished();
		assertThat(context.getCompilationUnit("org.AnyClassTest")).isNotNull();
	}


	@Project(
            mainCls = "org:SomeClass",
            testCls = "com:SomeClassTest",
            mainSrcFolder = "src",
            testSrcFolder = "test")
	@Test
	public void should_rename_only_perfect_match_test_when_cut_gets_renamed()
	{
		renameSomeClassToAnyClassAndWaitUntilFinished();
		assertThat(context.getCompilationUnit("com.SomeClassTest")).isNotNull();
	}

	@Project(
            mainCls = "org:SomeClass",
            testCls = "org:SomeTest",
            mainSrcFolder = "src",
            testSrcFolder = "test")
	@Test
	public void should_not_rename_test_if_cut_name_not_part_of_test_name()
	{
		renameSomeClassToAnyClassAndWaitUntilFinished();

		// Ensure SomeTest wasn't renamed because "SomeClass" isn't in its name.
		// (The "index == -1" condition in RenameClassParticipant).
		assertThat(context.getCompilationUnit("org.SomeTest")).isNotNull();
	}

	@Project(
            mainCls = "org:OtherClass",
            testCls = "org:OtherClassTest",
            mainSrcFolder = "src",
            testSrcFolder = "test")
	@Test
	public void should_rename_test_using_regex_fallback_when_cut_gets_renamed()
	{
		renameOtherClassToAnyClassAndWaitUntilFinished();

		assertThat(context.getCompilationUnit("org.AnyClassTest")).isNotNull();
	}

	private void renameSomeClassToAnyClassAndWaitUntilFinished()
	{
		final SWTBotTreeItem packageItem = selectAndReturnPackageWithName("org");
		packageItem.expand();
		packageItem.getNode("SomeClass.java").select();
		bot.waitUntil(new DefaultCondition()
        {

            @Override
            public boolean test() throws Exception
            {
                return packageItem.getNode("SomeClass.java").isSelected();
            }

            @Override
            public String getFailureMessage()
            {
                return "Node SomeClass.java not selected";
            }
        });
		getShortcutStrategy().pressRenameShortcut();
		bot.textWithLabel("New name:").setText("AnyClass");
		bot.button("Finish").click();
		SWTBotShell renameDialog = bot.activeShell();
		bot.waitUntil(org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses(renameDialog), 20000);
	}

	private void renameOtherClassToAnyClassAndWaitUntilFinished()
	{
		final SWTBotTreeItem packageItem = selectAndReturnPackageWithName("org");
		packageItem.expand();
		packageItem.getNode("OtherClass.java").select();
		bot.waitUntil(new DefaultCondition()
        {

            @Override
            public boolean test() throws Exception
            {
                return packageItem.getNode("OtherClass.java").isSelected();
            }

            @Override
            public String getFailureMessage()
            {
                return "Node OtherClass.java not selected";
            }
        });
		getShortcutStrategy().pressRenameShortcut();
		bot.textWithLabel("New name:").setText("AnyClass");
		bot.button("Finish").click();
		SWTBotShell renameDialog = bot.activeShell();
		bot.waitUntil(org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses(renameDialog), 20000);
	}

}
