package org.moreunit.refactoring;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swtbot.swt.finder.junit5.SWTBotJunit5Extension;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.moreunit.JavaProjectSWTBotTestHelper;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.Project;
import org.moreunit.test.context.TestType;
import org.moreunit.test.workspace.WorkspaceHelper;

@Preferences(testClassNameTemplate = "${srcFile}Test",
             testSrcFolder = "test",
             testType = TestType.JUNIT4)
@ExtendWith(SWTBotJunit5Extension.class)
public class MoveClassTest extends JavaProjectSWTBotTestHelper
{
	@Project(
            mainCls = "org:SomeClass",
            testCls = "org:SomeClassTest",
            mainSrcFolder = "src",
            testSrcFolder = "test")
	@Test
	public void should_move_test_when_cut_gets_moved() throws JavaModelException
	{
		moveSomeClassFromOrgtoComPackageAndWaitUntilFinished();
		assertNotNull(context.getCompilationUnit("com.SomeClassTest"));
	}

	protected void moveSomeClassFromOrgtoComPackageAndWaitUntilFinished() throws JavaModelException
	{
		WorkspaceHelper.createNewPackageInSourceFolder(context.getProjectHandler().getMainSrcFolderHandler().get(), "com");
		SWTBotTreeItem packageItem = selectAndReturnPackageWithName("org");
		packageItem.expand();
		packageItem.getNode("SomeClass.java").select();
		getShortcutStrategy().pressMoveShortcut();
		bot.waitUntil(Conditions.shellIsActive("Move"));
		SWTBotTreeItem projectItem = bot.tree().getTreeItem(context.getProjectHandler().getName());
		projectItem.getNode("src").getNode("com").select();
		SWTBotShell moveDialog = bot.activeShell();
		bot.button("OK").click();
		bot.waitUntil(Conditions.shellCloses(moveDialog), 20000);
	}

	@Project(
            mainCls = "org:SomeClass",
            testCls = "de:SomeClassTest",
            mainSrcFolder = "src",
            testSrcFolder = "test")
	@Test
	public void should_move_prefect_match_only_when_cut_gets_moved() throws JavaModelException
	{
		moveSomeClassFromOrgtoComPackageAndWaitUntilFinished();
		assertNotNull(context.getCompilationUnit("de.SomeClassTest"));
	}
}
