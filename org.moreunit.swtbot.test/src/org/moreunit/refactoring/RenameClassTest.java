package org.moreunit.refactoring;

import static org.fest.assertions.Assertions.assertThat;

import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Test;
import org.moreunit.JavaProjectSWTBotTestHelper;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.Project;
import org.moreunit.test.context.TestType;


@Preferences(testClassSuffixes="Test", 
             testSrcFolder="test", 
             testType=TestType.JUNIT4)
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
	
	private void renameSomeClassToAnyClassAndWaitUntilFinished() 
	{
		SWTBotTreeItem packageItem = selectAndReturnPackageWithName("org");
		packageItem.expand();
		packageItem.getNode("SomeClass.java").select();
		getShortcutStrategy().pressRenameShortcut();
		bot.textWithLabel("New name:").setText("AnyClass");
		bot.button("Finish").click();
		SWTBotShell renameDialog = bot.activeShell();
		bot.waitUntil(Conditions.shellCloses(renameDialog), 20000);
	}
	
}
